package com.sdex.activityrunner.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

public class AppUtils {

    public static final String REPOSITORY = "https://github.com/sdex/ActivityManager";
    public static final String ISSUES_TRACKER = REPOSITORY + "/issues";
    public static final String CHANGELOG = REPOSITORY + "/blob/main/CHANGELOG.md";
    public static final String SUGGESTION_LINK = REPOSITORY + "/discussions/categories/ideas";
    public static final String TRANSLATE_LINK = "https://crowdin.com/project/activity-manager";

    public static void openLink(Context context, String url) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(context, "Failed to open link", Toast.LENGTH_SHORT).show();
        }
    }

    public static void sendEmail(Context context, String address, String subject, String text) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setData(Uri.parse("mailto:" + address));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openApp(Context context, String appPackageName) {
        if (isAppInstalled(context, appPackageName)) {
            try {
                Intent LaunchIntent = context.getPackageManager()
                        .getLaunchIntentForPackage(appPackageName);
                context.startActivity(LaunchIntent);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        openPlayStore(context, appPackageName);
    }

    public static void openPlayStore(Context context) {
        openPlayStore(context, context.getPackageName());
    }

    public static void openPlayStore(Context context, String appPackageName) {
        try {
            context.startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException anfe) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            } catch (Exception e) {
                Toast.makeText(context, "Failed to open Play Store", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static boolean isAppInstalled(Context context, String appPackageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(appPackageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return false;
    }
}
