package com.sdex.activityrunner.intent.analyzer

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class IntentInterceptorTest {

    private val context: Context = RuntimeEnvironment.getApplication()

    @Test
    fun `the alias is declared in the manifest`() {
        val component = ComponentName(
            context.packageName,
            "com.sdex.activityrunner.intent.analyzer.IntentInterceptorActivity",
        )

        val info = context.packageManager.getActivityInfo(
            component,
            PackageManager.MATCH_DISABLED_COMPONENTS,
        )

        assertThat(info.targetActivity)
            .isEqualTo("com.sdex.activityrunner.intent.analyzer.IntentAnalyzerActivity")
    }

    @Test
    fun `interception is off until it is turned on`() {
        assertThat(IntentInterceptor.isEnabled(context)).isFalse()

        IntentInterceptor.setEnabled(context, true)

        assertThat(IntentInterceptor.isEnabled(context)).isTrue()
    }

    @Test
    fun `disabling reverts the component`() {
        IntentInterceptor.setEnabled(context, true)

        IntentInterceptor.setEnabled(context, false)

        assertThat(IntentInterceptor.isEnabled(context)).isFalse()
    }

    @Test
    fun `sync applies the stored preference`() {
        IntentInterceptor.sync(context, true)

        assertThat(IntentInterceptor.isEnabled(context)).isTrue()

        IntentInterceptor.sync(context, false)

        assertThat(IntentInterceptor.isEnabled(context)).isFalse()
    }
}
