package com.sdex.activityrunner;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.sdex.activityrunner.db.activity.ActivityModel;
import com.sdex.activityrunner.db.application.ApplicationModel;
import com.sdex.activityrunner.db.application.ItemModel;
import com.sdex.activityrunner.manifest.ManifestViewerActivity;
import com.sdex.activityrunner.util.GlideApp;
import com.sdex.activityrunner.util.IntentUtils;

import java.util.ArrayList;
import java.util.List;

public class ApplicationsListAdapter extends BaseExpandableListAdapter {

  public static final int CHILD_TYPE_NOT_EXPORTED = 0;
  public static final int CHILD_TYPE_EXPORTED = 1;

  private final List<ItemModel> items;
  private final Context context;
  private final RequestManager glide;

  private boolean showNotExported;

  public ApplicationsListAdapter(Context context) {
    this.context = context;
    this.items = new ArrayList<>();
    this.glide = GlideApp.with(context);
  }

  @Override
  public Object getChild(int groupPosition, int childPosition) {
    return items.get(groupPosition).getActivityModels().get(childPosition);
  }

  @Override
  public long getChildId(int groupPosition, int childPosition) {
    return childPosition;
  }

  @Override
  public int getChildTypeCount() {
    return 2;
  }

  @Override
  public int getChildType(int groupPosition, int childPosition) {
    return items.get(groupPosition).getActivityModels().get(childPosition).isExported() ?
      CHILD_TYPE_EXPORTED : CHILD_TYPE_NOT_EXPORTED;
  }

  @Override
  public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                           View convertView, ViewGroup parent) {
    ActivityModel activityModel = (ActivityModel) getChild(groupPosition, childPosition);
    LayoutInflater inflater = LayoutInflater.from(context);

    if (getChildType(groupPosition, childPosition) == CHILD_TYPE_NOT_EXPORTED
      && !showNotExported) {
      return inflater.inflate(R.layout.item_activity_not_exported_hide, parent, false);
    }

    View view = inflater.inflate(R.layout.item_activity, parent, false);

    TextView text1 = view.findViewById(android.R.id.text1);
    text1.setText(activityModel.getName());

    if (activityModel.isExported()) {
      text1.setTextColor(ContextCompat.getColor(parent.getContext(),
        android.R.color.black));
    } else {
      text1.setTextColor(ContextCompat.getColor(parent.getContext(),
        android.R.color.holo_red_dark));
    }

    TextView text2 = view.findViewById(android.R.id.text2);
    text2.setText(activityModel.getComponentName().getShortClassName());

    ImageView icon = view.findViewById(android.R.id.icon);
    glide.load(activityModel.getIconPath())
      .apply(new RequestOptions()
        .fitCenter())
      .into(icon);

    return view;
  }

  @Override
  public int getChildrenCount(int groupPosition) {
    return this.items.get(groupPosition).getActivityModels().size();
  }

  @Override
  public Object getGroup(int groupPosition) {
    return this.items.get(groupPosition);
  }

  @Override
  public int getGroupCount() {
    return this.items.size();
  }

  @Override
  public long getGroupId(int groupPosition) {
    return groupPosition;
  }

  @Override
  public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                           ViewGroup parent) {
    ItemModel itemModel = (ItemModel) getGroup(groupPosition);
    final ApplicationModel applicationModel = itemModel.getApplicationModel();
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.item_application, parent, false);

    TextView text = view.findViewById(android.R.id.text1);
    text.setText(applicationModel.getName());

    TextView text2 = view.findViewById(android.R.id.text2);
    text2.setText(applicationModel.getPackageName());

    ImageView icon = view.findViewById(android.R.id.icon);
    glide.load(applicationModel.getIconPath())
      .apply(new RequestOptions()
        .fitCenter())
      .into(icon);

    view.findViewById(R.id.app_menu).setOnClickListener(v -> {
      PopupMenu popup = new PopupMenu(context, v);
      popup.inflate(R.menu.application_item_menu);
      popup.show();
      popup.setOnMenuItemClickListener(item -> {
        final String packageName = applicationModel.getPackageName();
        switch (item.getItemId()) {
          case R.id.action_open_app_info: {
            IntentUtils.openApplicationInfo(context, packageName);
            return true;
          }
          case R.id.action_open_app_manifest: {
            ManifestViewerActivity.start(context, packageName, applicationModel.getName());
            return true;
          }
        }
        return false;
      });

    });

    return view;
  }

  @Override
  public boolean hasStableIds() {
    return false;
  }

  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition) {
    return true;
  }

  public void setItems(List<ItemModel> itemModels) {
    this.items.clear();
    this.items.addAll(itemModels);
    notifyDataSetChanged();
  }

  public void setShowNotExported(boolean showNotExported) {
    this.showNotExported = showNotExported;
  }
}
