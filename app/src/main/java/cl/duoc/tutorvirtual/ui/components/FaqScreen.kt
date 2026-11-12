package cl.duoc.tutorvirtual.ui.components
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.tutorvirtual.data.local.FaqLocalDataSource
import cl.duoc.tutorvirtual.data.remote.FaqRemota



@Composable
fun FaqScreen(
    modifier: Modifier = Modifier,
    onVolver: () -> Unit,
    preguntasRemotas: List<FaqRemota>? = null
) {


    val preguntasActualizadas = FaqLocalDataSource.preguntas.map { faq ->

        val actualizacion = preguntasRemotas?.find {
            it.id == faq.id
        }

        faq.copy(
            pregunta = actualizacion?.pregunta ?: faq.pregunta,
            respuesta = actualizacion?.respuesta ?: faq.respuesta,
            categoria = actualizacion?.categoria ?: faq.categoria
        )
    }



    val categorias = listOf(
        "Citación",
        "Metodología",
        "Formato"
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Text(
                text = "Preguntas frecuentes",
                style = MaterialTheme.typography.headlineMedium
            )
        }



        item {
            OutlinedButton(
                onClick = onVolver
            ) {
                Text("← Volver al inicio")
            }
        }

        categorias.forEach { categoria ->

            item {
                Text(
                    text = categoria,
                    style = MaterialTheme.typography.titleLarge
                )
            }


            items(
                preguntasActualizadas.filter {
                    it.categoria == categoria
                }
            ) { faq ->
                FaqCard(faq = faq)
            }



        }
        }
    }
