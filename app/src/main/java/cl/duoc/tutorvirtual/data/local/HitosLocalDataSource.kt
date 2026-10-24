package cl.duoc.tutorvirtual.data.local

import cl.duoc.tutorvirtual.data.model.Hito

object HitosLocalDataSource {

    val hitos = listOf(
        Hito(
            id = 1,
            titulo = "Planteamiento",
            descripcion = "Define el problema, los objetivos y el propósito del trabajo.",
            estado = "Por iniciar"
        ),
        Hito(
            id = 2,
            titulo = "Marco Teórico",
            descripcion = "Reúne antecedentes y fundamentos teóricos relacionados con el tema.",
            estado = "Por iniciar"
        ),
        Hito(
            id = 3,
            titulo = "Metodología",
            descripcion = "Define los métodos, técnicas y procedimientos de la investigación.",
            estado = "Por iniciar"
        ),
        Hito(
            id = 4,
            titulo = "Recogida de información",
            descripcion = "Permite obtener los datos necesarios para desarrollar la investigación.",
            estado = "Por iniciar"
        ),
        Hito(
            id = 5,
            titulo = "Análisis de resultados",
            descripcion = "Interpreta los resultados obtenidos y permite generar conclusiones.",
            estado = "Por iniciar"
        )
    )
}