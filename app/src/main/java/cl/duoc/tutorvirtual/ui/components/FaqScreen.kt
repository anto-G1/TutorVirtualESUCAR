
package cl.duoc.tutorvirtual.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.tutorvirtual.data.local.FaqLocalDataSource
import cl.duoc.tutorvirtual.data.remote.FaqRemota
import cl.duoc.tutorvirtual.ui.theme.*
import java.text.Normalizer
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.tutorvirtual.viewmodel.ConsultasViewModel

private fun normalizarTexto(texto: String): String {
    return Normalizer
        .normalize(texto.trim(), Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase(Locale.ROOT)
}

@Composable
fun FaqScreen(
    modifier: Modifier = Modifier,
    onVolver: () -> Unit,
    preguntasRemotas: List<FaqRemota>? = null
) {
    var busqueda by rememberSaveable {
        mutableStateOf("")
    }

    var categoriaSeleccionada by rememberSaveable {
        mutableStateOf("Todas")
    }

    val categorias = listOf(
        "Todas", "Citación", "Metodología", "Formato"
    )

    val preguntasActualizadas = remember(preguntasRemotas) {
        FaqLocalDataSource.preguntas.map { faq ->
            val actualizacion = preguntasRemotas?.find {
                it.id == faq.id
            }

            faq.copy(
                pregunta = actualizacion?.pregunta ?: faq.pregunta,
                respuesta = actualizacion?.respuesta ?: faq.respuesta,
                categoria = actualizacion?.categoria ?: faq.categoria
            )
        }
    }

    val consulta = normalizarTexto(busqueda)

    val resultados = preguntasActualizadas.filter { faq ->
        val coincideCategoria =
            categoriaSeleccionada == "Todas" ||
                    faq.categoria == categoriaSeleccionada

        val contenido = normalizarTexto(
            "${faq.pregunta} ${faq.respuesta} ${faq.categoria}"
        )

        coincideCategoria &&
                (consulta.isBlank() || contenido.contains(consulta))
    }

    val consultasViewModel: ConsultasViewModel = viewModel()

    var mostrarFormulario by rememberSaveable {
        mutableStateOf(false)
    }

// Filtrar las consultas creadas por el estudiante
    val consultasFiltradas = consultasViewModel.consultas.filter {
        val coincideCategoria =
            categoriaSeleccionada == "Todas" ||
                    it.categoria == categoriaSeleccionada

        val coincideTexto =
            consulta.isBlank() ||
                    normalizarTexto(it.pregunta).contains(consulta)

        coincideCategoria && coincideTexto
    }

// Mostrar formulario para agregar una consulta
    if (mostrarFormulario) {
        NuevaConsultaDialog(
            onCerrar = {
                mostrarFormulario = false
            },
            onGuardar = { categoria, pregunta ->
                consultasViewModel.agregarConsulta(
                    categoria = categoria,
                    pregunta = pregunta
                )
            }
        )
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
            OutlinedButton(
                onClick = onVolver,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("← Volver al inicio")
            }
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "CENTRO DE AYUDA",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Preguntas frecuentes",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Encuentra respuestas para avanzar con seguridad.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(22.dp),
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
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "¿Tienes alguna duda?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Blanco
                    )

                    Text(
                        text = "Explora nuestras respuestas sobre citación, metodología y formato.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Blanco
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("Buscar una pregunta") },
                placeholder = {
                    Text("Ej.: metodología, citas, formato...")
                },
                leadingIcon = {
                    Text(
                        text = "⌕",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categorias.forEach { categoria ->
                    FilterChip(
                        selected = categoriaSeleccionada == categoria,
                        onClick = {
                            categoriaSeleccionada = categoria
                        },
                        label = { Text(categoria) }
                    )
                }
            }
        }

        item {
            Text(
                text = "${resultados.size} preguntas encontradas",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (resultados.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Sin resultados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Prueba con otra palabra o selecciona una categoría diferente.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            val categoriasVisibles =
                if (categoriaSeleccionada == "Todas") {
                    categorias.drop(1)
                } else {
                    listOf(categoriaSeleccionada)
                }

            categoriasVisibles.forEach { categoria ->
                val preguntasCategoria = resultados.filter {
                    it.categoria == categoria
                }

                if (preguntasCategoria.isNotEmpty()) {
                    item {
                        Text(
                            text = categoria,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(
                        items = preguntasCategoria,
                        key = { it.id }
                    ) { faq ->
                        FaqCard(faq = faq)
                    }
                }
            }
        }
    }
}
