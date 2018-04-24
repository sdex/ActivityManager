package com.sdex.activityrunner.glide;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;
import com.sdex.activityrunner.db.application.ApplicationModel;

class ApplicationIconModelLoader implements ModelLoader<ApplicationModel, ApplicationModel> {

  @Nullable
  @Override
  public LoadData<ApplicationModel> buildLoadData(@NonNull final ApplicationModel applicationInfo,
                                                  int width, int height, @NonNull Options options) {
    return new LoadData<>(new ObjectKey(applicationInfo), new DataFetcher<ApplicationModel>() {
      @Override
      public void loadData(@NonNull Priority priority,
                           @NonNull DataCallback<? super ApplicationModel> callback) {
        callback.onDataReady(applicationInfo);
      }

      @Override
      public void cleanup() {

      }

      @Override
      public void cancel() {

      }

      @NonNull
      @Override
      public Class<ApplicationModel> getDataClass() {
        return ApplicationModel.class;
      }

      @NonNull
      @Override
      public DataSource getDataSource() {
        return DataSource.LOCAL;
      }
    });
  }

  @Override
  public boolean handles(@NonNull ApplicationModel applicationInfo) {
    return true;
  }
}