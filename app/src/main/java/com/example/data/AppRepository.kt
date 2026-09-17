package com.example.data

import android.content.Context
import androidx.room.Room
import com.example.data.db.AppDatabase
import com.example.data.db.HistoryEntity
import com.example.data.db.UserProgressEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "logica_matematica.db"
    ).build()

    val historyDao = db.historyDao()
    val progressDao = db.progressDao()

    val allHistory: Flow<List<HistoryEntity>> = historyDao.getAllHistory()
    val progress: Flow<UserProgressEntity> = progressDao.getProgress().map {
        it ?: UserProgressEntity()
    }

    suspend fun recordHistory(type: String, expression: String, resultSummary: String) {
        historyDao.insertHistory(
            HistoryEntity(
                type = type,
                expression = expression,
                resultSummary = resultSummary
            )
        )
    }

    suspend fun deleteHistory(id: Long) {
        historyDao.deleteHistory(id)
    }

    suspend fun clearAllHistory() {
        historyDao.clearHistory()
    }

    suspend fun updateQuestionAnswered(isCorrect: Boolean) {
        val current = progressDao.getProgress()
        // Simple synchronous fetch for current record
        // We can handle it safely:
    }

    suspend fun recordQuestionAnswer(isCorrect: Boolean, currentProgress: UserProgressEntity) {
        val updated = currentProgress.copy(
            questionsAnswered = currentProgress.questionsAnswered + 1,
            questionsCorrect = if (isCorrect) currentProgress.questionsCorrect + 1 else currentProgress.questionsCorrect,
            questionsIncorrect = if (!isCorrect) currentProgress.questionsIncorrect + 1 else currentProgress.questionsIncorrect,
            studyTimeMinutes = currentProgress.studyTimeMinutes + 1
        )
        progressDao.saveProgress(updated)
    }

    suspend fun recordExamCompleted(scorePercent: Int, currentProgress: UserProgressEntity) {
        val updated = currentProgress.copy(
            examsTaken = currentProgress.examsTaken + 1,
            bestExamScore = maxOf(currentProgress.bestExamScore, scorePercent),
            studyTimeMinutes = currentProgress.studyTimeMinutes + 5
        )
        progressDao.saveProgress(updated)
    }
}
