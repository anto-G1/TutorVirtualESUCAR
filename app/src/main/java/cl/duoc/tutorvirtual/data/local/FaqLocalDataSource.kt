
package cl.duoc.tutorvirtual.data.local

import cl.duoc.tutorvirtual.data.model.Faq

object FaqLocalDataSource {

    val preguntas = listOf(

        // ==========================================
        // CATEGORÍA: CITACIÓN
        // ==========================================

        Faq(
            id = 1,
            pregunta = "¿Qué es una cita bibliográfica?",
            respuesta = "Es una referencia a una fuente utilizada en un trabajo académico. Permite reconocer las ideas de otros autores y respaldar la información presentada en una investigación.",
            categoria = "Citación"
        ),

        Faq(
            id = 4,
            pregunta = "¿Cuál es la diferencia entre una cita y una referencia?",
            respuesta = "La cita identifica una fuente dentro del texto. La referencia proporciona los datos necesarios para identificar y localizar esa fuente en la bibliografía.",
            categoria = "Citación"
        ),

        Faq(
            id = 5,
            pregunta = "¿Debo citar cuando explico una idea con mis propias palabras?",
            respuesta = "Sí. Cuando utilizas una idea de otro autor, debes reconocer su procedencia, incluso si la explicas con tus propias palabras. Esto se conoce como paráfrasis.",
            categoria = "Citación"
        ),

        Faq(
            id = 6,
            pregunta = "¿Cómo puedo evitar el plagio académico?",
            respuesta = "Debes citar correctamente las fuentes utilizadas, diferenciar tus ideas de las de otros autores y utilizar comillas cuando reproduzcas un fragmento textual. También es recomendable revisar todas las referencias antes de entregar tu trabajo.",
            categoria = "Citación"
        ),

        // ==========================================
        // CATEGORÍA: METODOLOGÍA
        // ==========================================

        Faq(
            id = 2,
            pregunta = "¿Qué es el planteamiento del problema?",
            respuesta = "Es la descripción clara de una situación que necesita ser investigada. Permite explicar qué ocurre, por qué resulta importante estudiarlo y cuál será el enfoque de la investigación.",
            categoria = "Metodología"
        ),

        Faq(
            id = 7,
            pregunta = "¿Qué diferencia existe entre el objetivo general y los objetivos específicos?",
            respuesta = "El objetivo general representa el propósito principal de la investigación. Los objetivos específicos dividen ese propósito en acciones o resultados concretos que contribuyen a alcanzarlo.",
            categoria = "Metodología"
        ),

        Faq(
            id = 8,
            pregunta = "¿Cómo puedo formular una pregunta de investigación?",
            respuesta = "Primero identifica el problema que deseas estudiar. Luego delimita el tema, la población o el contexto y formula una pregunta clara, específica y que pueda responderse mediante una investigación.",
            categoria = "Metodología"
        ),

        Faq(
            id = 9,
            pregunta = "¿Cuál es la diferencia entre investigación cualitativa y cuantitativa?",
            respuesta = "La investigación cualitativa busca comprender experiencias, significados y procesos. La investigación cuantitativa utiliza mediciones y datos numéricos para analizar fenómenos. La elección depende del problema y los objetivos de investigación.",
            categoria = "Metodología"
        ),

        // ==========================================
        // CATEGORÍA: FORMATO
        // ==========================================

        Faq(
            id = 3,
            pregunta = "¿Cómo se organiza un trabajo académico?",
            respuesta = "Generalmente incluye introducción, desarrollo, conclusiones y referencias bibliográficas. Sin embargo, la estructura específica debe ajustarse a las instrucciones y pautas de la institución.",
            categoria = "Formato"
        ),

        Faq(
            id = 10,
            pregunta = "¿Qué debe incluir la introducción de un trabajo académico?",
            respuesta = "Habitualmente presenta el contexto de la investigación, el problema que se abordará, los objetivos y una descripción de la organización del documento. Se deben respetar los requisitos de la pauta académica.",
            categoria = "Formato"
        ),

        Faq(
            id = 11,
            pregunta = "¿Para qué sirven los anexos de una investigación?",
            respuesta = "Los anexos permiten incorporar información complementaria, como cuestionarios, instrumentos de investigación, tablas extensas o documentos de respaldo que ayudan a comprender el trabajo.",
            categoria = "Formato"
        ),

        Faq(
            id = 12,
            pregunta = "¿Qué debo revisar antes de entregar mi trabajo de título?",
            respuesta = "Comprueba que tu documento cumpla con la pauta de evaluación, revise correctamente la ortografía, incluya todas las secciones solicitadas y presente las citas y referencias correspondientes. Verifica también que el archivo final pueda abrirse correctamente.",
            categoria = "Formato"
        )
    )
}
