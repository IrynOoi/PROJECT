// CsvExporter.kt
package edu.utem.ftmk.slm02

import android.content.Context
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    fun exportPredictionsToCache(
        context: Context,
        predictions: List<PredictionResult>
    ): File {

        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())

        val fileName = "prediction_history_$timeStamp.csv"
        val file = File(context.cacheDir, fileName)

        FileWriter(file).use { writer ->

            // Group predictions by model
            val groupedByModel = predictions.groupBy { it.modelName }

            groupedByModel.forEach { (modelName, results) ->

                // ---- Model Separator ----
                writer.append("MODEL:,\"$modelName\"\n")

                // ---- Table Header ----
                writer.append(
                    "foodName,predictedAllergens,timestamp,latencyMs,javaHeapKb,nativeHeapKb\n"
                )

                results.forEach { result ->
                    val metrics = result.metrics

                    writer.append(
                                "\"${result.foodItem.name}\"," +
                                "\"${result.predictedAllergens}\"," +
                                "${result.timestamp}," +
                                "${metrics?.latencyMs ?: ""}," +
                                "${metrics?.javaHeapKb ?: ""}," +
                                "${metrics?.nativeHeapKb ?: ""}\n"
                    )
                }

                // Empty row between models
                writer.append("\n")
            }
        }

        return file
    }

    fun getShareableUri(context: Context, file: File) =
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
}
