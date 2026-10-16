package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "study_tasks")
data class StudyTaskEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subject: String = "Mathematics",
    val taskType: String = "Assignment", // Assignment, Exam, Reading, Project, Problem Set
    val deadlineTimestamp: Long, // Epoch millis
    val estimatedMinutes: Int = 45,
    val priority: String = "Important", // Urgent, Important, Standard
    val isCompleted: Boolean = false,
    val notes: String = "",
    val aiStudyTip: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
