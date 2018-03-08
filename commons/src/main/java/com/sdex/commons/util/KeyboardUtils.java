package com.sdex.commons.util;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Author: Yuriy Mysochenko
 * Date: 05-Aug-15
 */
public class KeyboardUtils {

  public static void showKeyboard(View theView) {
    Context context = theView.getContext();
    Object service = context.getSystemService(Context.INPUT_METHOD_SERVICE);

    InputMethodManager imm = (InputMethodManager) service;
    if (imm != null) {
      imm.toggleSoftInput(
        InputMethodManager.SHOW_FORCED,
        InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
  }

  public static void hideKeyboard(View theView) {
    Context context = theView.getContext();
    Object service = context.getSystemService(Context.INPUT_METHOD_SERVICE);

    InputMethodManager imm = (InputMethodManager) service;
    if (imm != null) {
      imm.hideSoftInputFromWindow(theView.getWindowToken(), 0);
    }
  }

  /**
   * Dismiss soft keyboard if touch outside {@link EditText}
   *
   * @param view The root view
   */
  public static void dismissWhenTouchOutside(View view) {
    //Set up touch listener for non-text box views to hide keyboard.
    if (!(view instanceof EditText)) {
      view.setOnTouchListener(new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
          hideKeyboard(v);
          return false;
        }
      });
    }
    //If a layout container, iterate over children
    if (view instanceof ViewGroup) {
      for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
        View innerView = ((ViewGroup) view).getChildAt(i);
        dismissWhenTouchOutside(innerView);
      }
    }
  }

}