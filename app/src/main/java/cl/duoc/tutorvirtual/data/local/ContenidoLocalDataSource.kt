
package cl.duoc.tutorvirtual.data.local

import android.content.Context
import cl.duoc.tutorvirtual.data.remote.ContenidoRemoto
import com.google.gson.Gson
import java.io.File
import android.util.AtomicFile
object ContenidoLocalDataSource {

    private const val ARCHIVO = "contenido_actualizado.json"
    private val gson = Gson()

    fun cargarContenidoInicial(context: Context): ContenidoRemoto {
        val json = context.assets.open("contenido.json")
            .bufferedReader()
            .use { it.readText() }

        return gson.fromJson(json, ContenidoRemoto::class.java)
    }


    fun obtenerContenido(context: Context): ContenidoRemoto {
        val archivo = File(context.filesDir, ARCHIVO)
        val atomicFile = AtomicFile(archivo)

        if (atomicFile.baseFile.exists() ||
            File(archivo.path + ".bak").exists()
        ) {
            try {
                val json = atomicFile.openRead()
                    .bufferedReader(Charsets.UTF_8)
                    .use { it.readText() }

                val contenido = gson.fromJson(
                    json,
                    ContenidoRemoto::class.java
                )

                if (
                    contenido != null &&
                    contenido.version > 0 &&
                    contenido.guias != null &&
                    contenido.preguntasFrecuentes != null
                ) {
                    return contenido
                }
            } catch (e: Exception) {
                // Si falla la lectura, usamos el contenido inicial.
            }
        }

        return cargarContenidoInicial(context)
    }


    fun guardarContenido(
        context: Context,
        contenido: ContenidoRemoto
    ) {
        val archivo = File(context.filesDir, ARCHIVO)
        val atomicFile = AtomicFile(archivo)

        val datos = gson.toJson(contenido)
            .toByteArray(Charsets.UTF_8)

        val salida = atomicFile.startWrite()

        try {
            salida.write(datos)
            atomicFile.finishWrite(salida)
        } catch (e: Exception) {
            atomicFile.failWrite(salida)
            throw e
        }
    }

}
