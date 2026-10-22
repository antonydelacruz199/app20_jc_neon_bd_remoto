package com.example.app19_jc_neon_bd_remoto.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app19_jc_neon_bd_remoto.model.Persona

@Composable
fun PersonaItem(persona: Persona, onEditar: () -> Unit, onEliminar: () -> Unit) {
    val edad = calcularEdad(persona.fechaNacimiento)

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "${persona.apellidos}, ${persona.nombres}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("DNI: ${persona.dni}", style = MaterialTheme.typography.bodyMedium)
                if (persona.correo.orEmpty().isNotBlank()) {
                    Text(persona.correo.orEmpty(), style = MaterialTheme.typography.bodyMedium)
                }
                Text(
                    text = "Nacimiento: ${formatearFecha(persona.fechaNacimiento)}" +
                            (edad?.let { " · $it años" } ?: ""),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onEditar) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}