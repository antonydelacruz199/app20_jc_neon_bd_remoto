require('dotenv').config();
const express = require('express');
const cors = require('cors');
const pool = require('./db');

const app = express();
app.use(cors());
app.use(express.json());

const DNI_REGEX = /^\d{8}$/;
const FECHA_REGEX = /^\d{4}-\d{2}-\d{2}$/;
const CORREO_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const COLUMNAS = 'id, nombres, apellidos, fecha_nacimiento, dni, correo';

function validar(body) {
  const { nombres, apellidos, fecha_nacimiento, dni, correo } = body;
  if (!nombres || !nombres.trim()) return 'Ingresa los nombres';
  if (!apellidos || !apellidos.trim()) return 'Ingresa los apellidos';
  if (!FECHA_REGEX.test(fecha_nacimiento || '')) return 'La fecha debe tener el formato AAAA-MM-DD';
  const hoy = new Date().toISOString().slice(0, 10);
  if (fecha_nacimiento > hoy) return 'La fecha de nacimiento no puede ser futura';
  if (!DNI_REGEX.test(dni || '')) return 'El DNI debe tener 8 dígitos';
  const correoLimpio = (correo || '').trim();
  if (!correoLimpio) return 'Ingresa el correo';
  if (correoLimpio.length > 120 || !CORREO_REGEX.test(correoLimpio)) return 'Ingresa un correo válido';
  return null;
}

function responderError(res, e) {
  const detalle = `${e.constraint || ''} ${e.detail || ''}`;
  if (e.code === '23505') {
    const mensaje = detalle.includes('correo')
      ? 'Ya existe una persona con ese correo'
      : 'Ya existe una persona con ese DNI';
    return res.status(409).json({ mensaje });
  }
  if (e.code === '23514') {
    const mensaje = detalle.includes('correo') ? 'Ingresa un correo válido' : 'Datos no válidos';
    return res.status(400).json({ mensaje });
  }
  console.error(e);
  return res.status(500).json({ mensaje: 'Error en el servidor' });
}

function idValido(req, res) {
  const id = Number(req.params.id);
  if (!Number.isInteger(id) || id <= 0) {
    res.status(400).json({ mensaje: 'Id no válido' });
    return null;
  }
  return id;
}

app.get('/', (req, res) => res.json({ mensaje: 'API Personas - Neon' }));

// READ: lista. ?q= filtra por apellidos o DNI, sin distinguir mayúsculas.
app.get('/api/personas', async (req, res) => {
  const q = String(req.query.q || '').trim();
  const patron = q ? `%${q}%` : null;
  try {
    const { rows } = await pool.query(
      `SELECT ${COLUMNAS} FROM personas
       WHERE ($1::text IS NULL OR apellidos ILIKE $1 OR dni ILIKE $1)
       ORDER BY apellidos, nombres`,
      [patron]
    );
    res.json(rows);
  } catch (e) {
    responderError(res, e);
  }
});

// READ: una persona
app.get('/api/personas/:id', async (req, res) => {
  const id = idValido(req, res);
  if (id === null) return;
  try {
    const { rows } = await pool.query(
      `SELECT ${COLUMNAS} FROM personas WHERE id = $1`, [id]
    );
    if (rows.length === 0) return res.status(404).json({ mensaje: 'Persona no encontrada' });
    res.json(rows[0]);
  } catch (e) {
    responderError(res, e);
  }
});

// CREATE
app.post('/api/personas', async (req, res) => {
  const error = validar(req.body);
  if (error) return res.status(400).json({ mensaje: error });
  const { nombres, apellidos, fecha_nacimiento, dni, correo } = req.body;
  try {
    const { rows } = await pool.query(
      `INSERT INTO personas (nombres, apellidos, fecha_nacimiento, dni, correo)
       VALUES ($1, $2, $3, $4, $5)
       RETURNING ${COLUMNAS}`,
      [nombres.trim(), apellidos.trim(), fecha_nacimiento, dni, correo.trim().toLowerCase()]
    );
    res.status(201).json(rows[0]);
  } catch (e) {
    responderError(res, e);
  }
});

// UPDATE
app.put('/api/personas/:id', async (req, res) => {
  const id = idValido(req, res);
  if (id === null) return;
  const error = validar(req.body);
  if (error) return res.status(400).json({ mensaje: error });
  const { nombres, apellidos, fecha_nacimiento, dni, correo } = req.body;
  try {
    const { rows } = await pool.query(
      `UPDATE personas
       SET nombres = $1, apellidos = $2, fecha_nacimiento = $3, dni = $4, correo = $5
       WHERE id = $6
       RETURNING ${COLUMNAS}`,
      [nombres.trim(), apellidos.trim(), fecha_nacimiento, dni, correo.trim().toLowerCase(), id]
    );
    if (rows.length === 0) return res.status(404).json({ mensaje: 'Persona no encontrada' });
    res.json(rows[0]);
  } catch (e) {
    responderError(res, e);
  }
});

// DELETE
app.delete('/api/personas/:id', async (req, res) => {
  const id = idValido(req, res);
  if (id === null) return;
  try {
    const { rowCount } = await pool.query('DELETE FROM personas WHERE id = $1', [id]);
    if (rowCount === 0) return res.status(404).json({ mensaje: 'Persona no encontrada' });
    res.status(204).end();
  } catch (e) {
    responderError(res, e);
  }
});

// La columna y sus reglas viven en PostgreSQL. Se aplican al arrancar, también en Render.
async function asegurarCorreo() {
  await pool.query('ALTER TABLE personas ADD COLUMN IF NOT EXISTS correo VARCHAR(120)');
  await pool.query('CREATE UNIQUE INDEX IF NOT EXISTS personas_correo_key ON personas (correo)');
  await pool.query(`
    DO $$
    BEGIN
      IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'personas_correo_formato'
      ) THEN
        ALTER TABLE personas ADD CONSTRAINT personas_correo_formato
        CHECK (
          correo IS NULL OR (
            char_length(correo) <= 120
            AND correo ~ '^[^@[:space:]]+@[^@[:space:]]+\\.[^@[:space:]]+$'
          )
        );
      END IF;
    END $$
  `);
}

const PORT = process.env.PORT || 3000;
// 0.0.0.0 permite que el celular entre por la IP de la PC y que Render reciba tráfico.
asegurarCorreo()
  .then(() => {
    app.listen(PORT, '0.0.0.0', () => console.log(`API escuchando en el puerto ${PORT}`));
  })
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });