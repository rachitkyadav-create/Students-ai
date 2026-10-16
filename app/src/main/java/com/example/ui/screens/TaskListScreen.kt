package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.StudyTaskEntity
import com.example.ui.TaskFilterStatus
import com.example.ui.TaskSortOrder
import com.example.ui.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskListScreen(
    taskViewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by taskViewModel.filteredTasks.collectAsStateWithLifecycle()
    val stats by taskViewModel.taskStats.collectAsStateWithLifecycle()
    val searchQuery by taskViewModel.searchQuery.collectAsStateWithLifecycle()
    val statusFilter by taskViewModel.statusFilter.collectAsStateWithLifecycle()
    val subjectFilter by taskViewModel.subjectFilter.collectAsStateWithLifecycle()
    val sortOrder by taskViewModel.sortOrder.collectAsStateWithLifecycle()
    val aiLoadingId by taskViewModel.aiLoadingTaskId.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    val subjectsList = listOf(
        "All Subjects",
        "Mathematics",
        "Physics & Chem",
        "Computer Science",
        "Biology & Med",
        "History & Social",
        "Literature & Essay"
    )

    Box(modifier = modifier.fillMaxSize().testTag("task_list_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Stats & Progress Hero Header
            item {
                TaskProgressHeader(stats = stats, onClearCompleted = { taskViewModel.clearCompleted() })
            }

            // Search Bar & Sort Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { taskViewModel.setSearchQuery(it) },
                        placeholder = { Text("Search tasks, notes, or subjects...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { taskViewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("task_search_field"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Sort dropdown button
                    Box {
                        OutlinedButton(
                            onClick = { showSortMenu = true },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .height(52.dp)
                                .testTag("task_sort_button"),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "Sort",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            TaskSortOrder.entries.forEach { order ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = order.label,
                                            fontWeight = if (sortOrder == order) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        taskViewModel.setSortOrder(order)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Status Filter Chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(TaskFilterStatus.entries) { filter ->
                        val isSelected = statusFilter == filter
                        val badgeCount = when (filter) {
                            TaskFilterStatus.ALL -> stats.total
                            TaskFilterStatus.PENDING -> stats.pending
                            TaskFilterStatus.COMPLETED -> stats.completed
                            TaskFilterStatus.DUE_SOON -> stats.urgentCount
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { taskViewModel.setStatusFilter(filter) },
                            label = { Text("${filter.label} ($badgeCount)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("filter_chip_${filter.name}")
                        )
                    }
                }
            }

            // Subject Filter Chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(subjectsList) { subject ->
                        val isSelected = subjectFilter.equals(subject, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { taskViewModel.setSubjectFilter(subject) }
                                .padding(vertical = 2.dp)
                                .testTag("subject_filter_${subject.replace(" ", "_")}")
                        ) {
                            Text(
                                text = subject,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Empty state
            if (tasks.isEmpty()) {
                item {
                    EmptyTaskState(
                        isFiltered = searchQuery.isNotBlank() || statusFilter != TaskFilterStatus.ALL || subjectFilter != "All Subjects",
                        onResetFilters = {
                            taskViewModel.setSearchQuery("")
                            taskViewModel.setStatusFilter(TaskFilterStatus.ALL)
                            taskViewModel.setSubjectFilter("All Subjects")
                        },
                        onAddTask = { showAddDialog = true }
                    )
                }
            } else {
                // List of tasks
                items(tasks, key = { it.id }) { task ->
                    StudyTaskItemCard(
                        task = task,
                        isAiLoading = aiLoadingId == task.id,
                        onToggle = { taskViewModel.toggleTask(task.id, task.isCompleted) },
                        onDelete = { taskViewModel.deleteTask(task.id) },
                        onAiTipRequest = { taskViewModel.requestAiStudyTip(task) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Floating Action Button to Add Task
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_task")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Task", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Add Task Dialog
    if (showAddDialog) {
        CreateTaskDialog(
            onDismiss = { showAddDialog = false },
            onTaskCreated = { title, subject, taskType, deadline, estMinutes, priority, notes ->
                taskViewModel.addTask(
                    title = title,
                    subject = subject,
                    taskType = taskType,
                    deadlineTimestamp = deadline,
                    estimatedMinutes = estMinutes,
                    priority = priority,
                    notes = notes
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun TaskProgressHeader(
    stats: com.example.ui.TaskStats,
    onClearCompleted: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Study Progress & Goals",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${stats.completed} of ${stats.total} completed • ${stats.estimatedMinutesRemaining} min remaining",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (stats.completionPercent >= 70) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${stats.completionPercent}% Done",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (stats.completionPercent >= 70) Color(0xFF166534) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { stats.completionPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary
            )

            // Alert banner if urgent tasks pending
            if (stats.urgentCount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEF2F2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationImportant,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${stats.urgentCount} high-priority tasks requiring focus today!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF991B1B)
                        )
                    }
                }
            }

            if (stats.completed > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onClearCompleted) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Clear Completed (${stats.completed})",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyTaskItemCard(
    task: StudyTaskEntity,
    isAiLoading: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onAiTipRequest: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val now = System.currentTimeMillis()
    val diffMillis = task.deadlineTimestamp - now
    val isOverdue = diffMillis < 0 && !task.isCompleted
    val isDueSoon = diffMillis in 0..(24 * 3600 * 1000L) && !task.isCompleted

    val deadlineText = formatDeadlineCountdown(task.deadlineTimestamp)

    val priorityColor = when (task.priority) {
        "Urgent" -> Color(0xFFEF4444)
        "Important" -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }

    val cardBorderColor by animateColorAsState(
        targetValue = when {
            task.isCompleted -> Color.Transparent
            isOverdue -> Color(0xFFEF4444).copy(alpha = 0.5f)
            isDueSoon -> Color(0xFFF59E0B).copy(alpha = 0.5f)
            else -> Color.Transparent
        },
        animationSpec = tween(300),
        label = "cardBorder"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(1.5.dp, cardBorderColor, RoundedCornerShape(18.dp))
            .clickable { expanded = !expanded }
            .testTag("task_item_${task.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (task.isCompleted) 1.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.testTag("task_check_${task.id}")
                )

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (task.isCompleted)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Subject Tag
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = task.subject,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        // Task Type Tag
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = task.taskType,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        // Estimated minutes
                        Text(
                            text = "${task.estimatedMinutes}m",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(horizontalAlignment = Alignment.End) {
                    // Priority Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = priorityColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = task.priority,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = priorityColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Deadline countdown chip
                    Text(
                        text = deadlineText,
                        fontSize = 10.sp,
                        fontWeight = if (isOverdue || isDueSoon) FontWeight.Bold else FontWeight.Normal,
                        color = when {
                            isOverdue -> Color(0xFFDC2626)
                            isDueSoon -> Color(0xFFD97706)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            // Expanded Details Section
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    if (task.notes.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Notes: ${task.notes}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // AI Coach Tip Section
                    if (task.aiStudyTip.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFEEF2FF),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFF4F46E5),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "AI Study Coach Tip:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4F46E5)
                                    )
                                    Text(
                                        text = task.aiStudyTip,
                                        fontSize = 11.sp,
                                        color = Color(0xFF1E1B4B)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onAiTipRequest,
                            enabled = !isAiLoading,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            if (isAiLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Coaching...", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (task.aiStudyTip.isBlank()) "Get AI Study Tip" else "Refresh Tip",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Task",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyTaskState(
    isFiltered: Boolean,
    onResetFilters: () -> Unit,
    onAddTask: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isFiltered) "No tasks match your filters" else "All caught up on studying!",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isFiltered) "Try adjusting your search keywords or resetting filters." else "Add assignments, problem sets, or upcoming exams to stay ahead.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (isFiltered) {
                OutlinedButton(onClick = onResetFilters) {
                    Text("Reset Filters")
                }
            } else {
                Button(
                    onClick = onAddTask,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add First Study Task")
                }
            }
        }
    }
}

@Composable
private fun CreateTaskDialog(
    onDismiss: () -> Unit,
    onTaskCreated: (String, String, String, Long, Int, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("Mathematics") }
    var selectedType by remember { mutableStateOf("Assignment") }
    var selectedPriority by remember { mutableStateOf("Important") }
    var estMinutes by remember { mutableStateOf(45) }
    var notes by remember { mutableStateOf("") }

    val now = System.currentTimeMillis()
    val oneHour = 3600 * 1000L
    val oneDay = 24 * oneHour

    var selectedDeadline by remember { mutableLongStateOf(now + (24 * oneHour)) }

    val subjects = listOf("Mathematics", "Physics & Chem", "Computer Science", "Biology & Med", "History & Social", "Literature & Essay")
    val types = listOf("Assignment", "Exam", "Problem Set", "Reading", "Project")
    val priorities = listOf("Urgent", "Important", "Standard")

    val deadlinePresets = listOf(
        "Today 6 PM" to (now + (6 * oneHour)),
        "Tonight 11:59 PM" to (now + (14 * oneHour)),
        "Tomorrow" to (now + oneDay),
        "In 3 Days" to (now + (3 * oneDay)),
        "Next Week" to (now + (7 * oneDay))
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Assignment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Study Task & Deadline")
            }
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Title *") },
                        placeholder = { Text("e.g. Physics Problem Set 4") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_task_title_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Subject:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(subjects) { subj ->
                            val isSel = selectedSubject == subj
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { selectedSubject = subj }
                            ) {
                                Text(
                                    text = subj,
                                    fontSize = 10.sp,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Task Type:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(types) { t ->
                            val isSel = selectedType == t
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { selectedType = t }
                            ) {
                                Text(
                                    text = t,
                                    fontSize = 10.sp,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Deadline Preset:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(deadlinePresets) { (label, timestamp) ->
                            val isSel = selectedDeadline == timestamp
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { selectedDeadline = timestamp }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Priority Level:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        priorities.forEach { p ->
                            val isSel = selectedPriority == p
                            val pColor = when (p) {
                                "Urgent" -> Color(0xFFEF4444)
                                "Important" -> Color(0xFFF59E0B)
                                else -> Color(0xFF10B981)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) pColor else pColor.copy(alpha = 0.12f),
                                modifier = Modifier.clickable { selectedPriority = p }
                            ) {
                                Text(
                                    text = p,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else pColor,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Study Notes / Chapter refs (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onTaskCreated(
                        title,
                        selectedSubject,
                        selectedType,
                        selectedDeadline,
                        estMinutes,
                        selectedPriority,
                        notes
                    )
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("save_task_button")
            ) {
                Text("Save Task")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatDeadlineCountdown(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = timestamp - now
    val absDiff = kotlin.math.abs(diff)

    val hours = (absDiff / (3600 * 1000L)).toInt()
    val days = (absDiff / (24 * 3600 * 1000L)).toInt()

    return when {
        diff < 0 -> {
            if (hours < 24) "Overdue ($hours h ago)" else "Overdue ($days d ago)"
        }
        hours < 1 -> "Due in <1 hour"
        hours < 24 -> "Due in $hours hours"
        days == 1 -> "Due Tomorrow"
        days < 7 -> "Due in $days days"
        else -> {
            val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
            "Due ${sdf.format(Date(timestamp))}"
        }
    }
}
