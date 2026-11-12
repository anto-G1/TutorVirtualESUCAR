package cl.duoc.tutorvirtual.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.tutorvirtual.data.local.EstadoEntregableDataStore
import cl.duoc.tutorvirtual.data.model.EstadoEntregable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HitoEstadoUiState(
    val estado: EstadoEntregable =
        EstadoEntregable.POR_INICIAR,
    val guardando: Boolean = false,
    val mensaje: String? = null
)

class HitoEstadoViewModel(
    private val idHito: Int,
    private val dataStore: EstadoEntregableDataStore
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(HitoEstadoUiState())

    val uiState: StateFlow<HitoEstadoUiState> =
        _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.obtenerEstado(idHito).collect { estado ->
                _uiState.update {
                    it.copy(estado = estado)
                }
            }
        }
    }

    fun seleccionarEstado(
        nuevoEstado: EstadoEntregable
    ) {
        if (nuevoEstado == _uiState.value.estado) {
            _uiState.update {
                it.copy(
                    mensaje =
                        "El estado ya estaba seleccionado."
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    guardando = true,
                    mensaje = null
                )
            }

            runCatching {
                dataStore.guardarEstado(
                    idHito = idHito,
                    estado = nuevoEstado
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        estado = nuevoEstado,
                        guardando = false,
                        mensaje =
                            "Estado guardado: " +
                                    nuevoEstado.etiqueta
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        guardando = false,
                        mensaje =
                            "No se pudo guardar el estado."
                    )
                }
            }
        }
    }

    fun limpiarMensaje() {
        _uiState.update {
            it.copy(mensaje = null)
        }
    }

    class Factory(
        private val idHito: Int,
        private val dataStore: EstadoEntregableDataStore
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            if (
                modelClass.isAssignableFrom(
                    HitoEstadoViewModel::class.java
                )
            ) {
                @Suppress("UNCHECKED_CAST")
                return HitoEstadoViewModel(
                    idHito = idHito,
                    dataStore = dataStore
                ) as T
            }

            throw IllegalArgumentException(
                "ViewModel no reconocido"
            )
        }
    }
}
