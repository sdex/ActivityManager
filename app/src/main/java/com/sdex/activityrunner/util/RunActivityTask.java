package com.sdex.activityrunner.util;

import android.content.ComponentName;
import android.os.AsyncTask;
import android.util.Log;

import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RunActivityTask extends AsyncTask<Void, Void, Integer> {

  private static final String TAG = "RunActivityTask";
  private final ComponentName componentName;

  public RunActivityTask(ComponentName componentName) {
    this.componentName = componentName;
  }

  @Override
  protected Integer doInBackground(Void[] params) {
    String className = componentName.getClassName();
    if (className.contains("$")) {
      className = className.replace("$", "\\$");
    }

    Log.d(TAG, "class name: " + className);
    try {
      RootTools.getShell(true).add(new Command(0,
        "am start -n " +
          componentName.getPackageName() + "/" + className) {
        @Override
        public void commandOutput(int id, String line) {
          Log.d(TAG, "commandOutput: " + line);
        }

        @Override
        public void commandTerminated(int id, String reason) {
          Log.d(TAG, "commandTerminated: " + reason);
        }

        @Override
        public void commandCompleted(int id, int exitcode) {
          Log.d(TAG, "commandCompleted: " + exitcode);
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    } catch (TimeoutException e) {
      e.printStackTrace();
    } catch (RootDeniedException e) {
      e.printStackTrace();
    }
    return 0;
  }
}