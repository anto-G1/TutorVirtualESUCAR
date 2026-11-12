package cl.duoc.tutorvirtual.data.local

import cl.duoc.tutorvirtual.data.model.Faq

object FaqLocalDataSource {

    val preguntas = listOf(
        Faq(
            1,
            "¿Qué es una cita bibliográfica?",
            "Es una referencia a una fuente utilizada en un trabajo académico.",
            "Citación"
        ),
        Faq(
            2,
            "¿Qué es el planteamiento del problema?",
            "Es la descripción de la situación que se desea investigar.",
            "Metodología"
        ),
        Faq(
            3,
            "¿Cómo se organiza un trabajo académico?",
            "Generalmente incluye introducción, desarrollo, conclusiones y referencias.",
            "Formato"
        )
    )
}