package com.sdex.activityrunner.manifest

import android.net.Uri
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class ManifestViewModelTest {

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
    fun `loadManifest emits loading and loaded states`() = runTest(dispatcher) {
        val manifestReader = FakeManifestReader(MANIFEST)
        val viewModel = viewModel(manifestReader = manifestReader)

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ManifestUiState.Idle)

            viewModel.loadManifest(PACKAGE_NAME)

            assertThat(awaitItem()).isEqualTo(ManifestUiState.Loading)
            assertThat(awaitItem()).isEqualTo(ManifestUiState.Loaded(MANIFEST))
            assertThat(manifestReader.loadedPackages).containsExactly(PACKAGE_NAME)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadManifest emits failed state when reader returns null`() = runTest(dispatcher) {
        val viewModel = viewModel(manifestReader = FakeManifestReader(null))

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ManifestUiState.Idle)

            viewModel.loadManifest(PACKAGE_NAME)

            assertThat(awaitItem()).isEqualTo(ManifestUiState.Loading)
            assertThat(awaitItem()).isEqualTo(ManifestUiState.Failed)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadManifest does not reload an already loaded manifest`() = runTest(dispatcher) {
        val manifestReader = FakeManifestReader(MANIFEST)
        val viewModel = viewModel(manifestReader = manifestReader)

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(ManifestUiState.Idle)

            viewModel.loadManifest(PACKAGE_NAME)
            assertThat(awaitItem()).isEqualTo(ManifestUiState.Loading)
            assertThat(awaitItem()).isEqualTo(ManifestUiState.Loaded(MANIFEST))

            viewModel.loadManifest(PACKAGE_NAME)
            advanceUntilIdle()

            assertThat(manifestReader.loadedPackages).containsExactly(PACKAGE_NAME)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `export writes loaded manifest in external coroutine scope`() = runTest(dispatcher) {
        val manifestWriter = FakeManifestWriter()
        val viewModel = viewModel(
            manifestReader = FakeManifestReader(MANIFEST),
            manifestWriter = manifestWriter,
        )
        val uri = mockk<Uri>()

        viewModel.loadManifest(PACKAGE_NAME)
        advanceUntilIdle()

        viewModel.export(uri)
        advanceUntilIdle()

        assertThat(manifestWriter.writes).containsExactly(ManifestWrite(uri, MANIFEST))
    }

    @Test
    fun `export does nothing before manifest is loaded`() = runTest(dispatcher) {
        val manifestWriter = FakeManifestWriter()
        val viewModel = viewModel(manifestWriter = manifestWriter)

        viewModel.export(mockk())
        advanceUntilIdle()

        assertThat(manifestWriter.writes).isEmpty()
    }

    private fun viewModel(
        manifestReader: ManifestReader = FakeManifestReader(MANIFEST),
        manifestWriter: FakeManifestWriter = FakeManifestWriter(),
    ) = ManifestViewModel(
        manifestReader = manifestReader,
        manifestWriter = manifestWriter,
        coroutineScope = TestScope(dispatcher),
        ioDispatcher = dispatcher,
    )

    private class FakeManifestReader(
        private val manifest: String?,
    ) : ManifestReader {

        val loadedPackages = mutableListOf<String>()

        override fun load(packageName: String): String? {
            loadedPackages += packageName
            return manifest
        }
    }

    private class FakeManifestWriter : ManifestWriter {

        val writes = mutableListOf<ManifestWrite>()

        override fun write(uri: Uri, data: String) {
            writes += ManifestWrite(uri, data)
        }
    }

    private data class ManifestWrite(
        val uri: Uri,
        val data: String,
    )

    private companion object {
        const val PACKAGE_NAME = "com.test.app"
        const val MANIFEST = "<manifest />"
    }
}
