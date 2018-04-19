package com.sdex.activityrunner;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.Toast;

import com.sdex.activityrunner.db.activity.ActivityModel;
import com.sdex.activityrunner.intent.LaunchParamsActivity;
import com.sdex.activityrunner.preferences.AdvancedPreferences;
import com.sdex.activityrunner.service.AppLoaderIntentService;
import com.sdex.activityrunner.util.IntentUtils;
import com.sdex.activityrunner.util.RunActivityTask;

public class AppsListFragment extends Fragment {

  public static final String TAG = "AppsListFragment";

  private static final int ACTION_CREATE_SHORTCUT = 1;
  private static final int ACTION_LAUNCH_ACTIVITY = 2;
  private static final int ACTION_LAUNCH_ACTIVITY_PARAMS = 3;
  private static final int ACTION_LAUNCH_ACTIVITY_BY_ROOT = 4;

  private ExpandableListView list;
  private ApplicationsListAdapter adapter;
  private SwipeRefreshLayout refreshLayout;
  private ContentLoadingProgressBar progressBar;
  private ApplicationListViewModel viewModel;
  private AdvancedPreferences advancedPreferences;
  private String searchText;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_apps_list, container, false);
    progressBar = view.findViewById(R.id.progress);
    progressBar.show();
    refreshLayout = view.findViewById(R.id.refresh);
    list = view.findViewById(R.id.list);
    adapter = new ApplicationsListAdapter(getActivity());
    list.setAdapter(adapter);
    list.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
      ExpandableListAdapter adapter = parent.getExpandableListAdapter();
      ActivityModel activityModel = (ActivityModel) adapter.getChild(groupPosition, childPosition);
      if (getActivity() != null) {
        if (activityModel.isExported()) {
          IntentUtils.launchActivity(getActivity(),
            activityModel.getComponentName(), activityModel.getName());
        } else {
          if (advancedPreferences.isRootIntegrationEnabled()) {
            RunActivityTask runActivityTask =
              new RunActivityTask(activityModel.getComponentName());
            runActivityTask.execute();
          } else {
            Toast.makeText(getActivity(), R.string.settings_error_root_not_active,
              Toast.LENGTH_SHORT).show();
          }
        }
      }
      return false;
    });
    refreshLayout.setOnRefreshListener(() -> {
      refreshLayout.setRefreshing(true);
      final Intent work = new Intent();
      work.putExtra(AppLoaderIntentService.ARG_REASON, AppLoaderIntentService.REFRESH_USER);
      AppLoaderIntentService.enqueueWork(getActivity(), work);
    });
    SharedPreferences sharedPreferences =
      PreferenceManager.getDefaultSharedPreferences(getActivity());
    advancedPreferences = new AdvancedPreferences(sharedPreferences);
    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = ViewModelProviders.of(this).get(ApplicationListViewModel.class);
  }

  @Override
  public void onResume() {
    super.onResume();
    viewModel.getItems(searchText).observe(this, itemModels -> {
      if (itemModels != null && !itemModels.isEmpty()) {
        adapter.setShowNotExported(advancedPreferences.isShowNotExported());
        adapter.setItems(itemModels);
        refreshLayout.setRefreshing(false);
        progressBar.hide();
      }
    });
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    registerForContextMenu(list);
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuInfo;
    if (getView() != null) {
      ExpandableListView list = getView().findViewById(R.id.list);
      switch (ExpandableListView.getPackedPositionType(info.packedPosition)) {
        case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
          ActivityModel activity = (ActivityModel) list.getExpandableListAdapter()
            .getChild(ExpandableListView.getPackedPositionGroup(info.packedPosition),
              ExpandableListView.getPackedPositionChild(info.packedPosition));
          String title = !TextUtils.isEmpty(activity.getName()) ?
            activity.getName() :
            activity.getComponentName().getShortClassName();
          menu.setHeaderTitle(title);
          if (activity.isExported()) {
            menu.add(Menu.NONE, ACTION_CREATE_SHORTCUT, 1,
              R.string.context_action_shortcut);
            menu.add(Menu.NONE, ACTION_LAUNCH_ACTIVITY, 2,
              R.string.context_action_launch);
            menu.add(Menu.NONE, ACTION_LAUNCH_ACTIVITY_PARAMS, 3,
              R.string.context_action_launch_params);
          }
          if (advancedPreferences.isRootIntegrationEnabled()) {
            menu.add(Menu.NONE, ACTION_LAUNCH_ACTIVITY_BY_ROOT, 4,
              R.string.context_action_launch_root);
          }
          break;
      }
    }
    super.onCreateContextMenu(menu, v, menuInfo);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
    if (getView() != null) {
      ExpandableListView list = getView().findViewById(R.id.list);
      switch (ExpandableListView.getPackedPositionType(info.packedPosition)) {
        case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
          ActivityModel activityModel = (ActivityModel) list.getExpandableListAdapter()
            .getChild(ExpandableListView.getPackedPositionGroup(info.packedPosition),
              ExpandableListView.getPackedPositionChild(info.packedPosition));
          switch (item.getItemId()) {
            case ACTION_CREATE_SHORTCUT:
              if (getFragmentManager() != null) {
                DialogFragment dialog = AddShortcutDialogFragment.newInstance(activityModel);
                dialog.show(getFragmentManager(), AddShortcutDialogFragment.TAG);
              }
              break;
            case ACTION_LAUNCH_ACTIVITY:
              if (getActivity() != null) {
                IntentUtils.launchActivity(getActivity(),
                  activityModel.getComponentName(), activityModel.getName());
              }
              break;
            case ACTION_LAUNCH_ACTIVITY_PARAMS:
              if (getActivity() != null) {
                LaunchParamsActivity.start(getActivity(), activityModel);
              }
              break;
            case ACTION_LAUNCH_ACTIVITY_BY_ROOT:
              if (getActivity() != null) {
                RunActivityTask runActivityTask =
                  new RunActivityTask(activityModel.getComponentName());
                runActivityTask.execute();
              }
              break;
          }
          break;
      }
    }
    return super.onContextItemSelected(item);
  }

  public void filter(String text) {
    if (adapter != null) {
      this.searchText = text;
      viewModel.getItems(text).observe(this,
        itemModels -> adapter.setItems(itemModels));
    }
  }
}
