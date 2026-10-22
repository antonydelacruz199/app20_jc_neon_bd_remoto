const { Pool, types } = require('pg');

// El tipo DATE (OID 1082) se devuelve como texto "AAAA-MM-DD".
// Sin esto, pg lo convierte a Date y al pasar a JSON la fecha puede correrse un día por la zona horaria.
types.setTypeParser(1082, (valor) => valor);

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
});

module.exports = pool;