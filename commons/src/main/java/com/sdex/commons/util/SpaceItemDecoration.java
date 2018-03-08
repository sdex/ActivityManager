package com.sdex.commons.util;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

  private final int space;

  public SpaceItemDecoration(Context context, @DimenRes int dimenRes) {
    this(context.getResources().getDimensionPixelSize(dimenRes));
  }

  private SpaceItemDecoration(int space) {
    this.space = space;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
    RecyclerView.State state) {
    outRect.left = space;
    outRect.right = space;
    outRect.bottom = space;

    if (parent.getChildAdapterPosition(view) == 0) {
      outRect.top = space;
    }
  }
}
