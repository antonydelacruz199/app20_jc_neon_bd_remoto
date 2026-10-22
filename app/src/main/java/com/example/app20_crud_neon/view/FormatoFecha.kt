package com.example.app19_jc_neon_bd_remoto.view

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// El DatePicker de Material 3 trabaja en milisegundos UTC
private fun formatoIsoUtc() = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

/** Milisegundos del DatePicker -> "2003-07-22" */
fun millisAIso(millis: Long): String = formatoIsoUtc().format(Date(millis))

/** "2003-07-22" -> milisegundos para abrir el DatePicker en esa fecha */
fun isoAMillis(iso: String): Long? =
    try {
        formatoIsoUtc().parse(iso)?.time
    } catch (e: Exception) {
        null
    }

/** "2003-07-22" -> "22/07/2003" */
fun formatearFecha(iso: String): String {
    val partes = iso.split("-")
    return if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else ""
}

/** Edad en años cumplidos a partir de "AAAA-MM-DD" */
fun calcularEdad(iso: String): Int? {
    val partes = iso.split("-").mapNotNull { it.toIntOrNull() }
    if (partes.size != 3) return null
    val (anio, mes, dia) = partes
    val hoy = Calendar.getInstance()
    val mesActual = hoy.get(Calendar.MONTH) + 1
    val diaActual = hoy.get(Calendar.DAY_OF_MONTH)
    var edad = hoy.get(Calendar.YEAR) - anio
    if (mesActual < mes || (mesActual == mes && diaActual < dia)) edad--
    return edad
}