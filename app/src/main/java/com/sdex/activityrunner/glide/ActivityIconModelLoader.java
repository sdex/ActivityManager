package com.sdex.activityrunner.glide;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;
import com.sdex.activityrunner.db.activity.ActivityModel;

class ActivityIconModelLoader implements ModelLoader<ActivityModel, ActivityModel> {

  @Nullable
  @Override
  public LoadData<ActivityModel> buildLoadData(@NonNull final ActivityModel applicationInfo,
                                               int width, int height, @NonNull Options options) {
    return new LoadData<>(new ObjectKey(applicationInfo), new DataFetcher<ActivityModel>() {
      @Override
      public void loadData(@NonNull Priority priority,
                           @NonNull DataFetcher.DataCallback<? super ActivityModel> callback) {
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
      public Class<ActivityModel> getDataClass() {
        return ActivityModel.class;
      }

      @NonNull
      @Override
      public DataSource getDataSource() {
        return DataSource.LOCAL;
      }
    });
  }

  @Override
  public boolean handles(@NonNull ActivityModel applicationInfo) {
    return true;
  }
}