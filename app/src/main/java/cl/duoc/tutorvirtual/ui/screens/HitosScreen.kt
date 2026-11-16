
package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.tutorvirtual.data.model.Hito
import cl.duoc.tutorvirtual.data.model.EstadoEntregable
import cl.duoc.tutorvirtual.ui.components.HitoCard
import cl.duoc.tutorvirtual.ui.theme.*

@Composable
fun HitosScreen(
    modifier: Modifier = Modifier,
    hitos: List<Hito>,
    onHitoClick: (Hito) -> Unit = {}
) {
    val aprobados = hitos.count {
        it.estado == EstadoEntregable.APROBADO
    }

    val enDesarrollo = hitos.count {
        it.estado == EstadoEntregable.EN_DESARROLLO
    }

    val progreso = if (hitos.isEmpty()) {
        0f
    } else {
        aprobados.toFloat() / hitos.size
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 20.dp,
            bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "TU RUTA ACADÉMICA",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Guía metodológica",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Avanza por las etapas de tu Trabajo de Título.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Resumen visual
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = VerdeOscuro
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    VerdeOscuro,
                                    VerdeInstitucional
                                )
                            )
                        )
                        .padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "PROGRESO DE TU TRABAJO",
                        color = VerdeSuave,
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = "${(progreso * 100).toInt()}% de hitos aprobados",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Blanco
                    )

                    LinearProgressIndicator(
                        progress = { progreso },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(9.dp),
                        color = VerdeSalvia,
                        trackColor = Blanco.copy(alpha = 0.25f)
                    )

                    Text(
                        text = "$aprobados de ${hitos.size} hitos aprobados",
                        color = Blanco,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResumenHitoCard(
                    valor = hitos.size.toString(),
                    etiqueta = "Etapas",
                    modifier = Modifier.weight(1f)
                )

                ResumenHitoCard(
                    valor = enDesarrollo.toString(),
                    etiqueta = "En desarrollo",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Etapas metodológicas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Selecciona una etapa para consultar su contenido.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(
            items = hitos,
            key = { it.id }
        ) { hito ->
            HitoCard(
                hito = hito,
                onClick = { onHitoClick(hito) }
            )
        }
    }
}

@Composable
private fun ResumenHitoCard(
    valor: String,
    etiqueta: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
