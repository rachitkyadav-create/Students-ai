package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyTaskDao {

    @Query("SELECT * FROM study_tasks ORDER BY isCompleted ASC, deadlineTimestamp ASC")
    fun getAllTasks(): Flow<List<StudyTaskEntity>>

    @Query("SELECT * FROM study_tasks WHERE isCompleted = 0 ORDER BY deadlineTimestamp ASC")
    fun getPendingTasks(): Flow<List<StudyTaskEntity>>

    @Query("SELECT * FROM study_tasks WHERE subject = :subject ORDER BY isCompleted ASC, deadlineTimestamp ASC")
    fun getTasksBySubject(subject: String): Flow<List<StudyTaskEntity>>

    @Query("SELECT * FROM study_tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: String): StudyTaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: StudyTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<StudyTaskEntity>)

    @Update
    suspend fun updateTask(task: StudyTaskEntity)

    @Delete
    suspend fun deleteTask(task: StudyTaskEntity)

    @Query("DELETE FROM study_tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: String)

    @Query("UPDATE study_tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: String, isCompleted: Boolean)

    @Query("UPDATE study_tasks SET aiStudyTip = :tip WHERE id = :taskId")
    suspend fun updateAiTip(taskId: String, tip: String)

    @Query("DELETE FROM study_tasks WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks()
}
