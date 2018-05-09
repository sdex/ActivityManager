package com.sdex.activityrunner.intent.dialog.source;

import android.os.Parcelable;

import java.util.ArrayList;

public interface SelectionDialogSource extends Parcelable {

  ArrayList<String> getList();

  String getItem(int position);
}
