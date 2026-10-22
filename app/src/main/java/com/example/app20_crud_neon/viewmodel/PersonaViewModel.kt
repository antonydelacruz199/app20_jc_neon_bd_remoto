package com.example.app19_jc_neon_bd_remoto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app19_jc_neon_bd_remoto.model.Persona
import com.example.app19_jc_neon_bd_remoto.model.PersonaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PersonaViewModel(
    private val repository: PersonaRepository = PersonaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonaUiState())
    val uiState: StateFlow<PersonaUiState> = _uiState.asStateFlow()

    // La pantalla pide la lista después del permiso de red local.
    // Neon no es tiempo real: se vuelve a pedir la lista después de cada cambio.
    private var cargaId = 0

    fun cargarPersonas() {
        val consulta = _uiState.value.busqueda.trim()
        val id = ++cargaId
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            repository.listar(consulta)
                .onSuccess { lista ->
                    if (id != cargaId) return@onSuccess
                    _uiState.update { it.copy(personas = lista, cargando = false) }
                }
                .onFailure { e ->
                    if (id != cargaId) return@onFailure
                    _uiState.update { it.copy(cargando = false, mensaje = e.message) }
                }
        }
    }

    fun onBusquedaChange(valor: String) {
        _uiState.update { it.copy(busqueda = valor) }
        cargarPersonas()
    }

    fun onNombresChange(valor: String) = _uiState.update { it.copy(nombres = valor) }

    fun onApellidosChange(valor: String) = _uiState.update { it.copy(apellidos = valor) }

    fun onFechaNacimientoChange(valor: String) = _uiState.update { it.copy(fechaNacimiento = valor) }

    fun onDniChange(valor: String) {
        if (valor.length <= 8 && valor.all { it.isDigit() }) {
            _uiState.update { it.copy(dni = valor) }
        }
    }

    fun onCorreoChange(valor: String) {
        if (valor.length <= 120 && valor.none { it.isWhitespace() }) {
            _uiState.update { it.copy(correo = valor) }
        }
    }

    fun guardar() {
        val estado = _uiState.value
        val error = validar(estado)
        if (error != null) {
            mostrarMensaje(error)
            return
        }

        val editando = estado.personaEditando
        val persona = Persona(
            id = editando?.id ?: 0,
            nombres = estado.nombres.trim(),
            apellidos = estado.apellidos.trim(),
            fechaNacimiento = estado.fechaNacimiento,
            dni = estado.dni,
            correo = estado.correo.trim().lowercase()
        )

        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true) }
            val resultado =
                if (editando == null) repository.insertar(persona) else repository.actualizar(persona)

            resultado
                .onSuccess {
                    mostrarMensaje(if (editando == null) "Persona registrada" else "Persona actualizada")
                    limpiarFormulario()
                    cargarPersonas()
                }
                .onFailure { e -> mostrarMensaje(e.message ?: "No se pudo guardar") }

            _uiState.update { it.copy(guardando = false) }
        }
    }

    fun seleccionarParaEditar(persona: Persona) = _uiState.update {
        it.copy(
            personaEditando = persona,
            nombres = persona.nombres,
            apellidos = persona.apellidos,
            fechaNacimiento = persona.fechaNacimiento,
            dni = persona.dni,
            correo = persona.correo.orEmpty()
        )
    }

    fun eliminar(persona: Persona) {
        viewModelScope.launch {
            repository.eliminar(persona.id)
                .onSuccess {
                    if (_uiState.value.personaEditando?.id == persona.id) limpiarFormulario()
                    mostrarMensaje("Persona eliminada")
                    cargarPersonas()
                }
                .onFailure { e -> mostrarMensaje(e.message ?: "No se pudo eliminar") }
        }
    }

    fun limpiarFormulario() = _uiState.update {
        it.copy(
            nombres = "",
            apellidos = "",
            fechaNacimiento = "",
            dni = "",
            correo = "",
            personaEditando = null
        )
    }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    private fun mostrarMensaje(texto: String) = _uiState.update { it.copy(mensaje = texto) }

    // Validación en la app; la API vuelve a validar (nunca confiar solo en el cliente)
    private fun validar(estado: PersonaUiState): String? = when {
        estado.nombres.isBlank() -> "Ingresa los nombres"
        estado.apellidos.isBlank() -> "Ingresa los apellidos"
        estado.fechaNacimiento.isBlank() -> "Selecciona la fecha de nacimiento"
        estado.dni.length != 8 -> "El DNI debe tener 8 dígitos"
        estado.correo.isBlank() -> "Ingresa el correo"
        estado.correo.length > 120 || !CORREO_REGEX.matches(estado.correo.trim()) -> "Ingresa un correo válido"
        else -> null
    }

    companion object {
        private val CORREO_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    }
}