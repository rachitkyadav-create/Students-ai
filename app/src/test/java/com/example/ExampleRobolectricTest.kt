package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.StudyDatabase
import com.example.data.local.StudyTaskEntity
import com.example.data.repository.StudyRepository
import com.example.data.repository.TaskRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var database: StudyDatabase

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, StudyDatabase::class.java)
      .allowMainThreadQueries()
      .build()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("OmniStudy AI", appName)
  }

  @Test
  fun `verify initial academic repository data`() {
    val repo = StudyRepository()
    assertTrue(repo.tasks.value.isNotEmpty())
    assertTrue(repo.decks.value.isNotEmpty())
    assertTrue(repo.savedCouncilInquiries.value.isNotEmpty())

    val firstDeliberation = repo.savedCouncilInquiries.value.first()
    assertEquals(4, firstDeliberation.individualAnswers.size)
    assertNotNull(firstDeliberation.masterConsensusAnswer)
  }

  @Test
  fun `verify Room StudyTaskDao operations`() = runBlocking {
    val dao = database.studyTaskDao()
    val repo = TaskRepository(dao)

    val task = StudyTaskEntity(
      id = "test_task_1",
      title = "Linear Algebra Eigenvalues HW",
      subject = "Mathematics",
      taskType = "Problem Set",
      deadlineTimestamp = System.currentTimeMillis() + 3600000,
      priority = "Urgent",
      isCompleted = false
    )

    repo.insertTask(task)

    val allTasks = repo.allTasks.first()
    assertEquals(1, allTasks.size)
    assertEquals("Linear Algebra Eigenvalues HW", allTasks[0].title)

    // Toggle completion
    repo.toggleTaskCompleted("test_task_1", true)
    val updatedTask = repo.getTaskById("test_task_1")
    assertNotNull(updatedTask)
    assertTrue(updatedTask!!.isCompleted)

    // Delete task
    repo.deleteTaskById("test_task_1")
    val tasksAfterDelete = repo.allTasks.first()
    assertTrue(tasksAfterDelete.isEmpty())
  }
}
