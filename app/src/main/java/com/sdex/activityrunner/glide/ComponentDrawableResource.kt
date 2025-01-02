package com.sdex.activityrunner.glide

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.resource.drawable.DrawableResource
import com.bumptech.glide.util.Util
import kotlin.math.max

class ComponentDrawableResource(icon: Drawable) : DrawableResource<Drawable>(icon) {

    override fun getResourceClass(): Class<Drawable> {
        return Drawable::class.java
    }

    override fun getSize(): Int {
        val localDrawable = drawable
        return if (localDrawable is BitmapDrawable) {
            Util.getBitmapByteSize(localDrawable.bitmap)
        } else {
            // 4 bytes per pixel for ARGB_8888 Bitmaps is something of a reasonable approximation.
            // If there are no intrinsic bounds, we can fall back just to 1.
            return max(1, drawable.intrinsicWidth * drawable.intrinsicHeight * 4)
        }
    }

    override fun recycle() {
        /* not from our pool */
    }
}
