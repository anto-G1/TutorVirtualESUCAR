
package cl.duoc.tutorvirtual.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.util.UUID

@Composable
fun RespaldoHitoTools(hitoId: Int) {
    val context = LocalContext.current

    val carpeta = remember(hitoId) {
        File(
            context.filesDir,
            "respaldos/hito_$hitoId"
        ).apply { mkdirs() }
    }

    var cantidad by remember(hitoId) {
        mutableIntStateOf(
            carpeta.listFiles()?.size ?: 0
        )
    }

    var mensaje by remember(hitoId) {
        mutableStateOf<String?>(null)
    }

    var fotoPendiente by remember(hitoId) {
        mutableStateOf<File?>(null)
    }

    val selector = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val tipo = context.contentResolver
                .getType(uri).orEmpty()

            if (
                tipo != "application/pdf" &&
                !tipo.startsWith("image/")
            ) {
                mensaje = "Solo se permiten PDF e imágenes."
            } else {
                val extension = when (tipo) {
                    "application/pdf" -> "pdf"
                    "image/png" -> "png"
                    "image/webp" -> "webp"
                    else -> "jpg"
                }

                val destino = File(
                    carpeta,
                    "respaldo_${UUID.randomUUID()}.$extension"
                )

                val resultado = runCatching {
                    val entrada = context.contentResolver
                        .openInputStream(uri)
                        ?: throw IOException("No se pudo abrir")

                    entrada.use { input ->
                        destino.outputStream().use { output ->
                            val buffer = ByteArray(8192)
                            var total = 0L

                            while (true) {
                                val leidos = input.read(buffer)
                                if (leidos < 0) break

                                total += leidos

                                if (total > 10L * 1024 * 1024) {
                                    throw IOException(
                                        "Archivo mayor a 10 MB"
                                    )
                                }

                                output.write(
                                    buffer, 0, leidos
                                )
                            }
                        }
                    }
                }

                if (resultado.isFailure) {
                    destino.delete()
                }

                mensaje = if (resultado.isSuccess) {
                    "Documento guardado correctamente."
                } else {
                    "No se pudo guardar. Máximo 10 MB."
                }

                cantidad =
                    carpeta.listFiles()?.size ?: 0
            }
        }
    }

    val camara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        if (!exito) {
            fotoPendiente?.delete()
        }

        mensaje = if (exito) {
            "Fotografía guardada correctamente."
        } else {
            "Captura cancelada o fallida."
        }

        fotoPendiente = null
        cantidad = carpeta.listFiles()?.size ?: 0
    }

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Material de respaldo",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                "Guarda documentos o fotografías " +
                        "relacionadas con esta etapa.",
                style = MaterialTheme.typography.bodySmall
            )

            OutlinedButton(
                onClick = {
                    selector.launch(
                        arrayOf(
                            "application/pdf",
                            "image/*"
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Seleccionar documento")
            }

            Button(
                onClick = {
                    runCatching {
                        val foto = File(
                            carpeta,
                            "foto_${UUID.randomUUID()}.jpg"
                        )

                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            foto
                        )

                        fotoPendiente = foto
                        camara.launch(uri)
                    }.onFailure {
                        mensaje = "No se pudo abrir la cámara."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tomar fotografía")
            }

            Text("$cantidad respaldo(s) guardado(s)")

            mensaje?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                "Los archivos permanecen en este " +
                        "dispositivo. No se envían a ESUCAR.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
