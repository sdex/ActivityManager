package com.sdex.activityrunner.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import com.sdex.activityrunner.db.cache.ApplicationModel

internal class ApplicationIconModelLoader :
    ModelLoader<ApplicationModel, ApplicationModel> {

    override fun buildLoadData(
        applicationInfo: ApplicationModel,
        width: Int, height: Int, options: Options
    ): ModelLoader.LoadData<ApplicationModel> {
        return ModelLoader.LoadData(
            ObjectKey(applicationInfo),
            object : DataFetcher<ApplicationModel> {
                override fun loadData(
                    priority: Priority,
                    callback: DataFetcher.DataCallback<in ApplicationModel>
                ) {
                    callback.onDataReady(applicationInfo)
                }

                override fun cleanup() {

                }

                override fun cancel() {

                }

                override fun getDataClass(): Class<ApplicationModel> {
                    return ApplicationModel::class.java
                }

                override fun getDataSource(): DataSource {
                    return DataSource.LOCAL
                }
            })
    }

    override fun handles(applicationInfo: ApplicationModel) = true
}
