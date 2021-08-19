package com.sdex.activityrunner.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import com.sdex.activityrunner.app.ActivityModel

internal class ActivityIconModelLoader : ModelLoader<ActivityModel, ActivityModel> {

    override fun buildLoadData(
        applicationInfo: ActivityModel,
        width: Int, height: Int, options: Options
    ): ModelLoader.LoadData<ActivityModel> {
        return ModelLoader.LoadData(
            ObjectKey(applicationInfo),
            object : DataFetcher<ActivityModel> {
                override fun loadData(
                    priority: Priority,
                    callback: DataFetcher.DataCallback<in ActivityModel>
                ) {
                    callback.onDataReady(applicationInfo)
                }

                override fun cleanup() {

                }

                override fun cancel() {

                }

                override fun getDataClass(): Class<ActivityModel> {
                    return ActivityModel::class.java
                }

                override fun getDataSource(): DataSource {
                    return DataSource.LOCAL
                }
            })
    }

    override fun handles(applicationInfo: ActivityModel) = true
}
