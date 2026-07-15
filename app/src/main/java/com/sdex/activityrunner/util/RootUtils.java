package com.sdex.activityrunner.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

import timber.log.Timber;

public class RootUtils {

    public static boolean isSuAvailable(@NonNull String suExecutable) {
        String result = execute(suExecutable, "id");
        return result != null && result.contains("uid=0(");
    }

    @Nullable
    public static String execute(@NonNull String suExecutable, @NonNull String command) {
        String result = null;
        try {
            Process exec = new ProcessBuilder(suExecutable.trim().split("\\s+"))
                .redirectErrorStream(true)
                .start();
            BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(exec.getInputStream()));
            DataOutputStream dataOutputStream = new DataOutputStream(exec.getOutputStream());
            dataOutputStream.writeBytes(command);
            dataOutputStream.writeBytes("\nexit\n");
            dataOutputStream.flush();
            StringBuilder builder = new StringBuilder();
            String aux;
            while ((aux = bufferedReader.readLine()) != null) {
                if (builder.length() > 0) {
                    builder.append('\n');
                }
                builder.append(aux);
            }
            try {
                exec.waitFor();
            } catch (InterruptedException e) {
                Timber.e(e);
            }
            if (builder.length() > 0) {
                result = builder.toString();
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return result;
    }

}
