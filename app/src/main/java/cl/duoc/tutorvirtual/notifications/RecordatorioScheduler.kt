package cl.duoc.tutorvirtual.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

object RecordatorioScheduler {

    private val anticipaciones = listOf(3, 1)

    fun programar(
        context: Context,
        hitoId: Int,
        titulo: String,
        fechaLimite: LocalDate
    ): Boolean {
        val workManager =
            WorkManager.getInstance(context)

        var recordatoriosProgramados = 0

        anticipaciones.forEach { dias ->
            val momento = fechaLimite
                .minusDays(dias.toLong())
                .atTime(9, 0)
                .atZone(ZoneId.systemDefault())
                .toInstant()

            val demora = Duration.between(
                Instant.now(),
                momento
            ).toMillis()

            val nombreTrabajo =
                nombreRecordatorio(hitoId, dias)

            if (demora > 0) {
                val datos = workDataOf(
                    RecordatorioWorker.CLAVE_HITO_ID
                            to hitoId,
                    RecordatorioWorker.CLAVE_TITULO
                            to titulo,
                    RecordatorioWorker.CLAVE_DIAS
                            to dias
                )

                val solicitud =
                    OneTimeWorkRequestBuilder<
                            RecordatorioWorker
                            >()
                        .setInputData(datos)
                        .setInitialDelay(
                            demora,
                            TimeUnit.MILLISECONDS
                        )
                        .build()

                workManager.enqueueUniqueWork(
                    nombreTrabajo,
                    ExistingWorkPolicy.REPLACE,
                    solicitud
                )

                recordatoriosProgramados++
            } else {
                workManager.cancelUniqueWork(
                    nombreTrabajo
                )
            }
        }

        return recordatoriosProgramados > 0
    }

    fun cancelar(
        context: Context,
        hitoId: Int
    ) {
        val workManager =
            WorkManager.getInstance(context)

        anticipaciones.forEach { dias ->
            workManager.cancelUniqueWork(
                nombreRecordatorio(hitoId, dias)
            )
        }

        workManager.cancelUniqueWork(
            "prueba_recordatorio_$hitoId"
        )
    }

    fun programarPrueba(
        context: Context,
        hitoId: Int,
        titulo: String
    ) {
        val datos = workDataOf(
            RecordatorioWorker.CLAVE_HITO_ID
                    to hitoId,
            RecordatorioWorker.CLAVE_TITULO
                    to titulo,
            RecordatorioWorker.CLAVE_DIAS
                    to 0
        )

        val solicitud =
            OneTimeWorkRequestBuilder<
                    RecordatorioWorker
                    >()
                .setInputData(datos)
                .setInitialDelay(
                    10,
                    TimeUnit.SECONDS
                )
                .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "prueba_recordatorio_$hitoId",
                ExistingWorkPolicy.REPLACE,
                solicitud
            )
    }

    private fun nombreRecordatorio(
        hitoId: Int,
        dias: Int
    ): String {
        return "recordatorio_${hitoId}_$dias"
    }
}