package cl.duoc.tutorvirtual.ui.screens

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import cl.duoc.tutorvirtual.data.model.EstadoEntregable
import cl.duoc.tutorvirtual.data.model.Hito
import cl.duoc.tutorvirtual.notifications.RecordatorioPreferences
import cl.duoc.tutorvirtual.notifications.RecordatorioScheduler
import cl.duoc.tutorvirtual.ui.viewmodel.HitoEstadoViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HitoDetalleScreen(
    hito: Hito,
    viewModel: HitoEstadoViewModel,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val preferences = remember(context) {
        RecordatorioPreferences(context)
    }

    var menuAbierto by remember {
        mutableStateOf(false)
    }

    var recordatoriosActivos by rememberSaveable(hito.id) {
        mutableStateOf(
            preferences.estaActivo(hito.id)
        )
    }

    var fechaIso by rememberSaveable(hito.id) {
        mutableStateOf(
            preferences.obtenerFecha(hito.id)?.toString()
        )
    }

    var mensajeRecordatorio by remember {
        mutableStateOf<String?>(null)
    }

    var accionPendiente by remember {
        mutableStateOf<(() -> Unit)?>(null)
    }

    val fechaSeleccionada = fechaIso?.let {
        runCatching {
            LocalDate.parse(it)
        }.getOrNull()
    }

    fun programarRecordatorios() {
        val fecha = fechaSeleccionada

        if (fecha == null) {
            recordatoriosActivos = false
            mensajeRecordatorio =
                "Selecciona primero una fecha límite."
            return
        }

        if (
            fecha.isBefore(
                LocalDate.now().plusDays(4)
            )
        ) {
            recordatoriosActivos = false
            mensajeRecordatorio =
                "Elige una fecha con al menos 4 días."
            return
        }

        val programado = RecordatorioScheduler.programar(
            context = context,
            hitoId = hito.id,
            titulo = hito.titulo,
            fechaLimite = fecha
        )

        if (programado) {
            preferences.guardarActivo(
                hitoId = hito.id,
                activo = true
            )
            recordatoriosActivos = true
            mensajeRecordatorio =
                "Recordatorios programados: " +
                        "3 días y 1 día antes."
        } else {
            recordatoriosActivos = false
            mensajeRecordatorio =
                "No fue posible programar recordatorios."
        }
    }

    val permisoLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission()
        ) { concedido ->
            if (concedido) {
                accionPendiente?.invoke()
            } else {
                recordatoriosActivos = false
                mensajeRecordatorio =
                    "Permiso de notificaciones rechazado."
            }

            accionPendiente = null
        }

    fun ejecutarConPermiso(
        accion: () -> Unit
    ) {
        val requierePermiso =
            Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.TIRAMISU

        val concedido =
            !requierePermiso ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

        if (concedido) {
            accion()
        } else {
            accionPendiente = accion
            permisoLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    LaunchedEffect(uiState.mensaje) {
        if (uiState.mensaje != null) {
            delay(4000)
            viewModel.limpiarMensaje()
        }
    }

    LaunchedEffect(mensajeRecordatorio) {
        if (mensajeRecordatorio != null) {
            delay(5000)
            mensajeRecordatorio = null
        }
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = hito.titulo,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = hito.descripcion,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "Estado del entregable",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        ExposedDropdownMenuBox(
            expanded = menuAbierto,
            onExpandedChange = {
                if (!uiState.guardando) {
                    menuAbierto = !menuAbierto
                }
            }
        ) {
            OutlinedTextField(
                value = uiState.estado.etiqueta,
                onValueChange = {},
                readOnly = true,
                enabled = !uiState.guardando,
                label = {
                    Text("Seleccionar estado")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuAbierto
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = menuAbierto,
                onDismissRequest = {
                    menuAbierto = false
                }
            ) {
                EstadoEntregable.entries.forEach { estado ->
                    DropdownMenuItem(
                        text = {
                            Text(estado.etiqueta)
                        },
                        onClick = {
                            menuAbierto = false
                            viewModel.seleccionarEstado(estado)
                        }
                    )
                }
            }
        }

        if (uiState.guardando) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator()
                Text("Guardando cambio...")
            }
        }

        AnimatedVisibility(
            visible = uiState.mensaje != null,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Surface(
                color =
                    MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = uiState.mensaje.orEmpty(),
                    color =
                        MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Recordatorios",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text =
                "Recibe avisos locales 3 días y 1 día " +
                        "antes de la fecha límite.",
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedButton(
            onClick = {
                val fechaInicial =
                    fechaSeleccionada
                        ?: LocalDate.now().plusDays(7)

                DatePickerDialog(
                    context,
                    { _, anio, mes, dia ->
                        val nuevaFecha = LocalDate.of(
                            anio,
                            mes + 1,
                            dia
                        )

                        fechaIso = nuevaFecha.toString()
                        preferences.guardarFecha(
                            hitoId = hito.id,
                            fecha = nuevaFecha
                        )

                        mensajeRecordatorio =
                            "Fecha límite guardada."

                        if (recordatoriosActivos) {
                            RecordatorioScheduler.programar(
                                context = context,
                                hitoId = hito.id,
                                titulo = hito.titulo,
                                fechaLimite = nuevaFecha
                            )
                        }
                    },
                    fechaInicial.year,
                    fechaInicial.monthValue - 1,
                    fechaInicial.dayOfMonth
                ).apply {
                    datePicker.minDate =
                        System.currentTimeMillis()
                }.show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            val textoFecha =
                fechaSeleccionada?.format(
                    DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy"
                    )
                ) ?: "Seleccionar fecha límite"

            Text(textoFecha)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {
            Text(
                text = "Activar recordatorios",
                style = MaterialTheme.typography.bodyLarge
            )

            Switch(
                checked = recordatoriosActivos,
                onCheckedChange = { activar ->
                    if (activar) {
                        ejecutarConPermiso {
                            programarRecordatorios()
                        }
                    } else {
                        RecordatorioScheduler.cancelar(
                            context = context,
                            hitoId = hito.id
                        )
                        preferences.guardarActivo(
                            hitoId = hito.id,
                            activo = false
                        )
                        recordatoriosActivos = false
                        mensajeRecordatorio =
                            "Recordatorios desactivados."
                    }
                }
            )
        }

        OutlinedButton(
            onClick = {
                ejecutarConPermiso {
                    RecordatorioScheduler.programarPrueba(
                        context = context,
                        hitoId = hito.id,
                        titulo = hito.titulo
                    )
                    mensajeRecordatorio =
                        "Prueba programada. " +
                                "Cierra la app y espera 10 segundos."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Probar notificación en 10 segundos")
        }

        AnimatedVisibility(
            visible = mensajeRecordatorio != null,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Surface(
                color =
                    MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = mensajeRecordatorio.orEmpty(),
                    color =
                        MaterialTheme.colorScheme
                            .onSecondaryContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text =
                "Formato requerido: ${hito.formatoRequerido}",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Fecha del contenido: ${hito.fechaLimite}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onVolver
        ) {
            Text("Volver al listado")
        }
    }
}