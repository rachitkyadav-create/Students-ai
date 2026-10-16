package com.example.data.model

data class EssayRubricCriterion(
    val name: String,
    val score: Int, // 1 to 10
    val maxScore: Int = 10,
    val feedback: String
)

data class EssayRewriteSuggestion(
    val originalSentence: String,
    val improvedSentence: String,
    val rationale: String
)

data class EssayReviewReport(
    val id: String = java.util.UUID.randomUUID().toString(),
    val textExcerpt: String,
    val overallGrade: String, // e.g. "A-", "B+"
    val overallScorePercent: Int, // e.g. 91
    val executiveSummary: String,
    val criteria: List<EssayRubricCriterion>,
    val strengths: List<String>,
    val areasToImprove: List<String>,
    val suggestions: List<EssayRewriteSuggestion>
)
