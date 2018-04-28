package com.sdex.activityrunner.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

import com.sdex.activityrunner.R;

public class RecyclerViewHelper {

  public static void addDivider(RecyclerView recyclerView) {
    Context context = recyclerView.getContext();
    final Drawable dividerDrawable = ContextCompat.getDrawable(context, R.drawable.list_divider);
    if (dividerDrawable != null) {
      DividerItemDecoration dividerItemDecoration =
        new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
      dividerItemDecoration.setDrawable(dividerDrawable);
      recyclerView.addItemDecoration(dividerItemDecoration);
    }
  }
}
