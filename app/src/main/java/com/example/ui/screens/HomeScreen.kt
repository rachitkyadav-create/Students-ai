package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.CouncilDeliberation
import com.example.ui.StudyViewModel

import com.example.ui.TaskViewModel

@Composable
fun HomeScreen(
    viewModel: StudyViewModel,
    taskViewModel: TaskViewModel? = null,
    onNavigateTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val roomTasks = taskViewModel?.filteredTasks?.collectAsStateWithLifecycle()?.value
    val roomStats = taskViewModel?.taskStats?.collectAsStateWithLifecycle()?.value

    val memoryTasks by viewModel.tasks.collectAsStateWithLifecycle()
    val decks by viewModel.decks.collectAsStateWithLifecycle()
    val savedCouncilInquiries by viewModel.savedCouncilInquiries.collectAsStateWithLifecycle()
    val pomodoroState by viewModel.pomodoroState.collectAsStateWithLifecycle()

    val totalMasteredCards = decks.sumOf { it.masteredCount }
    val pendingTasksCount = roomStats?.pending ?: memoryTasks.count { !it.isCompleted }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Visual Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_student_hero),
                        contentDescription = "Students learning with AI",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Scrim gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xCC0F172A),
                                        Color(0xF00F172A)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x33FFFFFF)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Multi-AI Council Powered",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Solve, Synthesize & Master",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Connect multiple AIs simultaneously for verified best answers.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0))
                        )
                    }
                }
            }
        }

        // Student Daily Pulse Stats Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Streak
                PulseStatCard(
                    title = "Study Streak",
                    value = "5 Days",
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = Color(0xFFEA580C),
                    modifier = Modifier.weight(1f)
                )

                // Pomodoros
                PulseStatCard(
                    title = "Pomodoros",
                    value = "${pomodoroState.completedPomodoros} Focus",
                    icon = Icons.Default.Timer,
                    iconTint = Color(0xFF0284C7),
                    modifier = Modifier.weight(1f)
                )

                // Mastered Cards
                PulseStatCard(
                    title = "Mastered",
                    value = "$totalMasteredCards Cards",
                    icon = Icons.Default.CheckCircle,
                    iconTint = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Feature Navigation Quick Grid
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "All-In-One Student Suite",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureLaunchCard(
                        title = "Multi-AI Council",
                        subtitle = "Ask 4 AIs & get Master Consensus",
                        icon = Icons.Default.Psychology,
                        gradientColors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("launch_council_button"),
                        onClick = { onNavigateTab(1) }
                    )

                    FeatureLaunchCard(
                        title = "Study Tasks (Room)",
                        subtitle = "SQLite deadlines & progress tracker",
                        icon = Icons.AutoMirrored.Filled.Assignment,
                        gradientColors = listOf(Color(0xFF2563EB), Color(0xFF0284C7)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("launch_tasks_button"),
                        onClick = { onNavigateTab(2) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureLaunchCard(
                        title = "Smart Flashcards",
                        subtitle = "Active recall & 3D card studio",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        gradientColors = listOf(Color(0xFF0D9488), Color(0xFF06B6D4)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("launch_flashcards_button"),
                        onClick = { onNavigateTab(3) }
                    )

                    FeatureLaunchCard(
                        title = "Practice Quiz",
                        subtitle = "AI exam simulator with feedback",
                        icon = Icons.Default.Quiz,
                        gradientColors = listOf(Color(0xFFD97706), Color(0xFFEA580C)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("launch_quiz_button"),
                        onClick = { onNavigateTab(4) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureLaunchCard(
                        title = "Essay Grader",
                        subtitle = "Rubrics & sentence polish",
                        icon = Icons.Default.EditNote,
                        gradientColors = listOf(Color(0xFF9333EA), Color(0xFFC026D3)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("launch_essay_button"),
                        onClick = { onNavigateTab(5) }
                    )

                    FeatureLaunchCard(
                        title = "Pomodoro Focus",
                        subtitle = "25m focus & interval timer",
                        icon = Icons.Default.Timer,
                        gradientColors = listOf(Color(0xFFE11D48), Color(0xFFF43F5E)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("launch_focus_button"),
                        onClick = { onNavigateTab(6) }
                    )
                }
            }
        }

        // Prompt Launch Shortcut Banner
        item {
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onNavigateTab(1) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Stuck on a tricky problem?",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pose it to the Council to debate and synthesize the solution.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Recent Council Inquiries
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Council Deliberations",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${savedCouncilInquiries.size} Inquiries",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(savedCouncilInquiries.take(3)) { item ->
            RecentInquiryCard(
                deliberation = item,
                onClick = { viewModel.loadHistoricalDeliberation(item) }
            )
        }

        // Upcoming Deadlines Card
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Study Deadlines",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "$pendingTasksCount Pending",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateTab(2) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (roomTasks != null && roomTasks.isNotEmpty()) {
            items(roomTasks.filter { !it.isCompleted }.take(3)) { task ->
                val priorityCol = when (task.priority) {
                    "Urgent" -> Color(0xFFEF4444)
                    "Important" -> Color(0xFFF59E0B)
                    else -> Color(0xFF10B981)
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { onNavigateTab(2) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(priorityCol)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${task.subject} • ${task.taskType}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = priorityCol.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = task.priority,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = priorityCol,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        } else {
            items(memoryTasks.filter { !it.isCompleted }.take(3)) { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { onNavigateTab(2) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(task.priority.color))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${task.subject.title} • Due ${task.dueDateString}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${task.estimatedMinutes}m",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PulseStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FeatureLaunchCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x33FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFF1F5F9),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentInquiryCard(
    deliberation: CouncilDeliberation,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(deliberation.subject.color).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = deliberation.subject.title,
                        color = Color(deliberation.subject.color),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = "${deliberation.consensusAgreementScore}% Consensus",
                        color = Color(0xFF166534),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = deliberation.question,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                deliberation.individualAnswers.forEach { answer ->
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(answer.member.primaryColor)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = answer.member.icon,
                            contentDescription = answer.member.displayName,
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Deliberated by 4 AI personas",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
