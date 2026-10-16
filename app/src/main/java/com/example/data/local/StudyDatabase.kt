package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [StudyTaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StudyDatabase : RoomDatabase() {

    abstract fun studyTaskDao(): StudyTaskDao

    companion object {
        @Volatile
        private var INSTANCE: StudyDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): StudyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudyDatabase::class.java,
                    "omnistudy_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateInitialTasks(database.studyTaskDao())
                    }
                }
            }

            suspend fun populateInitialTasks(dao: StudyTaskDao) {
                val now = System.currentTimeMillis()
                val oneHour = 3600 * 1000L
                val oneDay = 24 * oneHour

                val initialTasks = listOf(
                    StudyTaskEntity(
                        id = "task_init_1",
                        title = "Calculus II: Taylor Series & Convergence Radius",
                        subject = "Mathematics",
                        taskType = "Problem Set",
                        deadlineTimestamp = now + (4 * oneHour), // Today in 4 hours
                        estimatedMinutes = 45,
                        priority = "Urgent",
                        isCompleted = false,
                        notes = "Review Ratio Test and Root Test formulas before starting problem 14.",
                        aiStudyTip = "Focus on the interval endpoints: always test x = -R and x = +R separately for convergence!"
                    ),
                    StudyTaskEntity(
                        id = "task_init_2",
                        title = "Organic Chem: Sn1 vs Sn2 Lab Mechanism Report",
                        subject = "Physics & Chem",
                        taskType = "Assignment",
                        deadlineTimestamp = now + (28 * oneHour), // Tomorrow evening
                        estimatedMinutes = 60,
                        priority = "Urgent",
                        isCompleted = false,
                        notes = "Include polar aprotic solvent chart and carbocation stability diagrams.",
                        aiStudyTip = "Explain why Walden inversion occurs strictly in SN2 due to backside nucleophilic attack."
                    ),
                    StudyTaskEntity(
                        id = "task_init_3",
                        title = "Algorithms: Dynamic Programming Knapsack Proof",
                        subject = "Computer Science",
                        taskType = "Assignment",
                        deadlineTimestamp = now + (3 * oneDay), // In 3 days
                        estimatedMinutes = 50,
                        priority = "Important",
                        isCompleted = false,
                        notes = "Prove optimal substructure and overlapping subproblems.",
                        aiStudyTip = "Draw the 2D DP matrix table first before writing the recurrence relation code."
                    ),
                    StudyTaskEntity(
                        id = "task_init_4",
                        title = "Cellular Biology: Krebs Cycle & ATP Yield",
                        subject = "Biology & Med",
                        taskType = "Exam",
                        deadlineTimestamp = now + (5 * oneDay),
                        estimatedMinutes = 75,
                        priority = "Important",
                        isCompleted = false,
                        notes = "Memorize net NADH, FADH2, and GTP yield per glucose molecule.",
                        aiStudyTip = "Track carbons: Citrate (6C) -> alpha-ketoglutarate (5C) -> Succinate (4C)."
                    ),
                    StudyTaskEntity(
                        id = "task_init_5",
                        title = "World History: Industrial Revolution Essay Outline",
                        subject = "History & Social",
                        taskType = "Project",
                        deadlineTimestamp = now + (6 * oneDay),
                        estimatedMinutes = 40,
                        priority = "Standard",
                        isCompleted = true,
                        notes = "Outline steam engine adoption and demographic shifts.",
                        aiStudyTip = "Correlate coal basin geography directly with urbanization clusters."
                    )
                )
                dao.insertAll(initialTasks)
            }
        }
    }
}
