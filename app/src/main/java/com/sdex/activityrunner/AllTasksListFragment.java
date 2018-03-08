package com.sdex.activityrunner;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import com.sdex.activityrunner.info.MyActivityInfo;
import com.sdex.activityrunner.loader.AllTasksListAsyncProvider;
import com.sdex.activityrunner.loader.AsyncProvider;
import com.sdex.activityrunner.util.LauncherIconCreator;

public class AllTasksListFragment extends Fragment implements
  AllTasksListAsyncProvider.Listener<AllTasksListAdapter> {

  private ExpandableListView list;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
    Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_all_list, container, false);

    list = view.findViewById(R.id.expandableListView1);

    list.setOnChildClickListener(new OnChildClickListener() {
      @Override
      public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
        int childPosition, long id) {
        ExpandableListAdapter adapter = parent.getExpandableListAdapter();
        MyActivityInfo info = (MyActivityInfo) adapter.getChild(groupPosition, childPosition);
        LauncherIconCreator.launchActivity(getActivity(),
          info.getComponentName(), info.getName());
        return false;
      }
    });

    AllTasksListAsyncProvider provider = new AllTasksListAsyncProvider(this.getActivity(), this);
    provider.execute();

    return view;
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
        MyActivityInfo activity = (MyActivityInfo) list.getExpandableListAdapter()
          .getChild(ExpandableListView.getPackedPositionGroup(info.packedPosition),
            ExpandableListView.getPackedPositionChild(info.packedPosition));
        menu.setHeaderIcon(new BitmapDrawable(getResources(), activity.getIcon()));
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
        MyActivityInfo activity = (MyActivityInfo) list.getExpandableListAdapter()
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

  @Override
  public void onProviderFinished(AsyncProvider<AllTasksListAdapter> task,
    AllTasksListAdapter value) {
    list.setAdapter(value);
  }
}
