
package cl.duoc.tutorvirtual.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import cl.duoc.tutorvirtual.data.local.ConsultaLocal
import cl.duoc.tutorvirtual.data.local.ConsultasLocales

class ConsultasViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repositorio = ConsultasLocales(application)

    var consultas by mutableStateOf(
        repositorio.obtener()
    )
        private set

    fun agregarConsulta(
        categoria: String,
        pregunta: String
    ): String? {

        val texto = pregunta.trim()

        if (texto.length < 12) {
            return "La pregunta debe contener al menos 12 caracteres."
        }

        if (texto.length > 350) {
            return "La pregunta no puede superar los 350 caracteres."
        }

        if (categoria !in listOf(
                "Citación",
                "Metodología",
                "Formato"
            )
        ) {
            return "Selecciona una categoría válida."
        }

        consultas = repositorio.agregar(
            categoria = categoria,
            pregunta = texto
        )

        return null
    }
}
