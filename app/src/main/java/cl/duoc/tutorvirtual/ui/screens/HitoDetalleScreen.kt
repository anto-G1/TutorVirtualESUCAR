
package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cl.duoc.tutorvirtual.data.model.Hito
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button



@Composable

fun HitoDetalleScreen(
    hito: Hito,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
)
 {

     Column(
         modifier = modifier.padding(20.dp)
     )
     {

        Text(
            text = hito.titulo,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )


        Text(
            text = hito.descripcion,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )


        Text(
            text = "Formato requerido: ${hito.formatoRequerido}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )


        Text(
            text = "Fecha límite: ${hito.fechaLimite}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )


        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = onVolver
        ) {
            Text(text = "Volver al listado")
        }



    }
}
