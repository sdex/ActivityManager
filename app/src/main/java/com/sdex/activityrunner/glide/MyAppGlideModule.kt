package com.sdex.activityrunner.glide

import android.app.ActivityManager
import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.manager.ConnectivityMonitor
import com.bumptech.glide.manager.ConnectivityMonitorFactory
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.db.cache.ApplicationModel

@GlideModule
class MyAppGlideModule : AppGlideModule() {

    private val applicationModelLoaderFactory =
        object : ModelLoaderFactory<ApplicationModel, ApplicationModel> {
            override fun build(
                multiFactory: MultiModelLoaderFactory
            ): ModelLoader<ApplicationModel, ApplicationModel> {
                return ApplicationIconModelLoader()
            }

            override fun teardown() {

            }
        }

    private val activityModelLoaderFactory =
        object : ModelLoaderFactory<ActivityModel, ActivityModel> {
            override fun build(
                multiFactory: MultiModelLoaderFactory
            ): ModelLoader<ActivityModel, ActivityModel> {
                return ActivityIconModelLoader()
            }

            override fun teardown() {

            }
        }

    /**
     * No need to monitor network, glide loads only local images
     */
    private val nullConnectivityMonitor = object : ConnectivityMonitor {
        override fun onStart() {
        }

        override fun onStop() {
        }

        override fun onDestroy() {
        }
    }

    private val nullConnectivityMonitorFactory =
        ConnectivityMonitorFactory { _, _ -> nullConnectivityMonitor }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(
            ApplicationModel::class.java, ApplicationModel::class.java,
            applicationModelLoaderFactory
        ).append(
            ApplicationModel::class.java, Drawable::class.java,
            ApplicationIconDecoder(context)
        ).append(
            ActivityModel::class.java, ActivityModel::class.java,
            activityModelLoaderFactory
        ).append(
            ActivityModel::class.java, Drawable::class.java,
            ActivityIconDecoder(context)
        )
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setConnectivityMonitorFactory(nullConnectivityMonitorFactory)
        val defaultRequestOptions = RequestOptions()
            .format(getDecodeFormat(context))
        builder.setDefaultRequestOptions(defaultRequestOptions)
    }

    private fun getDecodeFormat(context: Context): DecodeFormat {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        if (activityManager != null) {
            return if (activityManager.isLowRamDevice) {
                DecodeFormat.PREFER_RGB_565
            } else {
                DecodeFormat.PREFER_ARGB_8888
            }
        }
        return DecodeFormat.DEFAULT
    }
}
