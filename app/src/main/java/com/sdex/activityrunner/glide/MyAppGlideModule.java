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
import com.sdex.activityrunner.db.application.ApplicationModel;

@GlideModule
public final class MyAppGlideModule extends AppGlideModule {

  @Override
  public void registerComponents(@NonNull Context context, @NonNull Glide glide,
                                 @NonNull Registry registry) {
    registry.append(ApplicationModel.class, ApplicationModel.class,
      new ModelLoaderFactory<ApplicationModel, ApplicationModel>() {
        @NonNull
        @Override
        public ModelLoader<ApplicationModel, ApplicationModel> build(
          @NonNull MultiModelLoaderFactory multiFactory) {
          return new ApplicationIconModelLoader();
        }

        @Override
        public void teardown() {

        }
      }).append(ApplicationModel.class, Drawable.class, new ApplicationIconDecoder(context));
  }
}