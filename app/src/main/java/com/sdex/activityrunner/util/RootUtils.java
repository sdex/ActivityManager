package com.sdex.activityrunner.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class RootUtils {
    public static boolean isSuAvailable(@NonNull String suExecutable) {
        String result = execute(suExecutable, "id");
        return result != null && result.contains("uid=0(");
    }
    @Nullable
    public static String launchComponent(
        @NonNull String suExecutable,
        @NonNull String packageName,
        @NonNull String className
    ) {
        String command = "am start -n '" + packageName + "/" + className + "' 2>&1";
        Timber.d("Execute: \"%s\"", command);
        return execute(suExecutable, command);
    }

    @Nullable
    public static String execute(@NonNull String suExecutable, @NonNull String command) {
        String result = null;
        try {
            List<String> args = new ArrayList<>();
            for (String part : suExecutable.trim().split("\\s+")) {
                args.add(part);
            }
            if (args.contains("-c")) {
                int index = args.indexOf("-c");
                if (index == args.size() - 1) {
                    args.add(command);
                }
            } else {
                args.add("-c");
                args.add(command);
            }
            Process exec = new ProcessBuilder(args)
                .redirectErrorStream(true)
                .start();
            BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(exec.getInputStream()));
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