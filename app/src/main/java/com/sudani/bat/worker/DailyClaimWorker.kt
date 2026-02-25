package com.sudani.bat.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sudani.bat.data.repository.SudaniRepository
import kotlinx.coroutines.flow.first

class DailyClaimWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: SudaniRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val numbers = repository.allNumbers.first()
            var totalGained = 0
            
            numbers.forEach { number ->
                val result = repository.claimPoints(number)
                if (result.isSuccess) {
                    totalGained += result.getOrDefault(0)
                }
            }
            
            // TODO: Show notification with totalGained
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
