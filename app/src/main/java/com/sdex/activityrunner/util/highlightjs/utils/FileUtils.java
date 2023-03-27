package com.sdex.activityrunner.util.highlightjs.utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * This Class was created by Patrick J
 * on 09.06.16. For more Details and Licensing
 * have a look at the README.md
 */

public class FileUtils {

    public interface Callback {
        void onDataLoaded(boolean success, String source);
    }

    public static String loadSourceFromFile(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append("\n");
            fileInputStream.close();
            return stringBuilder.toString();
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }

    public static void loadSourceFromUrl(Callback callback, URL url) {
        new NetworkLoader(callback, url).execute();
    }

    private static class NetworkLoader extends AsyncTask<Void, Void, String> {

        private final Callback callback;
        private final URL url;

        private NetworkLoader(Callback callback, URL url) {
            this.callback = callback;
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URLConnection urlConnection = url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                    stringBuilder.append(line).append("\n");
                bufferedReader.close();
                return stringBuilder.toString();
            } catch (IOException io) {
                io.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onCancelled() {
            callback.onDataLoaded(false, null);
        }

        @Override
        protected void onPostExecute(String s) {
            callback.onDataLoaded(s != null, s);
        }
    }

}
