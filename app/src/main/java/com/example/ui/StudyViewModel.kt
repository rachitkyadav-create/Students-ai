package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AcademicSubject
import com.example.data.model.AiCouncilMember
import com.example.data.model.CouncilDeliberation
import com.example.data.model.EssayReviewReport
import com.example.data.model.FlashcardDeck
import com.example.data.model.PomodoroSessionState
import com.example.data.model.QuizSession
import com.example.data.model.StudyTask
import com.example.data.model.TaskPriority
import com.example.data.remote.GeminiApiClient
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class DeliberationUiState {
    data object Idle : DeliberationUiState()
    data class Loading(val stageMessage: String) : DeliberationUiState()
    data class Success(val deliberation: CouncilDeliberation) : DeliberationUiState()
    data class Error(val message: String) : DeliberationUiState()
}

class StudyViewModel(
    private val repository: StudyRepository = StudyRepository()
) : ViewModel() {

    // Repository flows
    val tasks = repository.tasks
    val decks = repository.decks
    val savedCouncilInquiries = repository.savedCouncilInquiries

    // Navigation / Tab state: 0 = Home, 1 = Multi-AI Council, 2 = Flashcards, 3 = Practice Quiz, 4 = Essay Grader, 5 = Pomodoro & Planner
    private val _currentScreenTab = MutableStateFlow(0)
    val currentScreenTab: StateFlow<Int> = _currentScreenTab.asStateFlow()

    fun setScreenTab(tab: Int) {
        _currentScreenTab.value = tab
    }

    // ---------------- Multi-AI Council State ----------------
    private val _selectedSubject = MutableStateFlow(AcademicSubject.MATH)
    val selectedSubject: StateFlow<AcademicSubject> = _selectedSubject.asStateFlow()

    private val _questionInput = MutableStateFlow("")
    val questionInput: StateFlow<String> = _questionInput.asStateFlow()

    private val _selectedCouncilMembers = MutableStateFlow<Set<AiCouncilMember>>(
        AiCouncilMember.entries.toSet()
    )
    val selectedCouncilMembers: StateFlow<Set<AiCouncilMember>> = _selectedCouncilMembers.asStateFlow()

    private val _deliberationState = MutableStateFlow<DeliberationUiState>(
        // Seed with preloaded deliberation for immediate engagement
        repository.savedCouncilInquiries.value.firstOrNull()?.let { DeliberationUiState.Success(it) } ?: DeliberationUiState.Idle
    )
    val deliberationState: StateFlow<DeliberationUiState> = _deliberationState.asStateFlow()

    private val _selectedCouncilTab = MutableStateFlow("consensus") // "consensus" or member id
    val selectedCouncilTab: StateFlow<String> = _selectedCouncilTab.asStateFlow()

    fun setSelectedSubject(subject: AcademicSubject) {
        _selectedSubject.value = subject
    }

    fun setQuestionInput(text: String) {
        _questionInput.value = text
    }

    fun toggleCouncilMember(member: AiCouncilMember) {
        _selectedCouncilMembers.update { current ->
            if (current.contains(member)) {
                if (current.size > 1) current - member else current
            } else {
                current + member
            }
        }
    }

    fun setSelectedCouncilTab(tab: String) {
        _selectedCouncilTab.value = tab
    }

    fun askCouncil(customQuestion: String? = null) {
        val q = (customQuestion ?: _questionInput.value).trim()
        if (q.isBlank()) return

        viewModelScope.launch {
            _deliberationState.value = DeliberationUiState.Loading("Convening AI Council...")
            val stages = listOf(
                "Querying Dr. Scholar for theoretical rigor...",
                "Consulting Prof. Socratic for intuitive models...",
                "Running FastSolve algorithmic calculations...",
                "Auditing with Peer Reviewer for edge cases...",
                "Synthesizing collective Master Consensus Answer..."
            )

            // Dynamic deliberation progress
            val stageJob = launch {
                for (stage in stages) {
                    delay(380)
                    _deliberationState.value = DeliberationUiState.Loading(stage)
                }
            }

            try {
                val deliberation = GeminiApiClient.consultCouncil(
                    question = q,
                    subject = _selectedSubject.value,
                    members = _selectedCouncilMembers.value.toList()
                )
                stageJob.cancel()
                repository.saveCouncilDeliberation(deliberation)
                _deliberationState.value = DeliberationUiState.Success(deliberation)
                _selectedCouncilTab.value = "consensus"
                _questionInput.value = ""
            } catch (e: Exception) {
                stageJob.cancel()
                _deliberationState.value = DeliberationUiState.Error(e.message ?: "Failed to convene council")
            }
        }
    }

    fun loadHistoricalDeliberation(deliberation: CouncilDeliberation) {
        _deliberationState.value = DeliberationUiState.Success(deliberation)
        _selectedSubject.value = deliberation.subject
        _selectedCouncilTab.value = "consensus"
        _currentScreenTab.value = 1
    }

    // ---------------- Flashcards State ----------------
    private val _activeDeck = MutableStateFlow<FlashcardDeck?>(repository.decks.value.firstOrNull())
    val activeDeck: StateFlow<FlashcardDeck?> = _activeDeck.asStateFlow()

    private val _currentCardIndex = MutableStateFlow(0)
    val currentCardIndex: StateFlow<Int> = _currentCardIndex.asStateFlow()

    private val _isCardFlipped = MutableStateFlow(false)
    val isCardFlipped: StateFlow<Boolean> = _isCardFlipped.asStateFlow()

    private val _isGeneratingDeck = MutableStateFlow(false)
    val isGeneratingDeck: StateFlow<Boolean> = _isGeneratingDeck.asStateFlow()

    fun selectDeck(deck: FlashcardDeck) {
        _activeDeck.value = deck
        _currentCardIndex.value = 0
        _isCardFlipped.value = false
    }

    fun flipCard() {
        _isCardFlipped.update { !it }
    }

    fun nextCard() {
        val deck = _activeDeck.value ?: return
        if (_currentCardIndex.value < deck.cards.size - 1) {
            _currentCardIndex.update { it + 1 }
            _isCardFlipped.value = false
        }
    }

    fun previousCard() {
        if (_currentCardIndex.value > 0) {
            _currentCardIndex.update { it - 1 }
            _isCardFlipped.value = false
        }
    }

    fun toggleCurrentCardMastered() {
        val deck = _activeDeck.value ?: return
        val currentCard = deck.cards.getOrNull(_currentCardIndex.value) ?: return
        repository.toggleCardMastered(deck.id, currentCard.id)
        // Refresh active deck from repository
        val updated = repository.decks.value.find { it.id == deck.id }
        if (updated != null) {
            _activeDeck.value = updated
        }
    }

    fun generateNewFlashcards(topic: String, subject: AcademicSubject) {
        if (topic.isBlank()) return
        viewModelScope.launch {
            _isGeneratingDeck.value = true
            try {
                val cards = GeminiApiClient.generateFlashcards(topic, subject)
                val newDeck = FlashcardDeck(
                    title = "$topic Deck",
                    description = "AI-generated active recall cards for ${subject.title}.",
                    subject = subject,
                    cards = cards
                )
                repository.addDeck(newDeck)
                _activeDeck.value = newDeck
                _currentCardIndex.value = 0
                _isCardFlipped.value = false
            } finally {
                _isGeneratingDeck.value = false
            }
        }
    }

    // ---------------- Quiz State ----------------
    private val _activeQuiz = MutableStateFlow<QuizSession>(
        StudyRepository.getSampleQuiz(AcademicSubject.MATH)
    )
    val activeQuiz: StateFlow<QuizSession> = _activeQuiz.asStateFlow()

    private val _isGeneratingQuiz = MutableStateFlow(false)
    val isGeneratingQuiz: StateFlow<Boolean> = _isGeneratingQuiz.asStateFlow()

    fun selectQuizAnswer(questionIndex: Int, optionIndex: Int) {
        val currentQuiz = _activeQuiz.value
        val updatedAnswers = currentQuiz.userAnswers.toMutableMap().apply {
            put(questionIndex, optionIndex)
        }
        _activeQuiz.value = currentQuiz.copy(userAnswers = updatedAnswers)
    }

    fun submitQuiz() {
        _activeQuiz.value = _activeQuiz.value.copy(isSubmitted = true)
    }

    fun restartQuiz() {
        _activeQuiz.value = _activeQuiz.value.copy(
            currentIndex = 0,
            userAnswers = mutableMapOf(),
            isSubmitted = false
        )
    }

    fun loadSampleQuiz(subject: AcademicSubject) {
        _activeQuiz.value = StudyRepository.getSampleQuiz(subject)
    }

    fun generateCustomQuiz(topic: String, subject: AcademicSubject) {
        if (topic.isBlank()) return
        viewModelScope.launch {
            _isGeneratingQuiz.value = true
            try {
                val questions = GeminiApiClient.generateQuiz(topic, subject)
                _activeQuiz.value = QuizSession(
                    title = "$topic Practice Test",
                    subject = subject,
                    questions = questions
                )
            } finally {
                _isGeneratingQuiz.value = false
            }
        }
    }

    // ---------------- Essay Grader State ----------------
    private val _essayText = MutableStateFlow(
        "The impact of artificial intelligence on modern university pedagogy has introduced revolutionary paradigms. Students can now access personalized multi-perspective tutoring at scale, breaking traditional socioeconomic educational barriers."
    )
    val essayText: StateFlow<String> = _essayText.asStateFlow()

    private val _isGradingEssay = MutableStateFlow(false)
    val isGradingEssay: StateFlow<Boolean> = _isGradingEssay.asStateFlow()

    private val _essayReport = MutableStateFlow<EssayReviewReport?>(null)
    val essayReport: StateFlow<EssayReviewReport?> = _essayReport.asStateFlow()

    fun setEssayText(text: String) {
        _essayText.value = text
    }

    fun gradeEssay() {
        val text = _essayText.value.trim()
        if (text.isBlank()) return
        viewModelScope.launch {
            _isGradingEssay.value = true
            try {
                val report = GeminiApiClient.gradeEssay(text)
                _essayReport.value = report
            } finally {
                _isGradingEssay.value = false
            }
        }
    }

    // ---------------- Pomodoro & Study Planner State ----------------
    private val _pomodoroState = MutableStateFlow(PomodoroSessionState())
    val pomodoroState: StateFlow<PomodoroSessionState> = _pomodoroState.asStateFlow()

    private var timerJob: Job? = null

    fun togglePomodoroTimer() {
        val isRunning = _pomodoroState.value.isRunning
        if (isRunning) {
            timerJob?.cancel()
            _pomodoroState.update { it.copy(isRunning = false) }
        } else {
            _pomodoroState.update { it.copy(isRunning = true) }
            timerJob = viewModelScope.launch {
                while (_pomodoroState.value.secondsRemaining > 0 && _pomodoroState.value.isRunning) {
                    delay(1000)
                    _pomodoroState.update {
                        val next = it.secondsRemaining - 1
                        it.copy(secondsRemaining = next)
                    }
                }
                if (_pomodoroState.value.secondsRemaining <= 0) {
                    val wasBreak = _pomodoroState.value.isBreak
                    if (!wasBreak) {
                        _pomodoroState.update {
                            it.copy(
                                isRunning = false,
                                isBreak = true,
                                totalSeconds = 5 * 60,
                                secondsRemaining = 5 * 60,
                                completedPomodoros = it.completedPomodoros + 1
                            )
                        }
                    } else {
                        _pomodoroState.update {
                            it.copy(
                                isRunning = false,
                                isBreak = false,
                                totalSeconds = 25 * 60,
                                secondsRemaining = 25 * 60
                            )
                        }
                    }
                }
            }
        }
    }

    fun resetPomodoroTimer() {
        timerJob?.cancel()
        _pomodoroState.value = PomodoroSessionState()
    }

    fun toggleTask(taskId: String) {
        repository.toggleTaskCompleted(taskId)
    }

    fun addNewTask(title: String, subject: AcademicSubject, dueDate: String, priority: TaskPriority) {
        if (title.isBlank()) return
        val task = StudyTask(
            title = title,
            subject = subject,
            dueDateString = dueDate.ifBlank { "Tomorrow" },
            priority = priority
        )
        repository.addTask(task)
    }

    fun deleteTask(taskId: String) {
        repository.deleteTask(taskId)
    }
}
