package cl.duoc.tutorvirtual

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.tutorvirtual.data.local.EstadoEntregableDataStore
import cl.duoc.tutorvirtual.data.local.HitosLocalDataSource
import cl.duoc.tutorvirtual.data.model.Hito
import cl.duoc.tutorvirtual.data.remote.ContenidoRemoto
import cl.duoc.tutorvirtual.data.remote.RetrofitClient
import cl.duoc.tutorvirtual.data.repository.ContenidoRepository
import cl.duoc.tutorvirtual.ui.components.AccessibleButton
import cl.duoc.tutorvirtual.ui.components.FaqScreen
import cl.duoc.tutorvirtual.ui.screens.HitoDetalleScreen
import cl.duoc.tutorvirtual.ui.screens.HitosScreen
import cl.duoc.tutorvirtual.ui.theme.TutorVirtualESUCARTheme
import cl.duoc.tutorvirtual.ui.viewmodel.HitoEstadoViewModel
import cl.duoc.tutorvirtual.utils.NetworkUtils

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TutorVirtualESUCARTheme {
                TutorVirtualApp()
            }
        }
    }
}

@Composable
fun TutorVirtualApp() {
    val context = LocalContext.current

    val contenidoRepository = remember(context) {
        ContenidoRepository(
            context = context.applicationContext,
            api = RetrofitClient.api
        )
    }

    var hayInternet by remember {
        mutableStateOf(NetworkUtils.hayConexion(context))
    }

    DisposableEffect(context) {
        val manager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val callback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    hayInternet =
                        NetworkUtils.hayConexion(context)
                }

                override fun onLost(network: Network) {
                    hayInternet =
                        NetworkUtils.hayConexion(context)
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    capabilities: NetworkCapabilities
                ) {
                    hayInternet =
                        NetworkUtils.hayConexion(context)
                }
            }

        manager.registerDefaultNetworkCallback(callback)

        onDispose {
            manager.unregisterNetworkCallback(callback)
        }
    }

    var mensajeSincronizacion by remember {
        mutableStateOf("Sin sincronizar")
    }

    var versionContenido by remember {
        mutableIntStateOf(0)
    }

    var fechaActualizacion by remember {
        mutableStateOf("No disponible")
    }

    var contenidoActual by remember {
        mutableStateOf<ContenidoRemoto?>(null)
    }

    LaunchedEffect(Unit) {
        val contenidoLocal =
            contenidoRepository.obtenerLocal()

        contenidoActual = contenidoLocal
        versionContenido = contenidoLocal.version
        fechaActualizacion =
            contenidoLocal.fechaActualizacion
    }

    LaunchedEffect(hayInternet) {
        if (hayInternet) {
            mensajeSincronizacion =
                "Actualizando contenido..."

            val resultado =
                contenidoRepository.sincronizar()

            mensajeSincronizacion =
                if (resultado.isSuccess) {
                    contenidoActual =
                        resultado.getOrThrow()
                    versionContenido =
                        contenidoActual!!.version
                    fechaActualizacion =
                        contenidoActual!!.fechaActualizacion
                    "Contenido sincronizado"
                } else {
                    "Error al actualizar. " +
                            "Contenido local disponible."
                }
        } else {
            mensajeSincronizacion =
                "Sin conexión. Contenido local disponible."
        }
    }

    val estadoDataStore = remember(context) {
        EstadoEntregableDataStore(context)
    }

    val hitosConEstado =
        HitosLocalDataSource.hitos.map { hito ->
            val guiaActualizada =
                contenidoActual?.guias?.find {
                    it.id == hito.id
                }

            val estadoGuardado by estadoDataStore
                .obtenerEstado(hito.id)
                .collectAsState(initial = hito.estado)

            hito.copy(
                titulo =
                    guiaActualizada?.titulo
                        ?: hito.titulo,
                descripcion =
                    guiaActualizada?.descripcion
                        ?: hito.descripcion,
                formatoRequerido =
                    guiaActualizada?.formatoRequerido
                        ?: hito.formatoRequerido,
                fechaLimite =
                    guiaActualizada?.fechaLimite
                        ?: hito.fechaLimite,
                estado = estadoGuardado
            )
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
        containerColor =
            MaterialTheme.colorScheme.background,
        topBar = {
            Surface(
                color =
                    MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                Column {
                    Text(
                        text = "Tutor Virtual ESUCAR",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    )

                    Text(
                        text =
                            if (hayInternet) {
                                "● Con conexión"
                            } else {
                                "● Sin conexión"
                            },
                        color =
                            if (hayInternet) {
                                MaterialTheme
                                    .colorScheme
                                    .primary
                            } else {
                                MaterialTheme
                                    .colorScheme
                                    .error
                            },
                        style =
                            MaterialTheme
                                .typography
                                .labelMedium,
                        modifier = Modifier.padding(
                            start = 20.dp,
                            bottom = 8.dp
                        )
                    )

                    Text(
                        text = mensajeSincronizacion,
                        style =
                            MaterialTheme
                                .typography
                                .labelSmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface,
                        modifier = Modifier.padding(
                            start = 20.dp,
                            bottom = 8.dp
                        )
                    )

                    Text(
                        text =
                            "Versión del contenido: " +
                                    versionContenido,
                        style =
                            MaterialTheme
                                .typography
                                .labelSmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface,
                        modifier = Modifier.padding(
                            start = 20.dp,
                            bottom = 8.dp
                        )
                    )

                    Text(
                        text =
                            "Fecha de actualización: " +
                                    fechaActualizacion,
                        style =
                            MaterialTheme
                                .typography
                                .labelSmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface,
                        modifier = Modifier.padding(
                            start = 20.dp,
                            bottom = 8.dp
                        )
                    )
                }
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
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text =
                            "Bienvenido al Tutor Virtual",
                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onBackground
                    )

                    Text(
                        text =
                            "Apoyo para Trabajo de " +
                                    "Título I, II y III.",
                        style =
                            MaterialTheme
                                .typography
                                .bodyLarge,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onBackground
                    )

                    listOf(
                        "Guía metodológica",
                        "Preguntas frecuentes",
                        "Mis avances",
                        "Mi perfil"
                    ).forEach { destino ->
                        AccessibleButton(
                            text = destino,
                            onClick = {
                                pantalla = destino
                            }
                        )
                    }
                }
            }

            "Guía metodológica" -> {
                HitosScreen(
                    modifier =
                        Modifier.padding(padding),
                    hitos = hitosConEstado,
                    onHitoClick = { hito ->
                        hitoSeleccionado = hito
                        pantalla = "Detalle"
                    }
                )
            }

            "Detalle" -> {
                hitoSeleccionado?.let { hito ->
                    val hitoActualizado =
                        hitosConEstado.find {
                            it.id == hito.id
                        } ?: hito

                    val estadoViewModel:
                            HitoEstadoViewModel = viewModel(
                        key =
                            "estado_hito_${hito.id}",
                        factory =
                            HitoEstadoViewModel.Factory(
                                idHito = hito.id,
                                dataStore =
                                    estadoDataStore
                            )
                    )

                    HitoDetalleScreen(
                        hito = hitoActualizado,
                        viewModel = estadoViewModel,
                        onVolver = {
                            pantalla =
                                "Guía metodológica"
                        },
                        modifier =
                            Modifier.padding(padding)
                    )
                }
            }

            "Preguntas frecuentes" -> {
                FaqScreen(
                    modifier =
                        Modifier.padding(padding),
                    onVolver = {
                        pantalla = "Inicio"
                    },
                    preguntasRemotas =
                        contenidoActual
                            ?.preguntasFrecuentes
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = pantalla,
                        style =
                            MaterialTheme
                                .typography
                                .headlineMedium,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onBackground
                    )

                    Text(
                        text =
                            "Módulo preparado para " +
                                    "próximas actividades.",
                        style =
                            MaterialTheme
                                .typography
                                .bodyLarge,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onBackground
                    )

                    OutlinedButton(
                        onClick = {
                            pantalla = "Inicio"
                        },
                        modifier =
                            Modifier.heightIn(min = 48.dp)
                    ) {
                        Text("Volver al inicio")
                    }
                }
            }
        }
    }
}