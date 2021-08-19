package com.sdex.activityrunner.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

import timber.log.Timber;

public class RootUtils {

    public static boolean isSuAvailable() {
        String result = execute("id");
        return result != null && result.contains("uid=0(");
    }

    @Nullable
    public static String execute(@NonNull String command) {
        String result = null;
        try {
            Process exec = Runtime.getRuntime().exec("su\n");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(exec.getInputStream()));
            DataOutputStream dataOutputStream = new DataOutputStream(exec.getOutputStream());
            dataOutputStream.writeBytes(command);
            dataOutputStream.writeBytes("\nexit\n");
            dataOutputStream.flush();
            try {
                exec.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (bufferedReader.ready()) {
                StringBuilder builder = new StringBuilder();
                String aux;
                while ((aux = bufferedReader.readLine()) != null) {
                    builder.append(aux);
                }
                result = builder.toString();
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return result;
    }

}
