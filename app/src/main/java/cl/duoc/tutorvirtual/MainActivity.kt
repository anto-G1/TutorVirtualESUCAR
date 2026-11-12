package cl.duoc.tutorvirtual

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.tutorvirtual.data.local.EstadoEntregableDataStore
import cl.duoc.tutorvirtual.data.local.HitosLocalDataSource
import cl.duoc.tutorvirtual.ui.screens.HitosScreen
import cl.duoc.tutorvirtual.data.model.Hito
import cl.duoc.tutorvirtual.ui.screens.HitoDetalleScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                TutorVirtualApp()
            }
        }
    }
}

@Composable
fun TutorVirtualApp() {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val estadoDataStore = remember(context) {
        EstadoEntregableDataStore(context)
    }
    val hitosConEstado = HitosLocalDataSource.hitos.map { hito ->

        val estadoGuardado by estadoDataStore
            .obtenerEstado(hito.id)
            .collectAsState(initial = hito.estado)

        hito.copy(estado = estadoGuardado)
    }

    var pantalla by remember {
        mutableStateOf("Inicio")
    }

    var hitoSeleccionado by remember {
        mutableStateOf<Hito?>(null)
    }



    BackHandler(enabled = pantalla != "Inicio") {
        when (pantalla) {
            "Detalle" -> {
                pantalla = "Guía metodológica"
            }
            else -> {
                pantalla = "Inicio"
            }
        }
    }


    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 3.dp
            ) {
                Text(
                    text = "Tutor Virtual ESUCAR",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                )
            }
        }
    ) { padding ->

        when (pantalla) {

            "Inicio" -> {

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Bienvenido al Tutor Virtual",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        text = "Apoyo para Trabajo de Título I, II y III."
                    )

                    listOf(
                        "Guía metodológica",
                        "Preguntas frecuentes",
                        "Mis avances",
                        "Mi perfil"
                    ).forEach { destino ->

                        Button(
                            onClick = {
                                pantalla = destino
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(destino)
                        }
                    }
                }
            }

            "Guía metodológica" -> {
                HitosScreen(
                    modifier = Modifier.padding(padding),
                    hitos = hitosConEstado,
                    onHitoClick = { hito ->
                        hitoSeleccionado = hito
                        pantalla = "Detalle"
                    }
                )
            }



            "Detalle" -> {
                hitoSeleccionado?.let { hito ->

                    val hitoActualizado = hitosConEstado.find {
                        it.id == hito.id
                    } ?: hito

                    HitoDetalleScreen(
                        hito = hitoActualizado,
                        onVolver = {
                            pantalla = "Guía metodológica"
                        },
                        onCambiarEstado = { nuevoEstado ->
                            scope.launch {
                                estadoDataStore.guardarEstado(
                                    hito.id,
                                    nuevoEstado
                                )
                            }
                        },
                        modifier = Modifier.padding(padding)
                    )
                }
            }



            else -> {

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = pantalla,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "Módulo preparado para próximas actividades."
                    )

                    OutlinedButton(
                        onClick = {
                            pantalla = "Inicio"
                        }
                    ) {
                        Text("Volver al inicio")
                    }
                }
            }
        }
    }
}