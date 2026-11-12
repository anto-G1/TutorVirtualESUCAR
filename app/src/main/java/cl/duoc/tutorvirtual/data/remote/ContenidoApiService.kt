
package cl.duoc.tutorvirtual.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ContenidoApiService {

    @GET("contenido.json")
    suspend fun obtenerContenido(
        @Query("t") timestamp: Long
    ): ContenidoRemoto
}
