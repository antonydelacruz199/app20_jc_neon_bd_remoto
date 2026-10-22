package com.example.app19_jc_neon_bd_remoto.model

import com.google.gson.annotations.SerializedName

// Valores por defecto = constructor vacío para Gson.
// La fecha viaja como texto "AAAA-MM-DD", igual que la columna DATE de PostgreSQL.
data class Persona(
    val id: Int = 0,
    val nombres: String = "",
    val apellidos: String = "",
    @SerializedName("fecha_nacimiento")
    val fechaNacimiento: String = "",
    val dni: String = "",
    // Las filas antiguas vienen con correo null; Gson no usa el valor por defecto.
    val correo: String? = null
)