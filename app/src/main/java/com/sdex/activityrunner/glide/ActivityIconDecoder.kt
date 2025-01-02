package com.sdex.activityrunner.glide

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.sdex.activityrunner.app.ActivityModel

internal class ActivityIconDecoder(
    private val context: Context
) : ResourceDecoder<ActivityModel, Drawable> {

    override fun decode(
        source: ActivityModel,
        width: Int,
        height: Int,
        options: Options
    ): Resource<Drawable> {
        val icon = getComponentIcon(context.packageManager, source.componentName)
        return ComponentDrawableResource(icon)
    }

    override fun handles(source: ActivityModel, options: Options) = true

    @Suppress("DEPRECATION")
    private fun getComponentIcon(pm: PackageManager, componentName: ComponentName): Drawable {
        return try {
            val intent = Intent()
            intent.component = componentName
            val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.resolveActivity(intent, PackageManager.ResolveInfoFlags.of(0))
            } else {
                pm.resolveActivity(intent, 0)
            }
            resolveInfo?.loadIcon(pm) ?: pm.defaultActivityIcon
        } catch (_: Exception) {
            pm.defaultActivityIcon
        }
    }
}
