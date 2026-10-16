package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.StudyDatabase
import com.example.data.local.StudyTaskEntity
import com.example.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilterStatus(val label: String) {
    ALL("All Tasks"),
    PENDING("To Do"),
    COMPLETED("Completed"),
    DUE_SOON("Due in 24h")
}

enum class TaskSortOrder(val label: String) {
    DEADLINE_SOONEST("Deadline (Soonest)"),
    PRIORITY_HIGHEST("Priority (Urgent)"),
    NEWEST_CREATED("Recently Added"),
    TITLE_AZ("Title (A-Z)")
}

data class TaskStats(
    val total: Int = 0,
    val pending: Int = 0,
    val completed: Int = 0,
    val completionPercent: Int = 0,
    val urgentCount: Int = 0,
    val estimatedMinutesRemaining: Int = 0
)

class TaskViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: TaskRepository

    init {
        val database = StudyDatabase.getDatabase(application, viewModelScope)
        repository = TaskRepository(database.studyTaskDao())
    }

    private val rawTasks = repository.allTasks

    // Search and filter states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow(TaskFilterStatus.ALL)
    val statusFilter: StateFlow<TaskFilterStatus> = _statusFilter.asStateFlow()

    private val _subjectFilter = MutableStateFlow("All Subjects")
    val subjectFilter: StateFlow<String> = _subjectFilter.asStateFlow()

    private val _sortOrder = MutableStateFlow(TaskSortOrder.DEADLINE_SOONEST)
    val sortOrder: StateFlow<TaskSortOrder> = _sortOrder.asStateFlow()

    private val _aiLoadingTaskId = MutableStateFlow<String?>(null)
    val aiLoadingTaskId: StateFlow<String?> = _aiLoadingTaskId.asStateFlow()

    // Filtered and sorted tasks stream
    val filteredTasks: StateFlow<List<StudyTaskEntity>> = combine(
        rawTasks,
        _searchQuery,
        _statusFilter,
        _subjectFilter,
        _sortOrder
    ) { tasksList, query, status, subject, sort ->
        val now = System.currentTimeMillis()
        val twentyFourHours = 24 * 3600 * 1000L

        tasksList.filter { task ->
            // Search query filter
            val matchesQuery = query.isBlank() ||
                    task.title.contains(query, ignoreCase = true) ||
                    task.notes.contains(query, ignoreCase = true) ||
                    task.subject.contains(query, ignoreCase = true)

            // Status filter
            val matchesStatus = when (status) {
                TaskFilterStatus.ALL -> true
                TaskFilterStatus.PENDING -> !task.isCompleted
                TaskFilterStatus.COMPLETED -> task.isCompleted
                TaskFilterStatus.DUE_SOON -> !task.isCompleted && (task.deadlineTimestamp - now) in 0..twentyFourHours
            }

            // Subject filter
            val matchesSubject = subject == "All Subjects" || task.subject.equals(subject, ignoreCase = true)

            matchesQuery && matchesStatus && matchesSubject
        }.sortedWith { a, b ->
            when (sort) {
                TaskSortOrder.DEADLINE_SOONEST -> a.deadlineTimestamp.compareTo(b.deadlineTimestamp)
                TaskSortOrder.PRIORITY_HIGHEST -> priorityRank(a.priority).compareTo(priorityRank(b.priority))
                TaskSortOrder.NEWEST_CREATED -> b.createdAt.compareTo(a.createdAt)
                TaskSortOrder.TITLE_AZ -> a.title.compareTo(b.title, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Aggregate statistics
    val taskStats: StateFlow<TaskStats> = rawTasks.combine(_searchQuery) { tasksList, _ ->
        val total = tasksList.size
        val pending = tasksList.count { !it.isCompleted }
        val completed = tasksList.count { it.isCompleted }
        val percent = if (total == 0) 0 else (completed * 100) / total
        val urgent = tasksList.count { !it.isCompleted && it.priority == "Urgent" }
        val estMins = tasksList.filter { !it.isCompleted }.sumOf { it.estimatedMinutes }

        TaskStats(
            total = total,
            pending = pending,
            completed = completed,
            completionPercent = percent,
            urgentCount = urgent,
            estimatedMinutesRemaining = estMins
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskStats()
    )

    private fun priorityRank(p: String): Int = when (p) {
        "Urgent" -> 0
        "Important" -> 1
        else -> 2
    }

    fun setSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun setStatusFilter(status: TaskFilterStatus) {
        _statusFilter.value = status
    }

    fun setSubjectFilter(subject: String) {
        _subjectFilter.value = subject
    }

    fun setSortOrder(order: TaskSortOrder) {
        _sortOrder.value = order
    }

    // CRUD operations
    fun addTask(
        title: String,
        subject: String,
        taskType: String,
        deadlineTimestamp: Long,
        estimatedMinutes: Int = 45,
        priority: String = "Important",
        notes: String = ""
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val task = StudyTaskEntity(
                title = title.trim(),
                subject = subject,
                taskType = taskType,
                deadlineTimestamp = deadlineTimestamp,
                estimatedMinutes = estimatedMinutes,
                priority = priority,
                notes = notes.trim()
            )
            repository.insertTask(task)
        }
    }

    fun toggleTask(taskId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(taskId, !currentStatus)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTaskById(taskId)
        }
    }

    fun clearCompleted() {
        viewModelScope.launch {
            repository.clearCompletedTasks()
        }
    }

    fun requestAiStudyTip(task: StudyTaskEntity) {
        viewModelScope.launch {
            _aiLoadingTaskId.value = task.id
            try {
                repository.generateAiStudyTip(task)
            } finally {
                _aiLoadingTaskId.value = null
            }
        }
    }
}
