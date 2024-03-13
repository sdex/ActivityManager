package com.sdex.activityrunner.util;

import android.view.Menu;
import android.view.MenuItem;

public final class UIUtils {

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
}
