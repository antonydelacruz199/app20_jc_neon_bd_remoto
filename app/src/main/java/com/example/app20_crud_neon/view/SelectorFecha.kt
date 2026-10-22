package com.example.app19_jc_neon_bd_remoto.view

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorFecha(
    fechaIso: String,
    onFechaSeleccionada: (String) -> Unit,
    onCerrar: () -> Unit
) {
    val estado = rememberDatePickerState(
        initialSelectedDateMillis = isoAMillis(fechaIso),
        // No permite elegir fechas futuras
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis <= System.currentTimeMillis()
        }
    )

    DatePickerDialog(
        onDismissRequest = onCerrar,
        confirmButton = {
            TextButton(onClick = {
                estado.selectedDateMillis?.let { onFechaSeleccionada(millisAIso(it)) }
                onCerrar()
            }) { Text("Aceptar") }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) { Text("Cancelar") }
        }
    ) {
        DatePicker(state = estado)
    }
}