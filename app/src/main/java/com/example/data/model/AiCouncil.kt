package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents the distinct AI expert personas that make up the Multi-AI Council.
 */
enum class AiCouncilMember(
    val id: String,
    val displayName: String,
    val roleTitle: String,
    val description: String,
    val icon: ImageVector,
    val primaryColor: Long,
    val accentColor: Long,
    val modelAlias: String,
    val systemPromptPersona: String
) {
    SCHOLAR(
        id = "scholar",
        displayName = "Dr. Scholar",
        roleTitle = "Academic & Foundational Theory",
        description = "Provides rigorous foundational explanations, academic context, and principles.",
        icon = Icons.AutoMirrored.Filled.MenuBook,
        primaryColor = 0xFF4338CA,
        accentColor = 0xFF818CF8,
        modelAlias = "gemini-3.5-flash",
        systemPromptPersona = "You are Dr. Scholar, an elite academic researcher and university professor. Explain concepts with high academic rigor, foundational principles, theoretical underpinnings, and precise terminology. Be thorough, scholarly, and articulate."
    ),
    SOCRATIC(
        id = "socratic",
        displayName = "Prof. Socratic",
        roleTitle = "Intuitive & Guided Learning",
        description = "Uses relatable real-world analogies, step-by-step intuitive logic, and clarity.",
        icon = Icons.Default.Psychology,
        primaryColor = 0xFF0D9488,
        accentColor = 0xFF2DD4BF,
        modelAlias = "gemini-3.5-flash",
        systemPromptPersona = "You are Prof. Socratic, a renowned intuitive educator and mentor. Break complex ideas down into simple, vivid real-world analogies, step-by-step intuition, and approachable examples that make hard ideas click immediately."
    ),
    FAST_SOLVE(
        id = "fast_solve",
        displayName = "FastSolve AI",
        roleTitle = "Exam & Formula Specialist",
        description = "High-yield formulas, concise bullet points, and quick exam-ready takeaways.",
        icon = Icons.Default.Speed,
        primaryColor = 0xFFEA580C,
        accentColor = 0xFFFB923C,
        modelAlias = "gemini-3.5-flash",
        systemPromptPersona = "You are FastSolve AI, an intensive exam coach and speed-prep tutor. Deliver clear, high-yield answers: key formulas, step-by-step calculation steps, cheat-sheet bullet points, and the final direct answer with zero fluff."
    ),
    FACT_CHECKER(
        id = "fact_checker",
        displayName = "Peer Reviewer",
        roleTitle = "Critical Scrutiny & Pitfall Spotter",
        description = "Identifies common student misconceptions, edge cases, and exam trap questions.",
        icon = Icons.Default.CheckCircle,
        primaryColor = 0xFF7C3AED,
        accentColor = 0xFFA78BFA,
        modelAlias = "gemini-3.1-pro-preview",
        systemPromptPersona = "You are Peer Reviewer AI, a critical academic auditor and verification specialist. Analyze the problem for common misconceptions, tricky edge cases, typical mistakes students make on exams, and verify the conditions under which solutions hold true."
    );

    companion object {
        fun defaultCouncil(): List<AiCouncilMember> = listOf(SCHOLAR, SOCRATIC, FAST_SOLVE, FACT_CHECKER)
    }
}

/**
 * Academic subjects available for specialized student routing.
 */
enum class AcademicSubject(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val color: Long
) {
    MATH("math", "Mathematics", Icons.Default.Calculate, 0xFF3B82F6),
    PHYSICS_CHEM("physics_chem", "Physics & Chem", Icons.Default.Science, 0xFF8B5CF6),
    COMPUTER_SCIENCE("cs", "Computer Science", Icons.Default.Code, 0xFF10B981),
    BIOLOGY("bio", "Biology & Med", Icons.Default.AutoAwesome, 0xFFEC4899),
    HISTORY_SOCIAL("history", "History & Social", Icons.Default.HistoryEdu, 0xFFF59E0B),
    LITERATURE_LANG("literature", "Literature & Essay", Icons.AutoMirrored.Filled.MenuBook, 0xFF6366F1);

    companion object {
        fun getById(id: String): AcademicSubject =
            entries.find { it.id == id } ?: MATH
    }
}

/**
 * Single AI perspective answer
 */
data class AiPerspectiveAnswer(
    val member: AiCouncilMember,
    val content: String,
    val keyTakeaway: String,
    val confidencePercent: Int = 96,
    val latencyMs: Long = 420
)

/**
 * The unified multi-AI deliberation outcome
 */
data class CouncilDeliberation(
    val id: String = java.util.UUID.randomUUID().toString(),
    val question: String,
    val subject: AcademicSubject,
    val timestamp: Long = System.currentTimeMillis(),
    val masterConsensusAnswer: String,
    val consensusAgreementScore: Int = 98,
    val keySummaryPoints: List<String>,
    val individualAnswers: List<AiPerspectiveAnswer>,
    val citationsOrRules: List<String> = emptyList(),
    val followUpQuestions: List<String> = emptyList()
)
