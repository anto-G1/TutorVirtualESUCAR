
package cl.duoc.tutorvirtual.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.tutorvirtual.data.model.Hito

private data class PasoGuia(
    val titulo: String,
    val descripcion: String
)

// Contenido pedagógico de ejemplo.
// Debe validarse con las pautas oficiales de ESUCAR.
private fun obtenerPasos(idHito: Int): List<PasoGuia> {
    return when (idHito) {

        1 -> listOf(
            PasoGuia(
                "Identificar el problema",
                "Describe la situación que deseas investigar. " +
                        "Explica su contexto y principales características."
            ),
            PasoGuia(
                "Delimitar la investigación",
                "Define el alcance del estudio, sus límites " +
                        "y los aspectos que analizarás."
            ),
            PasoGuia(
                "Formular la pregunta",
                "Redacta una pregunta de investigación clara, " +
                        "específica y posible de responder."
            ),
            PasoGuia(
                "Establecer los objetivos",
                "Define el objetivo general y los objetivos " +
                        "específicos de tu investigación."
            ),
            PasoGuia(
                "Justificar el estudio",
                "Explica la relevancia académica y práctica " +
                        "del problema que investigarás."
            )
        )

        2 -> listOf(
            PasoGuia(
                "Buscar antecedentes",
                "Identifica investigaciones y publicaciones " +
                        "relacionadas con tu tema."
            ),
            PasoGuia(
                "Seleccionar fuentes",
                "Prioriza fuentes pertinentes, confiables " +
                        "y adecuadas para tu investigación."
            ),
            PasoGuia(
                "Organizar conceptos",
                "Identifica los conceptos y fundamentos " +
                        "teóricos esenciales."
            ),
            PasoGuia(
                "Relacionar antecedentes",
                "Compara hallazgos y explica cómo se " +
                        "relacionan con tu problema."
            ),
            PasoGuia(
                "Redactar el marco teórico",
                "Construye una explicación coherente y " +
                        "respalda las ideas con citas."
            )
        )

        3 -> listOf(
            PasoGuia(
                "Definir el enfoque",
                "Selecciona un enfoque metodológico " +
                        "coherente con tus objetivos."
            ),
            PasoGuia(
                "Definir participantes o fuentes",
                "Determina la población, muestra o " +
                        "fuentes de información pertinentes."
            ),
            PasoGuia(
                "Seleccionar instrumentos",
                "Establece los instrumentos y técnicas " +
                        "que utilizarás para obtener información."
            ),
            PasoGuia(
                "Describir el procedimiento",
                "Explica paso a paso cómo ejecutarás " +
                        "la investigación."
            ),
            PasoGuia(
                "Considerar aspectos éticos",
                "Identifica los permisos, resguardos " +
                        "y requisitos éticos aplicables."
            )
        )

        4 -> listOf(
            PasoGuia(
                "Planificar la recopilación",
                "Organiza las actividades necesarias " +
                        "para obtener los datos."
            ),
            PasoGuia(
                "Preparar los instrumentos",
                "Comprueba que los instrumentos sean " +
                        "adecuados y estén listos para su uso."
            ),
            PasoGuia(
                "Recopilar la información",
                "Aplica el procedimiento establecido " +
                        "respetando los resguardos necesarios."
            ),
            PasoGuia(
                "Organizar los registros",
                "Clasifica y protege la información " +
                        "obtenida durante la investigación."
            )
        )

        5 -> listOf(
            PasoGuia(
                "Organizar los resultados",
                "Clasifica la información de acuerdo " +
                        "con los objetivos del estudio."
            ),
            PasoGuia(
                "Analizar los datos",
                "Utiliza técnicas de análisis " +
                        "coherentes con tu metodología."
            ),
            PasoGuia(
                "Interpretar los hallazgos",
                "Relaciona los resultados con los " +
                        "antecedentes y objetivos."
            ),
            PasoGuia(
                "Redactar las conclusiones",
                "Explica los hallazgos principales, " +
                        "sus limitaciones y aportes."
            )
        )

        else -> listOf(
            PasoGuia(
                "Revisar la etapa",
                "Lee las instrucciones y los requisitos."
            ),
            PasoGuia(
                "Preparar el trabajo",
                "Organiza las actividades necesarias."
            ),
            PasoGuia(
                "Verificar el resultado",
                "Comprueba que cumples la pauta."
            )
        )
    }
}

@Composable
fun GuiaPasoAPaso(
    hito: Hito,
    modifier: Modifier = Modifier
) {
    val pasos = remember(hito.id) {
        obtenerPasos(hito.id)
    }

    var indiceActual by rememberSaveable(hito.id) {
        mutableIntStateOf(0)
    }

    val indiceSeguro = indiceActual.coerceIn(
        0,
        pasos.lastIndex
    )

    val progresoObjetivo =
        (indiceSeguro + 1).toFloat() / pasos.size

    val progresoAnimado by animateFloatAsState(
        targetValue = progresoObjetivo,
        label = "Progreso de la guía"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Guía paso a paso",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Sigue las orientaciones de esta etapa.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tu recorrido",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Paso ${indiceSeguro + 1} de ${pasos.size}",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                LinearProgressIndicator(
                    progress = { progresoAnimado },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(9.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor =
                        MaterialTheme.colorScheme.primaryContainer
                )

                Crossfade(
                    targetState = indiceSeguro,
                    label = "Transición entre pasos"
                ) { indice ->
                    val paso = pasos[indice]

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color =
                            MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "PASO ${indice + 1}",
                                style =
                                    MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = paso.titulo,
                                style =
                                    MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color =
                                    MaterialTheme.colorScheme
                                        .onPrimaryContainer
                            )

                            Text(
                                text = paso.descripcion,
                                style =
                                    MaterialTheme.typography.bodyMedium,
                                color =
                                    MaterialTheme.colorScheme
                                        .onPrimaryContainer
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            indiceActual =
                                (indiceSeguro - 1).coerceAtLeast(0)
                        },
                        enabled = indiceSeguro > 0,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Anterior")
                    }

                    Button(
                        onClick = {
                            indiceActual =
                                (indiceSeguro + 1).coerceAtMost(
                                    pasos.lastIndex
                                )
                        },
                        enabled = indiceSeguro < pasos.lastIndex,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Siguiente")
                    }
                }

                if (indiceSeguro == pasos.lastIndex) {
                    Text(
                        text = "Has llegado al último paso de esta guía.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Text(
            text = "Estas orientaciones son referenciales. " +
                    "Consulta la pauta académica correspondiente.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
