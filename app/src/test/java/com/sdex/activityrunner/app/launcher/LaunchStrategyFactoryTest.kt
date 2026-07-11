package com.sdex.activityrunner.app.launcher

import android.content.ComponentName
import android.content.Context
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LaunchStrategyFactoryTest {

    private val context = mockk<Context>()

    @Test
    fun `resolveAvailable prefers root over shizuku when both are available`() = runTest {
        val factory = factory(rootAvailable = true, shizukuAvailable = true)

        assertThat(factory.resolveAvailable(context)?.method).isEqualTo(LaunchMethod.ROOT)
    }

    @Test
    fun `resolveAvailable falls back to shizuku when root is unavailable`() = runTest {
        val factory = factory(rootAvailable = false, shizukuAvailable = true)

        assertThat(factory.resolveAvailable(context)?.method).isEqualTo(LaunchMethod.SHIZUKU)
    }

    @Test
    fun `resolveAvailable returns null when neither is available`() = runTest {
        val factory = factory(rootAvailable = false, shizukuAvailable = false)

        assertThat(factory.resolveAvailable(context)).isNull()
    }

    @Test
    fun `get returns the strategy for the requested method`() {
        val factory = factory(rootAvailable = false, shizukuAvailable = false)

        assertThat(factory.get(LaunchMethod.ROOT).method).isEqualTo(LaunchMethod.ROOT)
        assertThat(factory.get(LaunchMethod.SHIZUKU).method).isEqualTo(LaunchMethod.SHIZUKU)
    }

    private fun factory(rootAvailable: Boolean, shizukuAvailable: Boolean) = LaunchStrategyFactory(
        strategies = listOf(
            FakeStrategy(LaunchMethod.ROOT, rootAvailable),
            FakeStrategy(LaunchMethod.SHIZUKU, shizukuAvailable),
        ).associateBy { it.method },
    )

    private class FakeStrategy(
        override val method: LaunchMethod,
        private val available: Boolean,
    ) : LaunchStrategy {
        override suspend fun isAvailable(context: Context) = available
        override suspend fun launch(context: Context, component: ComponentName) =
            LaunchResult.Success
    }
}
