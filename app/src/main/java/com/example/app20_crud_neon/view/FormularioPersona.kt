package com.example.app19_jc_neon_bd_remoto.view

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.app19_jc_neon_bd_remoto.viewmodel.PersonaUiState

@Composable
fun FormularioPersona(
    state: PersonaUiState,
    onNombresChange: (String) -> Unit,
    onApellidosChange: (String) -> Unit,
    onDniChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onAbrirCalendario: () -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit
) {
    // El campo de fecha es de solo lectura: al tocarlo se abre el calendario
    val interaccionFecha = remember { MutableInteractionSource() }
    val fechaPresionada by interaccionFecha.collectIsPressedAsState()
    LaunchedEffect(fechaPresionada) {
        if (fechaPresionada) onAbrirCalendario()
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = state.nombres,
            onValueChange = onNombresChange,
            label = { Text("Nombres") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.apellidos,
            onValueChange = onApellidosChange,
            label = { Text("Apellidos") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = formatearFecha(state.fechaNacimiento),
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha de nacimiento") },
            placeholder = { Text("dd/mm/aaaa") },
            trailingIcon = {
                IconButton(onClick = onAbrirCalendario) {
                    Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha")
                }
            },
            interactionSource = interaccionFecha,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.dni,
            onValueChange = onDniChange,
            label = { Text("DNI") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            supportingText = { Text("${state.dni.length}/8") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.correo,
            onValueChange = onCorreoChange,
            label = { Text("Correo") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onGuardar, enabled = !state.guardando) {
                Text(if (state.personaEditando == null) "Guardar" else "Actualizar")
            }
            if (state.personaEditando != null) {
                OutlinedButton(onClick = onCancelar) { Text("Cancelar") }
            }
        }
    }
}