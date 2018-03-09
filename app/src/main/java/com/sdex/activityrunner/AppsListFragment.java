package com.sdex.activityrunner;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
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
import android.widget.ExpandableListView.OnChildClickListener;
import com.sdex.activityrunner.db.ActivityModel;
import com.sdex.activityrunner.db.ItemModel;
import com.sdex.activityrunner.service.AppLoaderIntentService;
import com.sdex.activityrunner.util.LauncherIconCreator;
import java.util.List;

public class AppsListFragment extends Fragment {

  private ExpandableListView list;
  private ApplicationsListAdapter adapter;
  private SwipeRefreshLayout refreshLayout;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
    Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_all_list, container, false);
    refreshLayout = view.findViewById(R.id.refresh);
    list = view.findViewById(R.id.expandableListView1);
    list.setOnChildClickListener(new OnChildClickListener() {
      @Override
      public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
        int childPosition, long id) {
        ExpandableListAdapter adapter = parent.getExpandableListAdapter();
        ActivityModel info = (ActivityModel) adapter.getChild(groupPosition, childPosition);
        LauncherIconCreator.launchActivity(getActivity(),
          info.getComponentName(), info.getName());
        return false;
      }
    });
    adapter = new ApplicationsListAdapter(getActivity());
    list.setAdapter(adapter);

    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresh() {
        refreshLayout.setRefreshing(true);
        AppLoaderIntentService.enqueueWork(getActivity(), new Intent());
      }
    });
    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ApplicationListViewModel viewModel = ViewModelProviders.of(this)
      .get(ApplicationListViewModel.class);
    viewModel.getItems().observe(this, new Observer<List<ItemModel>>() {
      @Override
      public void onChanged(@Nullable List<ItemModel> itemModels) {
        adapter.addItems(itemModels);
        refreshLayout.setRefreshing(false);
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
    ExpandableListView list = getView().findViewById(R.id.expandableListView1);
    switch (ExpandableListView.getPackedPositionType(info.packedPosition)) {
      case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
        ActivityModel activity = (ActivityModel) list.getExpandableListAdapter()
          .getChild(ExpandableListView.getPackedPositionGroup(info.packedPosition),
            ExpandableListView.getPackedPositionChild(info.packedPosition));
        menu.setHeaderTitle(activity.getName());
        menu.add(Menu.NONE, 0, Menu.NONE, R.string.context_action_shortcut);
        menu.add(Menu.NONE, 1, Menu.NONE, R.string.context_action_launch);
        break;
    }

    super.onCreateContextMenu(menu, v, menuInfo);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
    ExpandableListView list = getView().findViewById(R.id.expandableListView1);

    switch (ExpandableListView.getPackedPositionType(info.packedPosition)) {
      case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
        ActivityModel activity = (ActivityModel) list.getExpandableListAdapter()
          .getChild(ExpandableListView.getPackedPositionGroup(info.packedPosition),
            ExpandableListView.getPackedPositionChild(info.packedPosition));
        switch (item.getItemId()) {
          case 0:
            DialogFragment dialog = new ShortcutEditDialogFragment();
            Bundle args = new Bundle();
            args.putParcelable("activityInfo", activity.getComponentName());
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "ShortcutEditor");
            break;
          case 1:
            LauncherIconCreator.launchActivity(getActivity(),
              activity.getComponentName(), activity.getName());
            break;
        }
        break;
    }
    return super.onContextItemSelected(item);
  }
}
