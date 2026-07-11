package com.sdex.activityrunner.app.launcher

import android.content.pm.PackageManager
import android.view.KeyEvent
import com.sdex.activityrunner.app.launcher.ShizukuRunner.isServiceRunning
import rikka.shizuku.Shizuku
import timber.log.Timber

/**
 * Runs shell commands through the Shizuku/Sui service, which executes them as shell (uid 2000) or
 * root. Used by the Shizuku launch method to inject the assist key and, optionally, to grant
 * `WRITE_SECURE_SETTINGS` to this app on-device (shell may grant development-protection permissions).
 *
 * Commands are run via the `Shizuku.newProcess` helper (reached by reflection because it is
 * `@RestrictTo`); it is a Shizuku library method, so no framework hidden-API access is involved.
 */
object ShizukuRunner {

    const val PERMISSION_REQUEST_CODE = 4001

    /** Whether the Shizuku/Sui service is installed and running (v11+ binder alive). */
    fun isServiceRunning(): Boolean = try {
        Shizuku.pingBinder() && !Shizuku.isPreV11()
    } catch (e: Exception) {
        Timber.e(e)
        false
    }

    /** Whether this app has been authorized by Shizuku. Only meaningful when [isServiceRunning]. */
    fun isPermissionGranted(): Boolean = try {
        Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
    } catch (e: Exception) {
        Timber.e(e)
        false
    }

    /** Shows the Shizuku authorization prompt and reports the result on the main thread. */
    fun requestPermission(onResult: (granted: Boolean) -> Unit) {
        val listener = object : Shizuku.OnRequestPermissionResultListener {
            override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                if (requestCode != PERMISSION_REQUEST_CODE) return
                Shizuku.removeRequestPermissionResultListener(this)
                onResult(grantResult == PackageManager.PERMISSION_GRANTED)
            }
        }
        Shizuku.addRequestPermissionResultListener(listener)
        Shizuku.requestPermission(PERMISSION_REQUEST_CODE)
    }

    /** Injects a `KEYCODE_ASSIST` press so the system reads the swapped "assistant" setting. */
    fun injectAssistKey(): Boolean =
        exec("input", "keyevent", KeyEvent.KEYCODE_ASSIST.toString())

    /** Grants `WRITE_SECURE_SETTINGS` to [packageName] (works because shell may grant it). */
    fun grantWriteSecureSettings(packageName: String): Boolean =
        exec("pm", "grant", packageName, "android.permission.WRITE_SECURE_SETTINGS")

    private fun exec(vararg command: String): Boolean {
        var process: Process? = null
        return try {
            process = newProcess(command)
            val exitCode = process.waitFor()
            Timber.d("Shizuku exec %s -> %d", command.joinToString(" "), exitCode)
            exitCode == 0
        } catch (e: Exception) {
            Timber.e(e, "Shizuku exec failed: %s", command.joinToString(" "))
            false
        } finally {
            process?.destroy()
        }
    }

    private fun newProcess(command: Array<out String>): Process {
        val method = Shizuku::class.java.getDeclaredMethod(
            "newProcess",
            Array<String>::class.java,
            Array<String>::class.java,
            String::class.java,
        )
        method.isAccessible = true
        return method.invoke(null, command, null, null) as Process
    }
}
