package cl.duoc.tutorvirtual.notifications

import android.content.Context
import java.time.LocalDate

class RecordatorioPreferences(
    context: Context
) {
    private val preferences =
        context.getSharedPreferences(
            "recordatorios_hitos",
            Context.MODE_PRIVATE
        )

    fun estaActivo(hitoId: Int): Boolean {
        return preferences.getBoolean(
            "activo_$hitoId",
            false
        )
    }

    fun guardarActivo(
        hitoId: Int,
        activo: Boolean
    ) {
        preferences.edit()
            .putBoolean(
                "activo_$hitoId",
                activo
            )
            .apply()
    }

    fun obtenerFecha(
        hitoId: Int
    ): LocalDate? {
        val fecha = preferences.getString(
            "fecha_$hitoId",
            null
        ) ?: return null

        return runCatching {
            LocalDate.parse(fecha)
        }.getOrNull()
    }

    fun guardarFecha(
        hitoId: Int,
        fecha: LocalDate
    ) {
        preferences.edit()
            .putString(
                "fecha_$hitoId",
                fecha.toString()
            )
            .apply()
    }
}