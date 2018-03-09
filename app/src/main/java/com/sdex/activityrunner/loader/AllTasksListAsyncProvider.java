package com.sdex.activityrunner.loader;

import android.content.Context;
import com.sdex.activityrunner.AllTasksListAdapter;

@Deprecated
public class AllTasksListAsyncProvider extends AsyncProvider<AllTasksListAdapter> {

  public AllTasksListAsyncProvider(Context context,
    AsyncProvider.Listener<AllTasksListAdapter> listener) {
    super(context, listener, true);
  }

  @Override
  protected AllTasksListAdapter run(Updater updater) {
    return new AllTasksListAdapter(getContext(), updater);
  }
}
