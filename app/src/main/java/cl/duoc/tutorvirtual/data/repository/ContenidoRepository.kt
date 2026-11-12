
package cl.duoc.tutorvirtual.data.repository

import android.content.Context
import cl.duoc.tutorvirtual.data.local.ContenidoLocalDataSource
import cl.duoc.tutorvirtual.data.remote.ContenidoRemoto
import cl.duoc.tutorvirtual.data.remote.ContenidoApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContenidoRepository(
    private val context: Context,
    private val api: ContenidoApiService
) {

    suspend fun sincronizar(): Result<ContenidoRemoto> =
        withContext(Dispatchers.IO) {
            try {
                val remoto = api.obtenerContenido(System.currentTimeMillis())
                val local = ContenidoLocalDataSource.obtenerContenido(context)


                if (
                    remoto.version > local.version ||
                    (remoto.version == local.version && remoto != local)
                ) {
                    ContenidoLocalDataSource.guardarContenido(
                        context,
                        remoto
                    )
                }


                Result.success(
                    ContenidoLocalDataSource.obtenerContenido(context)
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun obtenerLocal(): ContenidoRemoto =
        withContext(Dispatchers.IO) {
            ContenidoLocalDataSource.obtenerContenido(context)
        }
}
