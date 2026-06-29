package com.sdex.activityrunner.intent.history

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.db.history.HistoryModelDao
import com.sdex.activityrunner.db.history.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `list emits history from repository`() = runTest(dispatcher) {
        val first = historyModel(id = 1, name = "First")
        val second = historyModel(id = 2, name = "Second")
        val dao = FakeHistoryModelDao(listOf(first, second))
        val viewModel = viewModel(dao)

        viewModel.list.test {
            assertThat(awaitItem()).containsExactly(first, second).inOrder()
            dao.history.value = listOf(second)
            assertThat(awaitItem()).containsExactly(second)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteItem removes item from history`() = runTest(dispatcher) {
        val first = historyModel(id = 1, name = "First")
        val second = historyModel(id = 2, name = "Second")
        val viewModel = viewModel(FakeHistoryModelDao(listOf(first, second)))

        viewModel.list.test {
            assertThat(awaitItem()).containsExactly(first, second).inOrder()

            viewModel.deleteItem(first)
            advanceUntilIdle()

            assertThat(awaitItem()).containsExactly(second)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clear removes all history`() = runTest(dispatcher) {
        val first = historyModel(id = 1, name = "First")
        val second = historyModel(id = 2, name = "Second")
        val viewModel = viewModel(FakeHistoryModelDao(listOf(first, second)))

        viewModel.list.test {
            assertThat(awaitItem()).containsExactly(first, second).inOrder()

            viewModel.clear()
            advanceUntilIdle()

            assertThat(awaitItem()).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun viewModel(
        historyModelDao: HistoryModelDao,
    ) = HistoryViewModel(
        historyRepository = HistoryRepository(historyModelDao),
        coroutineScope = TestScope(dispatcher),
    )

    private fun historyModel(
        id: Int,
        name: String,
    ) = HistoryModel(
        id = id,
        timestamp = id.toLong(),
        name = name,
        packageName = "com.test.$id",
        className = ".Test$id",
    )

    private class FakeHistoryModelDao(
        initialHistory: List<HistoryModel>,
    ) : HistoryModelDao {

        val history = MutableStateFlow(initialHistory)

        override suspend fun insert(vararg model: HistoryModel) {
            history.value = model.toList() + history.value
        }

        override suspend fun update(vararg models: HistoryModel) {
            val updatedById = models.associateBy { it.id }
            history.value = history.value.map { updatedById[it.id] ?: it }
        }

        override suspend fun delete(vararg models: HistoryModel) {
            val idsToDelete = models.map { it.id }.toSet()
            history.value = history.value.filterNot { it.id in idsToDelete }
        }

        override fun getHistory(): Flow<List<HistoryModel>> = history

        override suspend fun clean() {
            history.value = emptyList()
        }
    }
}
