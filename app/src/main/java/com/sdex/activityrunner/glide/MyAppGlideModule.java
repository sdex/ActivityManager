package com.sdex.activityrunner.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.sdex.activityrunner.db.activity.ActivityModel;
import com.sdex.activityrunner.db.application.ApplicationModel;

@GlideModule
public final class MyAppGlideModule extends AppGlideModule {

  @Override
  public void registerComponents(@NonNull Context context, @NonNull Glide glide,
                                 @NonNull Registry registry) {
    registry.append(ApplicationModel.class, ApplicationModel.class, applicationModelLoaderFactory)
      .append(ApplicationModel.class, Drawable.class, new ApplicationIconDecoder(context));
    registry.append(ActivityModel.class, ActivityModel.class, activityModelLoaderFactory)
      .append(ActivityModel.class, Drawable.class, new ActivityIconDecoder(context));
  }

  private final ModelLoaderFactory<ApplicationModel, ApplicationModel>
    applicationModelLoaderFactory = new ModelLoaderFactory<ApplicationModel, ApplicationModel>() {
    @NonNull
    @Override
    public ModelLoader<ApplicationModel, ApplicationModel> build(
      @NonNull MultiModelLoaderFactory multiFactory) {
      return new ApplicationIconModelLoader();
    }

    @Override
    public void teardown() {

    }
  };

  private final ModelLoaderFactory<ActivityModel, ActivityModel>
    activityModelLoaderFactory = new ModelLoaderFactory<ActivityModel, ActivityModel>() {
    @NonNull
    @Override
    public ModelLoader<ActivityModel, ActivityModel> build(
      @NonNull MultiModelLoaderFactory multiFactory) {
      return new ActivityIconModelLoader();
    }

    @Override
    public void teardown() {

    }
  };
}