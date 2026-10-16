package com.example.data.repository

import com.example.data.model.AcademicSubject
import com.example.data.model.AiCouncilMember
import com.example.data.model.AiPerspectiveAnswer
import com.example.data.model.CouncilDeliberation
import com.example.data.model.FlashcardDeck
import com.example.data.model.FlashcardItem
import com.example.data.model.QuizQuestion
import com.example.data.model.QuizSession
import com.example.data.model.StudyTask
import com.example.data.model.TaskPriority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StudyRepository {

    private val _tasks = MutableStateFlow<List<StudyTask>>(getInitialTasks())
    val tasks: StateFlow<List<StudyTask>> = _tasks.asStateFlow()

    private val _decks = MutableStateFlow<List<FlashcardDeck>>(getInitialFlashcardDecks())
    val decks: StateFlow<List<FlashcardDeck>> = _decks.asStateFlow()

    private val _savedCouncilInquiries = MutableStateFlow<List<CouncilDeliberation>>(getInitialCouncilInquiries())
    val savedCouncilInquiries: StateFlow<List<CouncilDeliberation>> = _savedCouncilInquiries.asStateFlow()

    // Task Management
    fun addTask(task: StudyTask) {
        _tasks.update { listOf(task) + it }
    }

    fun toggleTaskCompleted(taskId: String) {
        _tasks.update { current ->
            current.map {
                if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it
            }
        }
    }

    fun deleteTask(taskId: String) {
        _tasks.update { current -> current.filter { it.id != taskId } }
    }

    // Flashcard Management
    fun addDeck(deck: FlashcardDeck) {
        _decks.update { listOf(deck) + it }
    }

    fun toggleCardMastered(deckId: String, cardId: String) {
        _decks.update { currentDecks ->
            currentDecks.map { deck ->
                if (deck.id == deckId) {
                    val updatedCards = deck.cards.map { card ->
                        if (card.id == cardId) card.copy(isMastered = !card.isMastered) else card
                    }
                    deck.copy(cards = updatedCards)
                } else deck
            }
        }
    }

    // Council Deliberation History
    fun saveCouncilDeliberation(deliberation: CouncilDeliberation) {
        _savedCouncilInquiries.update { listOf(deliberation) + it }
    }

    // Built-in initial sample content
    companion object {
        fun getInitialTasks(): List<StudyTask> = listOf(
            StudyTask(
                title = "Calculus II: Taylor Series & Radius of Convergence",
                subject = AcademicSubject.MATH,
                dueDateString = "Today, 6:00 PM",
                estimatedMinutes = 45,
                priority = TaskPriority.HIGH
            ),
            StudyTask(
                title = "Organic Chem: Sn1 vs Sn2 Mechanism Lab Synthesis",
                subject = AcademicSubject.PHYSICS_CHEM,
                dueDateString = "Tomorrow, 11:59 PM",
                estimatedMinutes = 60,
                priority = TaskPriority.HIGH
            ),
            StudyTask(
                title = "Algorithms: Dynamic Programming Knapsack Proof",
                subject = AcademicSubject.COMPUTER_SCIENCE,
                dueDateString = "Thursday, 2:00 PM",
                estimatedMinutes = 50,
                priority = TaskPriority.MEDIUM
            ),
            StudyTask(
                title = "World History: Industrial Revolution Primary Source Essay",
                subject = AcademicSubject.HISTORY_SOCIAL,
                dueDateString = "Friday, 5:00 PM",
                estimatedMinutes = 90,
                priority = TaskPriority.MEDIUM
            )
        )

        fun getInitialFlashcardDecks(): List<FlashcardDeck> = listOf(
            FlashcardDeck(
                id = "deck_calc",
                title = "Calculus & Derivatives Mastery",
                description = "High-yield differentiation, integration techniques, and limits.",
                subject = AcademicSubject.MATH,
                cards = listOf(
                    FlashcardItem(
                        frontPrompt = "What is the Product Rule for differentiation?",
                        backAnswer = "d/dx [f(x)g(x)] = f'(x)g(x) + f(x)g'(x)",
                        hintOrFormula = "'Derivative of the first times second, plus first times derivative of second'",
                        subject = AcademicSubject.MATH,
                        isMastered = true
                    ),
                    FlashcardItem(
                        frontPrompt = "What is the derivative of ln(x)?",
                        backAnswer = "1 / x (for x > 0)",
                        hintOrFormula = "Remember domain constraint x > 0",
                        subject = AcademicSubject.MATH,
                        isMastered = true
                    ),
                    FlashcardItem(
                        frontPrompt = "What does L'Hôpital's Rule state?",
                        backAnswer = "If lim f(x)/g(x) yields 0/0 or ±∞/±∞, then lim f(x)/g(x) = lim f'(x)/g'(x), provided the limit exists.",
                        hintOrFormula = "Only applies to indeterminate forms 0/0 or ∞/∞!",
                        subject = AcademicSubject.MATH
                    ),
                    FlashcardItem(
                        frontPrompt = "What is Integration by Parts formula?",
                        backAnswer = "∫ u dv = u·v - ∫ v du",
                        hintOrFormula = "LIATE rule for picking 'u'",
                        subject = AcademicSubject.MATH
                    ),
                    FlashcardItem(
                        frontPrompt = "What is the Fundamental Theorem of Calculus (Part 1)?",
                        backAnswer = "If F(x) = ∫[a to x] f(t) dt, then F'(x) = f(x).",
                        hintOrFormula = "Differentiation is the inverse of integration",
                        subject = AcademicSubject.MATH
                    )
                )
            ),
            FlashcardDeck(
                id = "deck_ochem",
                title = "Organic Chem Reactions & Mechanisms",
                description = "Nucleophilic substitution, elimination, and functional groups.",
                subject = AcademicSubject.PHYSICS_CHEM,
                cards = listOf(
                    FlashcardItem(
                        frontPrompt = "Compare SN1 vs SN2 kinetics and stereochemistry:",
                        backAnswer = "SN1: Unimolecular (Rate = k[substrate]), forms carbocation intermediate, racemization. SN2: Bimolecular (Rate = k[substrate][nucleophile]), concerted backside attack, Walden inversion.",
                        hintOrFormula = "SN2: 2 molecules involved in rate-determining step, inversion of stereocenter",
                        subject = AcademicSubject.PHYSICS_CHEM,
                        isMastered = true
                    ),
                    FlashcardItem(
                        frontPrompt = "What is Markovnikov's Rule?",
                        backAnswer = "In addition of HX to an alkene, hydrogen attaches to the carbon with more hydrogens ('the rich get richer'), generating the more stable carbocation.",
                        hintOrFormula = "Carbocation stability: 3° > 2° > 1° > methyl",
                        subject = AcademicSubject.PHYSICS_CHEM
                    ),
                    FlashcardItem(
                        frontPrompt = "What solvent type favors an SN2 reaction?",
                        backAnswer = "Polar Aprotic solvents (e.g., DMSO, DMF, Acetone) because they solvate cations without neutralizing the nucleophile.",
                        hintOrFormula = "Polar aprotic does NOT form hydrogen bonds to nucleophiles",
                        subject = AcademicSubject.PHYSICS_CHEM
                    )
                )
            ),
            FlashcardDeck(
                id = "deck_cs",
                title = "Data Structures & Big-O Complexity",
                description = "Sorting, trees, graphs, and algorithmic runtimes.",
                subject = AcademicSubject.COMPUTER_SCIENCE,
                cards = listOf(
                    FlashcardItem(
                        frontPrompt = "What is the average and worst-case time complexity of QuickSort?",
                        backAnswer = "Average: O(n log n). Worst-case: O(n²) when the pivot chosen is consistently the smallest or largest element (e.g. already sorted array without randomized pivot).",
                        hintOrFormula = "Randomized or median-of-three pivot avoids O(n²)",
                        subject = AcademicSubject.COMPUTER_SCIENCE,
                        isMastered = true
                    ),
                    FlashcardItem(
                        frontPrompt = "Explain the difference between BFS and DFS traversal:",
                        backAnswer = "BFS uses a Queue and visits neighbor nodes level by level (optimal for shortest path in unweighted graphs). DFS uses a Stack/Recursion and visits as deep as possible before backtracking.",
                        hintOrFormula = "BFS = Queue / Level order; DFS = Stack / Deep dive",
                        subject = AcademicSubject.COMPUTER_SCIENCE
                    ),
                    FlashcardItem(
                        frontPrompt = "What are the two key properties of Dynamic Programming?",
                        backAnswer = "1. Optimal Substructure (solution can be constructed from optimal subproblem solutions). 2. Overlapping Subproblems (same subproblems solved repeatedly, memoized).",
                        hintOrFormula = "Memoization (top-down) or Tabulation (bottom-up)",
                        subject = AcademicSubject.COMPUTER_SCIENCE
                    )
                )
            )
        )

        fun getInitialCouncilInquiries(): List<CouncilDeliberation> = listOf(
            CouncilDeliberation(
                id = "inquiry_1",
                question = "Why does water expand when it freezes unlike most other liquids?",
                subject = AcademicSubject.PHYSICS_CHEM,
                timestamp = System.currentTimeMillis() - 7200000,
                masterConsensusAnswer = """
                    ## 🎓 Unified Council Master Answer: The Hydrogen Bonding Lattice

                    ### 1. The Core Scientific Consensus
                    Water is one of the rare substances that expands upon freezing due to the geometric requirements of **hydrogen bonding**. While liquid water consists of dynamic, densely packed molecules slipping past one another, ice forces each water molecule into a rigid tetrahedral crystalline hexagonal lattice.

                    ### 2. Step-by-Step Molecular Breakdown
                    - **Liquid State (Above 4°C):** Thermal energy causes molecules to tumble rapidly. Hydrogen bonds break and reform in picoseconds, allowing molecules to crowd closer together. Water reaches maximum density at **3.98°C**.
                    - **Cooling to 0°C:** Molecular motion slows. Attractive dipole-dipole hydrogen bonds between electronegative oxygen and electropositive hydrogen lock into place.
                    - **The Hexagonal Cage:** To maximize stability (4 hydrogen bonds per molecule), the molecules must arrange at roughly 104.5°–109.5° bond angles. This creates open spaces (hexagonal rings) in the crystal structure, increasing volume by approximately **9%** and reducing density.

                    ### 3. High-Yield Exam Takeaway
                    Density of ice is ~0.917 g/cm³ compared to water's ~1.00 g/cm³, explaining why icebergs float and aquatic life survives winters under insulating frozen lake surfaces!
                """.trimIndent(),
                consensusAgreementScore = 99,
                keySummaryPoints = listOf(
                    "Rigid tetrahedral hexagonal lattice creates open internal cavities.",
                    "Maximum density occurs at 3.98°C in liquid state.",
                    "All 4 AI models agree on hydrogen bond geometry as the primary driver."
                ),
                individualAnswers = listOf(
                    AiPerspectiveAnswer(
                        member = AiCouncilMember.SCHOLAR,
                        content = "Electronegativity differential between Oxygen (3.44) and Hydrogen (2.20) induces strong permanent dipole moments. Thermodynamics dictates minimal free energy configuration in ice as an open hexagonal wurtzite-like lattice with coordination number 4.",
                        keyTakeaway = "Thermodynamically favored tetrahedral coordination demands greater intermolecular volume.",
                        confidencePercent = 99,
                        latencyMs = 410
                    ),
                    AiPerspectiveAnswer(
                        member = AiCouncilMember.SOCRATIC,
                        content = "Imagine a crowded dance floor where people are swaying freely and bumping elbows (liquid water). When the music stops and everyone is told to hold hands at full arm's length in a geometric hexagram (ice), everyone has to spread out, taking up more space!",
                        keyTakeaway = "Holding hands at arm's length forces more open room between dancers than jostling close.",
                        confidencePercent = 98,
                        latencyMs = 380
                    ),
                    AiPerspectiveAnswer(
                        member = AiCouncilMember.FAST_SOLVE,
                        content = "Key Points for Exams:\n- Reason: Hexagonal crystal lattice via H-bonding.\n- Volume Change: ~9% expansion.\n- Density: ρ(ice) = 0.92 g/cm³ < ρ(water) = 1.00 g/cm³.\n- Max density point: 3.98°C (~4°C).",
                        keyTakeaway = "9% volume expansion, ice density 0.92 g/cm³, max water density at 4°C.",
                        confidencePercent = 99,
                        latencyMs = 210
                    ),
                    AiPerspectiveAnswer(
                        member = AiCouncilMember.FACT_CHECKER,
                        content = "Watch out for exam traps: Water does NOT continually expand as it cools. From 100°C down to 4°C, it contracts normally! The anomalous expansion only kicks in below 3.98°C.",
                        keyTakeaway = "Critical distinction: Water contracts from 100°C down to 4°C, and only expands from 4°C to 0°C.",
                        confidencePercent = 97,
                        latencyMs = 460
                    )
                ),
                followUpQuestions = listOf(
                    "What happens to ice under extreme planetary pressures (like Ice VII or Ice X)?",
                    "How does salinity affect the freezing point and density curve?"
                )
            )
        )

        fun getSampleQuiz(subject: AcademicSubject): QuizSession {
            val questions = when (subject) {
                AcademicSubject.MATH -> listOf(
                    QuizQuestion(
                        questionText = "What is the limit of (sin x) / x as x approaches 0?",
                        options = listOf("0", "1", "Does not exist", "Infinity"),
                        correctIndex = 1,
                        explanation = "By L'Hôpital's Rule: d/dx[sin x]/d/dx[x] = cos(x)/1. As x->0, cos(0) = 1. This is also derived geometrically via the Squeeze Theorem.",
                        conceptTip = "Fundamental trigonometric limit!"
                    ),
                    QuizQuestion(
                        questionText = "If f'(x) > 0 and f''(x) < 0 on an interval, the graph of f is:",
                        options = listOf(
                            "Decreasing and concave down",
                            "Increasing and concave up",
                            "Increasing and concave down",
                            "Constant"
                        ),
                        correctIndex = 2,
                        explanation = "f'(x) > 0 means the function is increasing (rising from left to right). f''(x) < 0 indicates the rate of increase is slowing down, meaning concave down (like a hill crest).",
                        conceptTip = "First derivative = direction; second derivative = concavity."
                    ),
                    QuizQuestion(
                        questionText = "Which matrix operation is NOT commutative in general?",
                        options = listOf(
                            "Matrix Addition (A + B)",
                            "Scalar Multiplication (c * A)",
                            "Matrix Multiplication (A * B)",
                            "Zero Matrix Addition (A + 0)"
                        ),
                        correctIndex = 2,
                        explanation = "In general, AB ≠ BA for matrices. Matrix multiplication depends on dimensional alignment and row-column order.",
                        conceptTip = "AB ≠ BA is a classic linear algebra trap."
                    )
                )
                AcademicSubject.COMPUTER_SCIENCE -> listOf(
                    QuizQuestion(
                        questionText = "Which data structure provides O(1) average lookup, insertion, and deletion?",
                        options = listOf("Binary Search Tree", "Hash Table", "Sorted Array", "Doubly Linked List"),
                        correctIndex = 1,
                        explanation = "Hash tables compute array indices via hash functions, providing expected O(1) amortized performance.",
                        conceptTip = "Watch out for hash collisions!"
                    ),
                    QuizQuestion(
                        questionText = "What is the worst-case space complexity of recursive Depth-First Search on a tree of height h?",
                        options = listOf("O(1)", "O(log h)", "O(h)", "O(2^h)"),
                        correctIndex = 2,
                        explanation = "The call stack memory is bounded by the maximum recursion depth, which is the height of the tree O(h).",
                        conceptTip = "Recursion uses stack space equal to tree depth."
                    )
                )
                else -> listOf(
                    QuizQuestion(
                        questionText = "Which organelle is responsible for generating the majority of cellular ATP in eukaryotes?",
                        options = listOf("Ribosome", "Endoplasmic Reticulum", "Mitochondria", "Golgi Apparatus"),
                        correctIndex = 2,
                        explanation = "Mitochondria carry out the Krebs cycle and oxidative phosphorylation via the electron transport chain, generating the bulk of ATP.",
                        conceptTip = "The powerhouse of the cell!"
                    ),
                    QuizQuestion(
                        questionText = "What is the primary function of DNA Polymerase III during prokaryotic replication?",
                        options = listOf(
                            "Unwinding the double helix",
                            "Synthesizing the leading and lagging daughter DNA strands 5' to 3'",
                            "Replacing RNA primers with DNA",
                            "Joining Okazaki fragments with phosphodiester bonds"
                        ),
                        correctIndex = 1,
                        explanation = "DNA Polymerase III is the primary catalytic replicase that adds complementary nucleotides in the 5' to 3' direction.",
                        conceptTip = "Polymerase adds nucleotides 5' -> 3' only."
                    )
                )
            }

            return QuizSession(
                title = "${subject.title} Practice Assessment",
                subject = subject,
                questions = questions
            )
        }
    }
}
