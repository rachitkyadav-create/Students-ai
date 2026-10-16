package com.example.data.repository

import com.example.data.local.StudyTaskDao
import com.example.data.local.StudyTaskEntity
import com.example.data.remote.GeminiApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TaskRepository(
    private val taskDao: StudyTaskDao
) {
    val allTasks: Flow<List<StudyTaskEntity>> = taskDao.getAllTasks()
    val pendingTasks: Flow<List<StudyTaskEntity>> = taskDao.getPendingTasks()

    fun getTasksBySubject(subject: String): Flow<List<StudyTaskEntity>> {
        return taskDao.getTasksBySubject(subject)
    }

    suspend fun getTaskById(id: String): StudyTaskEntity? = withContext(Dispatchers.IO) {
        taskDao.getTaskById(id)
    }

    suspend fun insertTask(task: StudyTaskEntity) = withContext(Dispatchers.IO) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: StudyTaskEntity) = withContext(Dispatchers.IO) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: StudyTaskEntity) = withContext(Dispatchers.IO) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteTaskById(id: String) = withContext(Dispatchers.IO) {
        taskDao.deleteTaskById(id)
    }

    suspend fun toggleTaskCompleted(id: String, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        taskDao.updateTaskStatus(id, isCompleted)
    }

    suspend fun clearCompletedTasks() = withContext(Dispatchers.IO) {
        taskDao.deleteCompletedTasks()
    }

    /**
     * AI Coach Feature: Generates a high-yield study strategy tip for a specific task
     */
    suspend fun generateAiStudyTip(task: StudyTaskEntity): String = withContext(Dispatchers.IO) {
        val prompt = "Provide 1 concise, highly actionable study tip or formula checkpoint for a student preparing for: '${task.title}' (${task.subject}, type: ${task.taskType}). Keep it under 2 sentences."
        val tip = try {
            GeminiApiClient.queryModel("gemini-3.5-flash", prompt, "You are an elite academic study coach.")
        } catch (_: Exception) {
            when {
                task.title.contains("Calculus", ignoreCase = true) -> "Check boundary conditions and test interval endpoints separately for convergence!"
                task.title.contains("Chem", ignoreCase = true) -> "Identify carbocation stability and steric hindrance before picking between SN1 and SN2 pathways."
                task.title.contains("Algorithm", ignoreCase = true) -> "Draw the recurrence tree first and check if subproblems overlap before coding."
                else -> "Break this assignment into two 25-minute Pomodoro sessions and review key definitions first."
            }
        }
        taskDao.updateAiTip(task.id, tip)
        tip
    }
}
