package com.sdex.activityrunner;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.sdex.activityrunner.db.ActivityModel;
import com.sdex.activityrunner.service.AppLoaderIntentService;
import com.sdex.activityrunner.util.IntentUtils;

public class AppsListFragment extends Fragment {

  public static final String TAG = "AppsListFragment";

  private static final int ACTION_CREATE_SHORTCUT = 0;
  private static final int ACTION_LAUNCH_ACTIVITY = 1;

  private ExpandableListView list;
  private ApplicationsListAdapter adapter;
  private SwipeRefreshLayout refreshLayout;
  private ContentLoadingProgressBar progressBar;
  private ApplicationListViewModel viewModel;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
    Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_apps_list, container, false);
    progressBar = view.findViewById(R.id.progress);
    progressBar.show();
    refreshLayout = view.findViewById(R.id.refresh);
    list = view.findViewById(R.id.list);
    list.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
      ExpandableListAdapter adapter = parent.getExpandableListAdapter();
      ActivityModel info = (ActivityModel) adapter.getChild(groupPosition, childPosition);
      if (getActivity() != null) {
        IntentUtils.launchActivity(getActivity(), info.getComponentName(), info.getName());
      }
      return false;
    });
    adapter = new ApplicationsListAdapter(getActivity());
    list.setAdapter(adapter);

    refreshLayout.setOnRefreshListener(() -> {
      refreshLayout.setRefreshing(true);
      final Intent work = new Intent();
      work.putExtra(AppLoaderIntentService.ARG_REASON, AppLoaderIntentService.REFRESH_USER);
      AppLoaderIntentService.enqueueWork(getActivity(), work);
    });
    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = ViewModelProviders.of(this).get(ApplicationListViewModel.class);
    viewModel.getItems().observe(this, itemModels -> {
      if (itemModels != null && !itemModels.isEmpty()) {
        adapter.addItems(itemModels);
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
          menu.setHeaderTitle(activity.getName());
          menu.add(Menu.NONE, ACTION_CREATE_SHORTCUT, Menu.NONE, R.string.context_action_shortcut);
          menu.add(Menu.NONE, ACTION_LAUNCH_ACTIVITY, Menu.NONE, R.string.context_action_launch);
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
                DialogFragment dialog = new AddShortcutDialogFragment();
                Bundle args = new Bundle();
                args.putSerializable(AddShortcutDialogFragment.ARG_ACTIVITY_MODEL, activityModel);
                dialog.setArguments(args);
                dialog.show(getFragmentManager(), AddShortcutDialogFragment.TAG);
              }
              break;
            case ACTION_LAUNCH_ACTIVITY:
              if (getActivity() != null) {
                IntentUtils.launchActivity(getActivity(),
                  activityModel.getComponentName(), activityModel.getName());
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
      viewModel.getItems(text).observe(this,
        itemModels -> adapter.addItems(itemModels));
    }
  }
}