
package cl.duoc.tutorvirtual.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NuevaConsultaDialog(
    onCerrar: () -> Unit,
    onGuardar: (String, String) -> String?
) {
    var pregunta by rememberSaveable {
        mutableStateOf("")
    }

    var categoria by rememberSaveable {
        mutableStateOf("Metodología")
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = {
            Text("Nueva consulta")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Registra una duda sobre tu Trabajo de Título."
                )

                Text(
                    "Categoría",
                    style = MaterialTheme.typography.labelLarge
                )

                Row(
                    modifier = Modifier.horizontalScroll(
                        rememberScrollState()
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Citación",
                        "Metodología",
                        "Formato"
                    ).forEach { opcion ->
                        FilterChip(
                            selected = categoria == opcion,
                            onClick = {
                                categoria = opcion
                            },
                            label = {
                                Text(opcion)
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = pregunta,
                    onValueChange = {
                        pregunta = it
                        error = null
                    },
                    label = {
                        Text("Escribe tu pregunta")
                    },
                    minLines = 3,
                    maxLines = 5,
                    isError = error != null,
                    supportingText = {
                        Text(
                            error ?: "${pregunta.length}/350 caracteres"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    "Se guardará únicamente en este dispositivo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val resultado = onGuardar(
                        categoria,
                        pregunta
                    )

                    if (resultado == null) {
                        onCerrar()
                    } else {
                        error = resultado
                    }
                }
            ) {
                Text("Guardar consulta")
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) {
                Text("Cancelar")
            }
        }
    )
}
