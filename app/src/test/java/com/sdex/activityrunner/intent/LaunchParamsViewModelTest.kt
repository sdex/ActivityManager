package com.sdex.activityrunner.intent

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.sdex.activityrunner.app.ActivityLauncher
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.db.history.HistoryModelDao
import com.sdex.activityrunner.db.history.HistoryRepository
import com.sdex.activityrunner.preferences.AppPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LaunchParamsViewModelTest {

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
    fun `setUseRoot updates state`() = runTest(dispatcher) {
        val viewModel = viewModel(FakeHistoryModelDao())

        assertThat(viewModel.launchParamsState.value.useRoot).isFalse()
        viewModel.setUseRoot(true)
        assertThat(viewModel.launchParamsState.value.useRoot).isTrue()
    }

    @Test
    fun `launch without root launches intent via ActivityLauncher`() = runTest(dispatcher) {
        val activityLauncher = mockk<ActivityLauncher>(relaxed = true)
        val viewModel = viewModel(FakeHistoryModelDao(), activityLauncher)
        viewModel.setLaunchParams(LaunchParams(packageName = "com.test", className = ".Main"))

        viewModel.launch(saveToHistory = false)

        verify { activityLauncher.launchIntent(any()) }
    }

    @Test
    fun `launch without root emits LaunchError when launch fails`() = runTest(dispatcher) {
        val activityLauncher = mockk<ActivityLauncher>()
        every { activityLauncher.launchIntent(any()) } returns "boom"
        val viewModel = viewModel(FakeHistoryModelDao(), activityLauncher)
        viewModel.setLaunchParams(LaunchParams(packageName = "com.test", className = ".Main"))

        viewModel.events.test {
            viewModel.launch(saveToHistory = false)
            val event = awaitItem()
            assertThat(event).isInstanceOf(LaunchParamsViewModel.LaunchEvent.LaunchError::class.java)
            assertThat((event as LaunchParamsViewModel.LaunchEvent.LaunchError).details)
                .isEqualTo("boom")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `launch with saveToHistory inserts into history`() = runTest(dispatcher) {
        val dao = FakeHistoryModelDao()
        val viewModel = viewModel(dao)
        viewModel.setLaunchParams(LaunchParams(packageName = "com.test", className = ".Main"))

        viewModel.launch(saveToHistory = true)
        advanceUntilIdle()

        assertThat(dao.history.value).hasSize(1)
        assertThat(dao.history.value.first().packageName).isEqualTo("com.test")
    }

    @Test
    fun `launch without saveToHistory does not touch history`() = runTest(dispatcher) {
        val dao = FakeHistoryModelDao()
        val viewModel = viewModel(dao)
        viewModel.setLaunchParams(LaunchParams(packageName = "com.test", className = ".Main"))

        viewModel.launch(saveToHistory = false)
        advanceUntilIdle()

        assertThat(dao.history.value).isEmpty()
    }

    private fun viewModel(
        dao: HistoryModelDao,
        activityLauncher: ActivityLauncher = mockk(relaxed = true),
    ) = LaunchParamsViewModel(
        historyRepository = HistoryRepository(dao),
        appPreferences = mockk<AppPreferences>(relaxed = true),
        activityLauncher = activityLauncher,
        ioDispatcher = dispatcher,
    )

    private class FakeHistoryModelDao(
        initialHistory: List<HistoryModel> = emptyList(),
    ) : HistoryModelDao {

        val history = MutableStateFlow(initialHistory)

        override suspend fun insert(vararg model: HistoryModel) {
            history.value = model.toList() + history.value
        }

        override suspend fun update(vararg models: HistoryModel) = Unit

        override suspend fun delete(vararg models: HistoryModel) = Unit

        override fun getHistory(): Flow<List<HistoryModel>> = history

        override suspend fun clean() {
            history.value = emptyList()
        }
    }
}
