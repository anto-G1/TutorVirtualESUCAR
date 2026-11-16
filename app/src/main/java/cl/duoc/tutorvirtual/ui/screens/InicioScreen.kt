
package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.tutorvirtual.data.model.EstadoEntregable
import cl.duoc.tutorvirtual.data.model.Hito
import cl.duoc.tutorvirtual.ui.theme.*

@Composable
fun InicioScreen(
    hitos: List<Hito>,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Encabezado principal
        Column {
            Text(
                text = "TU ESPACIO ACADÉMICO",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Bienvenido a ESUCAR",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Organiza tu Trabajo de Título y avanza a tu ritmo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Tarjeta destacada
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = VerdeOscuro
            )
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            listOf(VerdeOscuro, VerdeInstitucional)
                        )
                    )
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "MI PROGRESO ACADÉMICO",
                    style = MaterialTheme.typography.labelMedium,
                    color = VerdeSuave
                )

                Text(
                    text = "${(progreso * 100).toInt()}% completado",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Blanco
                )

                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    color = VerdeSalvia,
                    trackColor = Blanco.copy(alpha = 0.25f)
                )

                Text(
                    text = "$aprobados de ${hitos.size} etapas aprobadas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Blanco
                )

                Button(
                    onClick = {
                        onNavigate("Guía metodológica")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blanco,
                        contentColor = VerdeOscuro
                    )
                ) {
                    Text("Continuar mi trabajo")
                }
            }
        }

        // Estadísticas
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            EstadisticaCard(
                titulo = "Etapas",
                valor = hitos.size.toString(),
                modifier = Modifier.weight(1f)
            )

            EstadisticaCard(
                titulo = "En desarrollo",
                valor = enDesarrollo.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Explora tu espacio",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AccesoCard(
                numero = "01",
                titulo = "Guía metodológica",
                descripcion = "Etapas y contenidos",
                modifier = Modifier.weight(1f),
                onClick = {
                    onNavigate("Guía metodológica")
                }
            )

            AccesoCard(
                numero = "02",
                titulo = "Preguntas frecuentes",
                descripcion = "Resuelve tus dudas",
                modifier = Modifier.weight(1f),
                onClick = {
                    onNavigate("Preguntas frecuentes")
                }
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AccesoCard(
                numero = "03",
                titulo = "Mis avances",
                descripcion = "Seguimiento académico",
                modifier = Modifier.weight(1f),
                onClick = {
                    onNavigate("Mis avances")
                }
            )

            AccesoCard(
                numero = "04",
                titulo = "Mi perfil",
                descripcion = "Información personal",
                modifier = Modifier.weight(1f),
                onClick = {
                    onNavigate("Mi perfil")
                }
            )
        }
    }
}

@Composable
private fun EstadisticaCard(
    titulo: String,
    valor: String,
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
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AccesoCard(
    numero: String,
    titulo: String,
    descripcion: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(158.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = numero,
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 7.dp
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
