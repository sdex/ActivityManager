package com.sdex.activityrunner;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

  private final PackageManager packageManager;
  private MutableLiveData<List<PackageInfo>> liveData;

  public MainViewModel(@NonNull Application application) {
    super(application);
    packageManager = application.getPackageManager();
  }

  public MutableLiveData<List<PackageInfo>> getPackages() {
    if (liveData == null) {
      liveData = new MutableLiveData<>();
      loadPackages();
    }
    return liveData;
  }

  private void loadPackages() {
    Loader loader = new Loader(packageManager, liveData);
    loader.execute();
  }

  private static class Loader extends AsyncTask<Void, Void, Void> {

    private final PackageManager packageManager;
    private final MutableLiveData<List<PackageInfo>> liveData;

    public Loader(PackageManager packageManager,
      MutableLiveData<List<PackageInfo>> liveData) {
      this.packageManager = packageManager;
      this.liveData = liveData;
    }

    @Override
    protected Void doInBackground(Void... voids) {
      List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
      liveData.postValue(installedPackages);
      return null;
    }
  }
}
