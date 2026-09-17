package com.sdex.activityrunner.intent.analyzer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class IntentAnalyzerViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val context: Context = RuntimeEnvironment.getApplication()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `analyze emits the rows of the intent`() = runTest(dispatcher) {
        val viewModel = viewModel()

        viewModel.items.test {
            assertThat(awaitItem()).isEmpty()

            viewModel.analyze(Intent(Intent.ACTION_SEND).setType("text/plain"), IntentSource())

            val items = awaitItem()
            assertThat(items.filterIsInstance<AnalyzerItem.Field>().map { it.value })
                .contains(Intent.ACTION_SEND)
        }
    }

    @Test
    fun `analyze runs once so a configuration change keeps the result`() = runTest(dispatcher) {
        val viewModel = viewModel()

        viewModel.items.test {
            assertThat(awaitItem()).isEmpty()

            viewModel.analyze(Intent(Intent.ACTION_SEND).setType("text/plain"), IntentSource())
            val items = awaitItem()

            viewModel.analyze(Intent(Intent.ACTION_VIEW), IntentSource())

            expectNoEvents()
            assertThat(items.filterIsInstance<AnalyzerItem.Field>().map { it.value })
                .contains(Intent.ACTION_SEND)
        }
    }

    @Test
    fun `launch params are empty before analyze`() {
        val viewModel = viewModel()

        assertThat(viewModel.getLaunchParams()).isNull()
        assertThat(viewModel.getUnsupportedUris()).isEmpty()
    }

    @Test
    fun `launch params drop the interceptor component`() = runTest(dispatcher) {
        val viewModel = viewModel()
        val intent = Intent(Intent.ACTION_SEND).apply {
            setType("text/plain")
            component = ComponentName(
                context.packageName,
                "com.sdex.activityrunner.intent.analyzer.IntentInterceptorActivity",
            )
        }

        viewModel.analyze(intent, IntentSource())

        val launchParams = viewModel.getLaunchParams()
        assertThat(launchParams?.packageName).isNull()
        assertThat(launchParams?.className).isNull()
        assertThat(launchParams?.action).isEqualTo(Intent.ACTION_SEND)
        assertThat(launchParams?.mimeType).isEqualTo("text/plain")
    }

    @Test
    fun `launch params keep a foreign component`() = runTest(dispatcher) {
        val viewModel = viewModel()
        val intent = Intent(Intent.ACTION_VIEW).apply {
            component = ComponentName("com.example.app", "com.example.app.MainActivity")
        }

        viewModel.analyze(intent, IntentSource())

        assertThat(viewModel.getLaunchParams()?.packageName).isEqualTo("com.example.app")
        assertThat(viewModel.getLaunchParams()?.className)
            .isEqualTo("com.example.app.MainActivity")
    }

    @Test
    fun `unsupported uris are reported for a shared stream`() = runTest(dispatcher) {
        val viewModel = viewModel()
        val uri = "content://media/external/images/1".toUri()

        viewModel.analyze(
            Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_STREAM, uri),
            IntentSource(),
        )

        assertThat(viewModel.getUnsupportedUris()).containsExactly(uri)
    }

    private fun viewModel() = IntentAnalyzerViewModel(
        context = context,
        intentAnalyzer = IntentAnalyzer(context),
        ioDispatcher = dispatcher,
    )
}
