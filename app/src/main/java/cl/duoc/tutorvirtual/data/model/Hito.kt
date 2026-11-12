package cl.duoc.tutorvirtual.data.model

data class Hito(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val estado: EstadoEntregable = EstadoEntregable.POR_INICIAR,
    val formatoRequerido: String = "Por definir",
    val fechaLimite: String = "Por definir"

)