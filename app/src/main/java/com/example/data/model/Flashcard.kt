package com.example.data.model

data class FlashcardItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val frontPrompt: String,
    val backAnswer: String,
    val hintOrFormula: String = "",
    val subject: AcademicSubject = AcademicSubject.MATH,
    var isMastered: Boolean = false
)

data class FlashcardDeck(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val subject: AcademicSubject,
    val cards: List<FlashcardItem>,
    val createdAt: Long = System.currentTimeMillis()
) {
    val masteredCount: Int get() = cards.count { it.isMastered }
    val progressPercent: Float get() = if (cards.isEmpty()) 0f else masteredCount.toFloat() / cards.size.toFloat()
}
