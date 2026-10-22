package com.example.app19_jc_neon_bd_remoto.viewmodel

import com.example.app19_jc_neon_bd_remoto.model.Persona

data class PersonaUiState(
    val personas: List<Persona> = emptyList(),
    val nombres: String = "",
    val apellidos: String = "",
    val fechaNacimiento: String = "",       // "AAAA-MM-DD"
    val dni: String = "",
    val correo: String = "",
    val busqueda: String = "",              // DNI o apellidos
    val personaEditando: Persona? = null,   // null = modo registrar
    val cargando: Boolean = false,
    val guardando: Boolean = false,
    val mensaje: String? = null
)