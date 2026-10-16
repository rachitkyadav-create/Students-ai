package com.example.data.model

data class QuizQuestion(
    val id: String = java.util.UUID.randomUUID().toString(),
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val conceptTip: String = ""
)

data class QuizSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val subject: AcademicSubject,
    val questions: List<QuizQuestion>,
    var currentIndex: Int = 0,
    val userAnswers: MutableMap<Int, Int> = mutableMapOf(),
    var isSubmitted: Boolean = false
) {
    val score: Int get() = userAnswers.count { (qIdx, ans) ->
        qIdx < questions.size && questions[qIdx].correctIndex == ans
    }
    val totalQuestions: Int get() = questions.size
    val scorePercentage: Int get() = if (questions.isEmpty()) 0 else (score * 100) / totalQuestions
}
