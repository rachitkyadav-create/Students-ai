package com.example.data.model

data class StudyTask(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val subject: AcademicSubject = AcademicSubject.MATH,
    val dueDateString: String,
    val estimatedMinutes: Int = 45,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TaskPriority(val label: String, val color: Long) {
    HIGH("Urgent", 0xFFEF4444),
    MEDIUM("Important", 0xFFF59E0B),
    LOW("Standard", 0xFF10B981)
}

data class PomodoroSessionState(
    val totalSeconds: Int = 25 * 60,
    val secondsRemaining: Int = 25 * 60,
    val isRunning: Boolean = false,
    val isBreak: Boolean = false,
    val completedPomodoros: Int = 3
) {
    val progress: Float
        get() = if (totalSeconds == 0) 0f else (totalSeconds - secondsRemaining).toFloat() / totalSeconds.toFloat()

    val formattedTime: String
        get() {
            val mins = secondsRemaining / 60
            val secs = secondsRemaining % 60
            return "%02d:%02d".format(mins, secs)
        }
}
