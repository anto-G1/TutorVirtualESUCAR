
package cl.duoc.tutorvirtual.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.tutorvirtual.data.model.Hito
import cl.duoc.tutorvirtual.data.model.EstadoEntregable
import cl.duoc.tutorvirtual.notifications.RecordatorioPreferences
import cl.duoc.tutorvirtual.ui.theme.VerdeOscuro
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private data class HitoConFecha(
    val hito: Hito,
    val fecha: LocalDate?
)

// ===============================================
// HU-08: CHECKLIST DE ENTREGABLES
// ===============================================

@Composable
fun ChecklistHitosScreen(
    hitos: List<Hito>,
    onAbrirHito: (Hito) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val almacenamiento = remember(context) {
        context.getSharedPreferences(
            "checklist_personal_esucar",
            Context.MODE_PRIVATE
        )
    }

    val fechas = remember(context) {
        RecordatorioPreferences(context)
    }

    var revisados by remember {
        mutableStateOf(
            hitos.associate { hito ->
                hito.id to almacenamiento.getBoolean(
                    "revisado_${hito.id}",
                    false
                )
            }
        )
    }

    val elementos = hitos.map { hito ->
        val fechaGuardada = fechas.obtenerFecha(hito.id)

        val fechaContenido = runCatching {
            LocalDate.parse(hito.fechaLimite)
        }.getOrNull()

        HitoConFecha(
            hito = hito,
            fecha = fechaGuardada ?: fechaContenido
        )
    }.sortedWith(
        compareBy(
            { it.fecha == null },
            { it.fecha ?: LocalDate.MAX }
        )
    )

    val revisadosTotal = revisados.values.count { it }
    val hoy = LocalDate.now()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                "MIS ENTREGABLES",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                "Checklist académico",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Organiza tus hitos y revisa las fechas importantes.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = VerdeOscuro
                ),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "MI CHECKLIST",
                        color = androidx.compose.ui.graphics.Color.White
                    )

                    Text(
                        "$revisadosTotal de ${hitos.size} revisados",
                        color = androidx.compose.ui.graphics.Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )

                    LinearProgressIndicator(
                        progress = {
                            if (hitos.isEmpty()) 0f
                            else revisadosTotal.toFloat() / hitos.size
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(9.dp)
                    )
                }
            }
        }

        items(elementos, key = { it.hito.id }) { elemento ->
            val hito = elemento.hito
            val fecha = elemento.fecha

            val dias = fecha?.let {
                ChronoUnit.DAYS.between(hoy, it)
            }

            val aviso = when {
                dias == null -> "Fecha pendiente"
                dias < 0 -> "Fecha vencida"
                dias <= 3 -> "Próximo a vencer"
                else -> "Fecha programada"
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        Checkbox(
                            checked = revisados[hito.id] == true,
                            onCheckedChange = { marcado ->
                                revisados = revisados +
                                        (hito.id to marcado)

                                almacenamiento.edit()
                                    .putBoolean(
                                        "revisado_${hito.id}",
                                        marcado
                                    )
                                    .apply()
                            }
                        )

                        Column(Modifier.weight(1f)) {
                            Text(
                                hito.titulo,
                                style =
                                    MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "Estado: ${hito.estado.etiqueta}",
                                style =
                                    MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    HorizontalDivider()

                    Text(
                        text = fecha?.format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        ) ?: "Sin fecha asignada",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = aviso,
                        color = if (dias != null && dias <= 3) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedButton(
                        onClick = { onAbrirHito(hito) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Consultar etapa")
                    }
                }
            }
        }

        item {
            Text(
                "Marcar un ítem como revisado es un control " +
                        "personal y no cambia su estado oficial " +
                        "ni significa que esté aprobado.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ===============================================
// HU-10: RUBRICAS SIMPLIFICADAS
// ===============================================

// Contenidos referenciales que deben ser
// validados por el profesor guía de ESUCAR.
private val criteriosPorEtapa = mapOf(
    1 to listOf(
        "Claridad en la formulación del problema.",
        "Coherencia de la pregunta de investigación.",
        "Objetivos generales y específicos definidos.",
        "Justificación y delimitación del estudio."
    ),
    2 to listOf(
        "Pertinencia y confiabilidad de las fuentes.",
        "Definición de conceptos fundamentales.",
        "Relación entre antecedentes y problema.",
        "Citación y referencias correctas."
    ),
    3 to listOf(
        "Coherencia entre metodología y objetivos.",
        "Justificación del enfoque seleccionado.",
        "Descripción de técnicas e instrumentos.",
        "Procedimiento y resguardos éticos."
    ),
    4 to listOf(
        "Aplicación adecuada de los instrumentos.",
        "Organización de los datos recopilados.",
        "Calidad y trazabilidad de los registros.",
        "Respeto de los aspectos éticos."
    ),
    5 to listOf(
        "Análisis coherente con los objetivos.",
        "Interpretación fundamentada de resultados.",
        "Relación con el marco teórico.",
        "Conclusiones, aportes y limitaciones."
    )
)

@Composable
fun RubricasScreen(
    hitos: List<Hito>,
    modifier: Modifier = Modifier
) {
    var etapaAbierta by rememberSaveable {
        mutableIntStateOf(hitos.firstOrNull()?.id ?: -1)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                "EVALUACIÓN ACADÉMICA",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                "Pautas y rúbricas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Conoce los criterios orientativos de cada etapa.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(hitos, key = { it.id }) { hito ->
            Card(
                onClick = {
                    etapaAbierta =
                        if (etapaAbierta == hito.id) -1
                        else hito.id
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            hito.titulo,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            if (etapaAbierta == hito.id) "−" else "+",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    if (etapaAbierta == hito.id) {
                        HorizontalDivider()

                        criteriosPorEtapa[hito.id]
                            .orEmpty()
                            .forEach { criterio ->
                                Text(
                                    "• $criterio",
                                    style =
                                        MaterialTheme.typography.bodyMedium
                                )
                            }
                    }
                }
            }
        }

        item {
            Text(
                "Estas rúbricas son ejemplos simplificados. " +
                        "No sustituyen las pautas oficiales " +
                        "entregadas por ESUCAR.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
