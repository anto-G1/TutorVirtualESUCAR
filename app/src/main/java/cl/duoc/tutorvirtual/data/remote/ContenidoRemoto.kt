
package cl.duoc.tutorvirtual.data.remote

data class ContenidoRemoto(
    val version: Int,
    val fechaActualizacion: String,
    val guias: List<GuiaRemota>,
    val preguntasFrecuentes: List<FaqRemota>
)

data class GuiaRemota(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val formatoRequerido: String,
    val fechaLimite: String
)

data class FaqRemota(
    val id: Int,
    val pregunta: String,
    val respuesta: String,
    val categoria: String
)
