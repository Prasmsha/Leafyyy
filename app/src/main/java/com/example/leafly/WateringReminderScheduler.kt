package com.example.leafly

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.leafly.model.PlantModel
import java.util.concurrent.TimeUnit

object WateringReminderScheduler {

    fun scheduleReminder(context: Context, plant: PlantModel) {
        val workManager = WorkManager.getInstance(context)
        val inputData = Data.Builder()
            .putString("plantName", plant.name)
            .build()

        // Schedule periodic reminder based on plant's watering frequency
        val days = plant.wateringFrequencyDays.toLong().coerceAtLeast(1)
        val workRequest = PeriodicWorkRequestBuilder<WateringReminderWorker>(days, TimeUnit.DAYS)
            .setInputData(inputData)
            .addTag("watering_${plant.id}")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "watering_${plant.id}",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun scheduleTestReminder(context: Context, plantName: String) {
        val workManager = WorkManager.getInstance(context)
        val inputData = Data.Builder()
            .putString("plantName", plantName)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<WateringReminderWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .setInputData(inputData)
            .build()

        workManager.enqueue(workRequest)
    }

    fun cancelReminder(context: Context, plantId: String) {
        WorkManager.getInstance(context).cancelUniqueWork("watering_$plantId")
    }
}
