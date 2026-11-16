
package cl.duoc.tutorvirtual.data.local

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class ConsultaLocal(
    val id: String,
    val categoria: String,
    val pregunta: String
)

class ConsultasLocales(context: Context) {

    private val preferencias = context.applicationContext
        .getSharedPreferences(
            "consultas_esucar",
            Context.MODE_PRIVATE
        )

    fun obtener(): List<ConsultaLocal> {
        val contenido = preferencias.getString(
            "preguntas",
            "[]"
        ) ?: "[]"

        return try {
            val array = JSONArray(contenido)

            (0 until array.length()).mapNotNull { indice ->
                val item = array.optJSONObject(indice)
                    ?: return@mapNotNull null

                ConsultaLocal(
                    id = item.optString("id"),
                    categoria = item.optString("categoria"),
                    pregunta = item.optString("pregunta")
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun agregar(
        categoria: String,
        pregunta: String
    ): List<ConsultaLocal> {

        val nueva = ConsultaLocal(
            id = UUID.randomUUID().toString(),
            categoria = categoria,
            pregunta = pregunta.trim()
        )

        val actualizadas = listOf(nueva) + obtener()
        val array = JSONArray()

        actualizadas.forEach { consulta ->
            array.put(
                JSONObject().apply {
                    put("id", consulta.id)
                    put("categoria", consulta.categoria)
                    put("pregunta", consulta.pregunta)
                }
            )
        }

        preferencias.edit()
            .putString("preguntas", array.toString())
            .apply()

        return actualizadas
    }
}
