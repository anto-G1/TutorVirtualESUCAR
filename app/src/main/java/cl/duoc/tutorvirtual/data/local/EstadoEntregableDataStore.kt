
package cl.duoc.tutorvirtual.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cl.duoc.tutorvirtual.data.model.EstadoEntregable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.estadoDataStore by preferencesDataStore(
    name = "estados_entregables"
)

class EstadoEntregableDataStore(
    private val context: Context
) {

    fun obtenerEstado(idHito: Int): Flow<EstadoEntregable> {
        val clave = stringPreferencesKey("estado_$idHito")

        return context.estadoDataStore.data.map { preferencias ->
            val valor = preferencias[clave]

            EstadoEntregable.entries.find {
                it.name == valor
            } ?: EstadoEntregable.POR_INICIAR
        }
    }

    suspend fun guardarEstado(
        idHito: Int,
        estado: EstadoEntregable
    ) {
        val clave = stringPreferencesKey("estado_$idHito")

        context.estadoDataStore.edit { preferencias ->
            preferencias[clave] = estado.name
        }
    }
}
