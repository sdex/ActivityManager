package com.sdex.activityrunner.glide

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.sdex.activityrunner.db.cache.ApplicationModel

internal class ApplicationIconDecoder(
    private val context: Context
) : ResourceDecoder<ApplicationModel, Drawable> {

    override fun decode(
        source: ApplicationModel,
        width: Int,
        height: Int,
        options: Options
    ): Resource<Drawable> {
        val packageManager = context.packageManager
        val icon: Drawable = try {
            packageManager.getApplicationIcon(source.packageName)
        } catch (_: Exception) {
            packageManager.defaultActivityIcon
        }
        return ComponentDrawableResource(icon)
    }

    override fun handles(source: ApplicationModel, options: Options) = true
}
