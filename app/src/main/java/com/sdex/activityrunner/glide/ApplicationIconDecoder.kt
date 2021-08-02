package com.sdex.activityrunner.glide

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.drawable.DrawableResource
import com.bumptech.glide.util.Util
import com.sdex.activityrunner.db.cache.ApplicationModel

internal class ApplicationIconDecoder(
    private val context: Context
) : ResourceDecoder<ApplicationModel, Drawable> {

    override fun decode(
        source: ApplicationModel, width: Int, height: Int,
        options: Options
    ): Resource<Drawable>? {
        val packageManager = context.packageManager
        var icon: Drawable
        try {
            icon = packageManager.getApplicationIcon(source.packageName)
        } catch (e: Exception) {
            icon = packageManager.defaultActivityIcon
        }

        return object : DrawableResource<Drawable>(icon) {
            override fun getResourceClass(): Class<Drawable> {
                return Drawable::class.java
            }

            override fun getSize(): Int { // best effort
                return if (drawable is BitmapDrawable) {
                    Util.getBitmapByteSize(drawable.bitmap)
                } else {
                    1
                }
            }

            override fun recycle() { /* not from our pool */
            }
        }
    }

    override fun handles(source: ApplicationModel, options: Options): Boolean {
        return true
    }
}
