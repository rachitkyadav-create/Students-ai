package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AcademicSubject
import com.example.data.model.QuizQuestion
import com.example.ui.StudyViewModel

@Composable
fun QuizScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val quiz by viewModel.activeQuiz.collectAsStateWithLifecycle()
    val isGeneratingQuiz by viewModel.isGeneratingQuiz.collectAsStateWithLifecycle()

    var showGenerateDialog by remember { mutableStateOf(false) }
    var quizTopicInput by remember { mutableStateOf("") }
    var quizSubjectInput by remember { mutableStateOf(AcademicSubject.MATH) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("quiz_screen"),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        // Header & subject quick switcher
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Practice Exam & Quiz",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${quiz.subject.title} • ${quiz.totalQuestions} Questions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showGenerateDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("generate_custom_quiz_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI Generator", fontSize = 12.sp)
                }
            }

            // Subject tabs to load sample quizzes
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(AcademicSubject.entries) { subj ->
                    val isSelected = quiz.subject == subj
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable { viewModel.loadSampleQuiz(subj) }
                            .testTag("quiz_subject_tab_${subj.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = subj.icon,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isSelected) Color.White else Color(subj.color)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = subj.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Score summary card if submitted
        if (quiz.isSubmitted) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Score: ${quiz.score} / ${quiz.totalQuestions} (${quiz.scorePercentage}%)",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = if (quiz.scorePercentage >= 80) "Great mastery! Keep up the momentum." else "Review the verified explanations below.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.restartQuiz() },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry Quiz")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // List of questions
        items(quiz.questions.indices.toList()) { index ->
            val question = quiz.questions[index]
            val selectedOption = quiz.userAnswers[index]

            QuestionItemCard(
                index = index,
                question = question,
                selectedOption = selectedOption,
                isSubmitted = quiz.isSubmitted,
                onSelectOption = { optIdx ->
                    if (!quiz.isSubmitted) {
                        viewModel.selectQuizAnswer(index, optIdx)
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Submit Button
        if (!quiz.isSubmitted && quiz.questions.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.submitQuiz() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp)
                        .testTag("submit_quiz_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit Answers & View Mastery Score", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // AI Quiz Generator Dialog
    if (showGenerateDialog) {
        AlertDialog(
            onDismissRequest = { if (!isGeneratingQuiz) showGenerateDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate Custom AI Quiz")
                }
            },
            text = {
                Column {
                    Text(
                        text = "Enter any subject topic, textbook chapter, or exam objective.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = quizTopicInput,
                        onValueChange = { quizTopicInput = it },
                        placeholder = { Text("e.g. Mendelian Genetics, Kinematics, Dijkstra's algorithm...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    if (isGeneratingQuiz) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating verified test questions...", fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.generateCustomQuiz(quizTopicInput, quizSubjectInput)
                        showGenerateDialog = false
                        quizTopicInput = ""
                    },
                    enabled = quizTopicInput.isNotBlank() && !isGeneratingQuiz
                ) {
                    Text("Create Test")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showGenerateDialog = false },
                    enabled = !isGeneratingQuiz
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun QuestionItemCard(
    index: Int,
    question: QuizQuestion,
    selectedOption: Int?,
    isSubmitted: Boolean,
    onSelectOption: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "QUESTION ${index + 1}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                if (isSubmitted && selectedOption != null) {
                    val isCorrect = selectedOption == question.correctIndex
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isCorrect) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                    ) {
                        Text(
                            text = if (isCorrect) "Correct" else "Incorrect",
                            color = if (isCorrect) Color(0xFF166534) else Color(0xFF991B1B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Options
            question.options.forEachIndexed { optIndex, optionText ->
                val isSelected = selectedOption == optIndex
                val isCorrectAnswer = optIndex == question.correctIndex

                val optionColor = when {
                    isSubmitted && isCorrectAnswer -> Color(0xFFDCFCE7)
                    isSubmitted && isSelected && !isCorrectAnswer -> Color(0xFFFEE2E2)
                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                }

                val optionBorderColor = when {
                    isSubmitted && isCorrectAnswer -> Color(0xFF16A34A)
                    isSubmitted && isSelected && !isCorrectAnswer -> Color(0xFFDC2626)
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> Color.Transparent
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, optionBorderColor, RoundedCornerShape(12.dp))
                        .clickable(enabled = !isSubmitted) { onSelectOption(optIndex) }
                        .testTag("quiz_option_${index}_$optIndex"),
                    shape = RoundedCornerShape(12.dp),
                    color = optionColor
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected || (isSubmitted && isCorrectAnswer))
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outlineVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ('A' + optIndex).toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )

                        if (isSubmitted && isCorrectAnswer) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Correct",
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(18.dp)
                            )
                        } else if (isSubmitted && isSelected && !isCorrectAnswer) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Wrong",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Reveal explanation immediately if an option is picked or submitted
            if (selectedOption != null || isSubmitted) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Council Verified Explanation",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = question.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
