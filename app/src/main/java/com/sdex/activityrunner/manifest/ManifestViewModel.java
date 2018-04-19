package com.sdex.activityrunner.manifest;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

public class ManifestViewModel extends AndroidViewModel {

  private MutableLiveData<String> manifestData;

  public ManifestViewModel(@NonNull Application application) {
    super(application);
  }

  public MutableLiveData<String> loadManifest(String packageName) {
    if (manifestData == null) {
      manifestData = new MutableLiveData<>();
      loadAndroidManifest(packageName);
    }
    return manifestData;
  }

  private void loadAndroidManifest(String packageName) {
    LoadTask loadTask = new LoadTask(getApplication(), packageName, manifestData);
    loadTask.execute();
  }

  private static class LoadTask extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private final String packageName;
    private final MutableLiveData<String> liveData;

    public LoadTask(Context context, String packageName, MutableLiveData<String> liveData) {
      this.context = context;
      this.packageName = packageName;
      this.liveData = liveData;
    }

    @Override
    protected Void doInBackground(Void... voids) {
      ManifestReader manifestReader = new ManifestReader();
      String androidManifest = manifestReader.loadAndroidManifest(context, packageName);
      liveData.postValue(androidManifest);
      return null;
    }
  }
}
