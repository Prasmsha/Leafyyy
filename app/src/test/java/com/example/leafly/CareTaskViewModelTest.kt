package com.example.leafly

import com.example.leafly.Repo.CareTaskRepo
import com.example.leafly.ViewModel.CareTaskViewModel
import com.example.leafly.model.CareTaskModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class CareTaskViewModelTest {

    private lateinit var viewModel: CareTaskViewModel
    private val repo: CareTaskRepo = mock(CareTaskRepo::class.java)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CareTaskViewModel(repo)
    }

    @Test
    fun `getTasksByPlant updates tasks state flow on success`() = runTest {
        val plantId = "plant123"
        val expectedTasks = listOf(
            CareTaskModel(id = "1", plantId = plantId, title = "Watering"),
            CareTaskModel(id = "2", plantId = plantId, title = "Fertilizing")
        )
        
        `when`(repo.getTasksByPlant(plantId)).thenReturn(Result.success(expectedTasks))

        viewModel.getTasksByPlant(plantId)
        
        advanceUntilIdle()

        assertEquals(expectedTasks, viewModel.tasks.value)
        assertEquals(false, viewModel.loading.value)
    }

    @Test
    fun `getTasksByPlant sets empty list on failure`() = runTest {
        val plantId = "plant123"
        
        `when`(repo.getTasksByPlant(plantId)).thenReturn(Result.failure(Exception("Error")))

        viewModel.getTasksByPlant(plantId)
        
        advanceUntilIdle()

        assertEquals(emptyList<CareTaskModel>(), viewModel.tasks.value)
        assertEquals(false, viewModel.loading.value)
    }
}
