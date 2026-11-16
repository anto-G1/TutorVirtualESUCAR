
package cl.duoc.tutorvirtual

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import cl.duoc.tutorvirtual.ui.screens.PerfilAcademicoScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import cl.duoc.tutorvirtual.data.local.EstadoEntregableDataStore
import cl.duoc.tutorvirtual.data.local.HitosLocalDataSource
import cl.duoc.tutorvirtual.data.remote.ContenidoRemoto
import cl.duoc.tutorvirtual.data.remote.RetrofitClient
import cl.duoc.tutorvirtual.data.repository.ContenidoRepository

import cl.duoc.tutorvirtual.ui.components.FaqScreen

import cl.duoc.tutorvirtual.ui.screens.ChecklistHitosScreen
import cl.duoc.tutorvirtual.ui.screens.HitoDetalleScreen
import cl.duoc.tutorvirtual.ui.screens.HitosScreen
import cl.duoc.tutorvirtual.ui.screens.InicioScreen
import cl.duoc.tutorvirtual.ui.screens.RubricasScreen

import cl.duoc.tutorvirtual.ui.theme.Blanco
import cl.duoc.tutorvirtual.ui.theme.TutorVirtualESUCARTheme
import cl.duoc.tutorvirtual.ui.theme.VerdeOscuro
import cl.duoc.tutorvirtual.ui.theme.VerdeSalvia

import cl.duoc.tutorvirtual.ui.viewmodel.HitoEstadoViewModel
import cl.duoc.tutorvirtual.utils.NetworkUtils


// =====================================================
// MAIN ACTIVITY
// =====================================================

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


// =====================================================
// APLICACIÓN PRINCIPAL
// =====================================================

