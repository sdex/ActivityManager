package com.sdex.commons.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DimenRes;
import androidx.core.content.ContextCompat;

public final class UIUtils {

    public static Drawable getDrawable(String name, Context context) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        return ContextCompat.getDrawable(context, resourceId);
    }

    public static void setViewEnabled(View view, boolean isEnabled) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewEnabled(child, isEnabled);
            }
        } else {
            view.setEnabled(isEnabled);
        }
    }

    public static int getDimension(Context context, @DimenRes int dimension) {
        Resources resources = context.getResources();
        return (int) resources.getDimension(dimension);
    }

    public static void setMenuItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) {
                item.setVisible(visible);
            }
        }
    }

    public static void setMenuItemsVisibility(Menu menu, boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            item.setVisible(visible);
        }
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
