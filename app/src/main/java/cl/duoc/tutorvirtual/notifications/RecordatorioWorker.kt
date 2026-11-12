package cl.duoc.tutorvirtual.notifications


import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cl.duoc.tutorvirtual.MainActivity

class RecordatorioWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val hitoId = inputData.getInt(CLAVE_HITO_ID, 0)
        val titulo = inputData.getString(CLAVE_TITULO)
            ?: "Entregable"
        val dias = inputData.getInt(CLAVE_DIAS, 0)

        crearCanal()

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.failure()
        }

        val intent = Intent(
            applicationContext,
            MainActivity::class.java
        ).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            hitoId * 10 + dias,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val mensaje =
            if (dias > 0) {
                "$titulo vence en $dias día(s)."
            } else {
                "Prueba de recordatorio para $titulo."
            }

        val notificacion = NotificationCompat.Builder(
            applicationContext,
            CANAL_ID
        )
            .setSmallIcon(
                R.drawable.ic_dialog_info
            )
            .setContentTitle("Tutor Virtual ESUCAR")
            .setContentText(mensaje)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(mensaje)
            )
            .setPriority(
                NotificationCompat.PRIORITY_HIGH
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat
            .from(applicationContext)
            .notify(hitoId * 10 + dias, notificacion)

        return Result.success()
    }

    private fun crearCanal() {
        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Recordatorios de entregables",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description =
                    "Avisos antes de las fechas límite"
            }

            val manager =
                applicationContext.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            manager.createNotificationChannel(canal)
        }
    }

    companion object {
        const val CANAL_ID =
            "recordatorios_entregables"
        const val CLAVE_HITO_ID = "hito_id"
        const val CLAVE_TITULO = "titulo"
        const val CLAVE_DIAS = "dias"
    }
}