@Composable
fun TutorVirtualApp() {

    val context = LocalContext.current

    // =================================================
    // 1. REPOSITORIO DE CONTENIDOS
    // =================================================

    val contenidoRepository = remember(context) {
        ContenidoRepository(
            context = context.applicationContext,
            api = RetrofitClient.api
        )
    }

    // =================================================
    // 2. CONECTIVIDAD
    // =================================================

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

    // =================================================
    // 3. SINCRONIZACIÓN DE CONTENIDOS
    // =================================================

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

    // Cargar contenido almacenado localmente
    LaunchedEffect(Unit) {

        val contenidoLocal =
            contenidoRepository.obtenerLocal()

        contenidoActual = contenidoLocal

        versionContenido =
            contenidoLocal.version

        fechaActualizacion =
            contenidoLocal.fechaActualizacion
    }

    // Actualizar contenido si existe conexión
    LaunchedEffect(hayInternet) {

        if (hayInternet) {

            mensajeSincronizacion =
                "Actualizando contenido..."

            val resultado =
                contenidoRepository.sincronizar()

            if (resultado.isSuccess) {

                val actualizado =
                    resultado.getOrThrow()

                contenidoActual = actualizado

                versionContenido =
                    actualizado.version

                fechaActualizacion =
                    actualizado.fechaActualizacion

                mensajeSincronizacion =
                    "Contenido sincronizado"

            } else {

                mensajeSincronizacion =
                    "Error al actualizar. " +
                            "Contenido local disponible."
            }

        } else {

            mensajeSincronizacion =
                "Sin conexión. Contenido local disponible."
        }
    }

    // =================================================
    // 4. ESTADOS DE ENTREGABLES
    // =================================================

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
                .collectAsState(
                    initial = hito.estado
                )

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

    // =================================================
    // 5. NAVEGACIÓN Y RESTAURACIÓN DE ESTADO
    // =================================================

    var pantalla by rememberSaveable {
        mutableStateOf("Inicio")
    }

    var idHitoSeleccionado by rememberSaveable {
        mutableIntStateOf(-1)
    }

    val hitoSeleccionado = hitosConEstado.firstOrNull {
        it.id == idHitoSeleccionado
    }

    // Botón Atrás del dispositivo
    BackHandler(
        enabled = pantalla != "Inicio"
    ) {
        when (pantalla) {

            "Detalle" -> {
                pantalla = "Guía metodológica"
            }

            else -> {
                pantalla = "Inicio"
            }
        }
    }

    // =================================================
    // 6. ADAPTACIÓN A ORIENTACIÓN
    // =================================================

    val esHorizontal =
        LocalConfiguration.current.orientation ==
                Configuration.ORIENTATION_LANDSCAPE

    // =================================================
    // 7. ESTRUCTURA VISUAL PRINCIPAL
    // =================================================

    Scaffold(

        containerColor =
            MaterialTheme.colorScheme.background,

        // =============================================
        // BARRA SUPERIOR
        // =============================================

        topBar = {

            Surface(
                modifier = Modifier.statusBarsPadding(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical =
                                if (esHorizontal) 8.dp
                                else 12.dp
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(4.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment =
                            Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = "Tutor Virtual ESUCAR",
                            modifier = Modifier.weight(1f),
                            style =
                                MaterialTheme.typography.titleMedium,
                            color =
                                MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )

                        Text(
                            text =
                                if (hayInternet) {
                                    "● En línea"
                                } else {
                                    "● Sin conexión"
                                },
                            style =
                                MaterialTheme.typography.labelSmall,
                            color =
                                if (hayInternet) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                }
                        )
                    }

                    if (!esHorizontal) {

                        Text(
                            text =
                                "Contenido v$versionContenido · " +
                                        mensajeSincronizacion,
                            style =
                                MaterialTheme.typography.labelSmall,
                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        },

        // =============================================
        // MENÚ INFERIOR
        // =============================================

        bottomBar = {

            NavigationBar(
                containerColor = VerdeOscuro,
                tonalElevation = 6.dp
            ) {

                // Símbolos sin dependencias adicionales
                val destinos = listOf(
                    Triple(
                        "Inicio",
                        "Inicio",
                        "⌂"
                    ),
                    Triple(
                        "Guía",
                        "Guía metodológica",
                        "▤"
                    ),
                    Triple(
                        "FAQ",
                        "Preguntas frecuentes",
                        "?"
                    ),
                    Triple(
                        "Hitos",
                        "Mis avances",
                        "☑"
                    ),
                    Triple(
                        "Pautas",
                        "Rúbricas",
                        "▦"
                    )
                )

                destinos.forEach { (etiqueta, destino, simbolo) ->

                    val seleccionado =
                        pantalla == destino ||
                                (
                                        destino == "Guía metodológica" &&
                                                pantalla == "Detalle"
                                        )

                    NavigationBarItem(

                        selected = seleccionado,

                        onClick = {
                            pantalla = destino
                        },

                        icon = {
                            Text(
                                text = simbolo,
                                style =
                                    MaterialTheme.typography.titleLarge
                            )
                        },

                        label = {
                            Text(etiqueta)
                        },

                        alwaysShowLabel = true,

                        colors =
                            NavigationBarItemDefaults.colors(

                                selectedIconColor = VerdeOscuro,

                                selectedTextColor = Blanco,

                                indicatorColor = VerdeSalvia,

                                unselectedIconColor =
                                    Blanco.copy(alpha = 0.75f),

                                unselectedTextColor =
                                    Blanco.copy(alpha = 0.75f)
                            )
                    )
                }
            }
        }

    ) { padding ->

        // =============================================
        // NAVEGACIÓN ENTRE PANTALLAS
        // =============================================

        when (pantalla) {

            // =========================================
            // INICIO - DASHBOARD
            // =========================================

            "Inicio" -> {

                InicioScreen(

                    hitos = hitosConEstado,

                    onNavigate = { destino ->
                        pantalla = destino
                    },

                    modifier = Modifier.padding(padding)
                )
            }

            // =========================================
            // GUÍA METODOLÓGICA - HU-01
            // =========================================

            "Guía metodológica" -> {

                HitosScreen(

                    modifier = Modifier.padding(padding),

                    hitos = hitosConEstado,

                    onHitoClick = { hito ->
                        idHitoSeleccionado = hito.id
                        pantalla = "Detalle"
                    }
                )
            }

            // =========================================
            // CHECKLIST Y AVANCES - HU-08
            // =========================================

            "Mis avances" -> {

                ChecklistHitosScreen(

                    hitos = hitosConEstado,

                    onAbrirHito = { hito ->
                        idHitoSeleccionado = hito.id
                        pantalla = "Detalle"
                    },

                    modifier = Modifier.padding(padding)
                )
            }

            // =========================================
            // RÚBRICAS - HU-10
            // =========================================

            "Rúbricas" -> {

                RubricasScreen(

                    hitos = hitosConEstado,

                    modifier = Modifier.padding(padding)
                )
            }

            // =========================================
            // DETALLE DEL HITO - HU-02, 03, 07, 09
            // =========================================

            "Detalle" -> {

                hitoSeleccionado?.let { hito ->

                    val hitoActualizado =
                        hitosConEstado.find {
                            it.id == hito.id
                        } ?: hito

                    val estadoViewModel:
                            HitoEstadoViewModel = viewModel(

                        key = "estado_hito_${hito.id}",

                        factory =
                            HitoEstadoViewModel.Factory(
                                idHito = hito.id,
                                dataStore = estadoDataStore
                            )
                    )

                    HitoDetalleScreen(

                        hito = hitoActualizado,

                        viewModel = estadoViewModel,

                        onVolver = {
                            pantalla = "Guía metodológica"
                        },

                        modifier = Modifier.padding(padding)
                    )
                }
            }

            // =========================================
            // PREGUNTAS FRECUENTES - HU-04 Y HU-05
            // =========================================

            "Preguntas frecuentes" -> {

                FaqScreen(

                    modifier = Modifier.padding(padding),

                    onVolver = {
                        pantalla = "Inicio"
                    },

                    preguntasRemotas =
                        contenidoActual?.preguntasFrecuentes
                )
            }

            // =========================================
            // PERFIL ACADÉMICO
            // =========================================

            "Mi perfil" -> {
                PerfilAcademicoScreen(
                    hitos = hitosConEstado,
                    modifier = Modifier.padding(padding)
                )
            }

            // =========================================
            // OTRAS PANTALLAS NO IMPLEMENTADAS
            // =========================================

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
                            MaterialTheme.typography.headlineMedium,
                        color =
                            MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text =
                            "Este módulo está pendiente de implementación.",
                        style =
                            MaterialTheme.typography.bodyLarge,
                        color =
                            MaterialTheme.colorScheme.onBackground
                    )

                    OutlinedButton(
                        onClick = {
                            pantalla = "Inicio"
                        },
                        modifier = Modifier.heightIn(
                            min = 48.dp
                        )
                    ) {
                        Text("Volver al inicio")
                    }
                }
            }
        }
    }
}
