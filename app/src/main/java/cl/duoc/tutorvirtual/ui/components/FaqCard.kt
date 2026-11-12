
package cl.duoc.tutorvirtual.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.tutorvirtual.data.model.Faq

@Composable
fun FaqCard(faq: Faq) {

    var expandida by remember(faq.id) {
        mutableStateOf(false)
    }

    Card(
        onClick = { expandida = !expandida },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = faq.pregunta,
                style = MaterialTheme.typography.titleMedium
            )

            if (expandida) {
                Spacer(Modifier.height(8.dp))

                Text(text = faq.respuesta)
            }
        }
    }
}
