package com.sdex.activityrunner.glide

import android.content.Context
import android.graphics.drawable.Drawable

import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.manager.ConnectivityMonitor
import com.bumptech.glide.manager.ConnectivityMonitorFactory
import com.bumptech.glide.module.AppGlideModule
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.db.cache.ApplicationModel

@GlideModule
class MyAppGlideModule : AppGlideModule() {

  private val applicationModelLoaderFactory = object : ModelLoaderFactory<ApplicationModel, ApplicationModel> {
    override fun build(
      multiFactory: MultiModelLoaderFactory): ModelLoader<ApplicationModel, ApplicationModel> {
      return ApplicationIconModelLoader()
    }

    override fun teardown() {

    }
  }

  private val activityModelLoaderFactory = object : ModelLoaderFactory<ActivityModel, ActivityModel> {
    override fun build(
      multiFactory: MultiModelLoaderFactory): ModelLoader<ActivityModel, ActivityModel> {
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

  private val nullConnectivityMonitorFactory = ConnectivityMonitorFactory { _, _ -> nullConnectivityMonitor }

  override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
    registry.append(ApplicationModel::class.java, ApplicationModel::class.java, applicationModelLoaderFactory)
      .append(ApplicationModel::class.java, Drawable::class.java, ApplicationIconDecoder(context))
    registry.append(ActivityModel::class.java, ActivityModel::class.java, activityModelLoaderFactory)
      .append(ActivityModel::class.java, Drawable::class.java, ActivityIconDecoder(context))
  }

  override fun applyOptions(context: Context, builder: GlideBuilder) {
    builder.setConnectivityMonitorFactory(nullConnectivityMonitorFactory)
  }
}