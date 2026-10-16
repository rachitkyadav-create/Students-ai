package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.remote.GeminiApiClient
import com.example.ui.StudyViewModel
import com.example.ui.TaskViewModel
import com.example.ui.components.AppHeader
import com.example.ui.screens.CouncilSolverScreen
import com.example.ui.screens.EssayGraderScreen
import com.example.ui.screens.FlashcardStudioScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlannerTimerScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.TaskListScreen
import com.example.ui.theme.OmniStudyTheme

data class NavTabItem(
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OmniStudyTheme {
                OmniStudyApp()
            }
        }
    }
}

@Composable
fun OmniStudyApp(
    viewModel: StudyViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel()
) {
    val currentTab by viewModel.currentScreenTab.collectAsStateWithLifecycle()
    var showCloudInfoDialog by remember { mutableStateOf(false) }

    // Predictive back / back button returns to Home screen if on any sub-tab
    BackHandler(enabled = currentTab != 0) {
        viewModel.setScreenTab(0)
    }

    val tabs = listOf(
        NavTabItem("Home", Icons.Default.Home, "nav_home"),
        NavTabItem("Council", Icons.Default.Psychology, "nav_council"),
        NavTabItem("Tasks", Icons.AutoMirrored.Filled.Assignment, "nav_tasks"),
        NavTabItem("Cards", Icons.AutoMirrored.Filled.MenuBook, "nav_flashcards"),
        NavTabItem("Quiz", Icons.Default.Quiz, "nav_quiz"),
        NavTabItem("Essay", Icons.Default.EditNote, "nav_essay"),
        NavTabItem("Focus", Icons.Default.Timer, "nav_focus")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppHeader(
                title = "OmniStudy AI",
                subtitle = when (currentTab) {
                    0 -> "Student Academic Hub"
                    1 -> "Multi-AI Council & Consensus"
                    2 -> "Room Study Tasks & Deadlines"
                    3 -> "Active Recall Flashcards"
                    4 -> "Practice Exam Simulator"
                    5 -> "Essay Grader & Writing Polish"
                    6 -> "Pomodoro Focus Timer"
                    else -> "Academic Assistant"
                },
                onStatusClick = { showCloudInfoDialog = true }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                tabs.forEachIndexed { index, item ->
                    val isSelected = currentTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setScreenTab(index) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 9.5.sp,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tabScreenTransition",
            modifier = Modifier.padding(innerPadding)
        ) { tabIndex ->
            when (tabIndex) {
                0 -> HomeScreen(
                    viewModel = viewModel,
                    taskViewModel = taskViewModel,
                    onNavigateTab = { viewModel.setScreenTab(it) }
                )
                1 -> CouncilSolverScreen(viewModel = viewModel)
                2 -> TaskListScreen(taskViewModel = taskViewModel)
                3 -> FlashcardStudioScreen(viewModel = viewModel)
                4 -> QuizScreen(viewModel = viewModel)
                5 -> EssayGraderScreen(viewModel = viewModel)
                6 -> PlannerTimerScreen(viewModel = viewModel)
                else -> HomeScreen(
                    viewModel = viewModel,
                    taskViewModel = taskViewModel,
                    onNavigateTab = { viewModel.setScreenTab(it) }
                )
            }
        }
    }

    if (showCloudInfoDialog) {
        val isCloudConnected = GeminiApiClient.isConfigured()
        AlertDialog(
            onDismissRequest = { showCloudInfoDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(if (isCloudConnected) "Gemini Cloud Connected" else "Dual-Engine Architecture")
            },
            text = {
                Text(
                    text = if (isCloudConnected) {
                        "OmniStudy is actively connected to Google Gemini Cloud (gemini-3.5-flash & gemini-3.1-pro-preview). All council members and study generators query live cloud intelligence."
                    } else {
                        "OmniStudy is operating with its built-in offline Academic Intelligence Engine and local Room SQLite Database. To enable real-time Gemini Cloud queries across all models, configure your GEMINI_API_KEY in the AI Studio Secrets panel."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showCloudInfoDialog = false }) {
                    Text("Got it")
                }
            }
        )
    }
}
