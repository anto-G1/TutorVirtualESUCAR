
package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.tutorvirtual.data.model.Hito

@Composable
fun PerfilAcademicoScreen(
    hitos: List<Hito>,
    modifier: Modifier = Modifier
) {
    var perfilSeleccionado by rememberSaveable {
        mutableStateOf("Estudiante")
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "PERFILES ACADÉMICOS",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                "Mi perfil",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Explora las interfaces de estudiante y profesor guía."
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = perfilSeleccionado == "Estudiante",
                    onClick = {
                        perfilSeleccionado = "Estudiante"
                    },
                    label = { Text("Estudiante") }
                )

                FilterChip(
                    selected = perfilSeleccionado == "Profesor guía",
                    onClick = {
                        perfilSeleccionado = "Profesor guía"
                    },
                    label = { Text("Profesor guía") }
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        if (perfilSeleccionado == "Estudiante")
                            "Mi espacio académico"
                        else
                            "Panel del profesor guía",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        if (perfilSeleccionado == "Estudiante")
                            "Consulta tus hitos, registra avances " +
                                    "y organiza tus entregables."
                        else
                            "Consulta el estado de los hitos " +
                                    "desde una vista de seguimiento."
                    )
                }
            }
        }

        item {
            Text(
                if (perfilSeleccionado == "Estudiante")
                    "Mis etapas metodológicas"
                else
                    "Seguimiento académico",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(hitos, key = { it.id }) { hito ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        hito.titulo,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Estado: ${hito.estado.etiqueta}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            HorizontalDivider()

            Text(
                "Modo demostración: cambiar de vista no " +
                        "autentica una cuenta ni concede permisos " +
                        "de profesor. Las aprobaciones están " +
                        "deshabilitadas en esta pantalla.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
