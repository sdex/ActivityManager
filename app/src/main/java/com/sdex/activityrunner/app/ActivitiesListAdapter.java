package com.sdex.activityrunner.app;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.sdex.activityrunner.R;
import com.sdex.activityrunner.db.activity.ActivityModel;
import com.sdex.activityrunner.glide.GlideApp;

public class ActivitiesListAdapter extends ListAdapter<ActivityModel,
  ActivitiesListAdapter.ViewHolder> {

  private final Callback callback;
  private final RequestManager glide;

  protected ActivitiesListAdapter(Context context, Callback callback) {
    super(DIFF_CALLBACK);
    this.callback = callback;
    this.glide = GlideApp.with(context);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_activity, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.bindTo(getItem(position), glide, callback);
  }

  interface Callback {
    void showShortcutDialog(ActivityModel item);

    void launchActivity(ActivityModel item);

    void launchActivityWithParams(ActivityModel item);
  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    private final TextView name;
    private final TextView packageName;
    private final ImageView icon;
    private final View overflowMenu;

    public ViewHolder(View itemView) {
      super(itemView);
      name = itemView.findViewById(android.R.id.text1);
      packageName = itemView.findViewById(android.R.id.text2);
      icon = itemView.findViewById(android.R.id.icon);
      overflowMenu = itemView.findViewById(R.id.app_menu);
    }

    public void bindTo(ActivityModel item, RequestManager glide, Callback callback) {
      name.setText(item.getName());
      packageName.setText(item.getComponentName().getShortClassName());

      glide.load(item.getIconPath())
        .apply(new RequestOptions()
          .fitCenter())
        .into(icon);

      Context context = itemView.getContext();

      @ColorRes final int color;
      if (item.isExported()) {
        color = android.R.color.black;
      } else {
        color = R.color.red;
      }
      name.setTextColor(ContextCompat.getColor(context, color));

      itemView.setOnClickListener(v -> callback.launchActivity(item));

      overflowMenu.setOnClickListener(v -> {
        PopupMenu popup = new PopupMenu(context, v);
        popup.inflate(R.menu.activity_item_menu);
        Menu menu = popup.getMenu();
        menu.setGroupVisible(R.id.menu_group_activity_exported,
          item.isExported());
        menu.setGroupVisible(R.id.menu_group_activity_not_exported,
          !item.isExported());
        popup.show();
        popup.setOnMenuItemClickListener(menuItem -> {
          switch (menuItem.getItemId()) {
            case R.id.action_activity_add_shortcut: {
              callback.showShortcutDialog(item);
              return true;
            }
            case R.id.action_activity_launch_with_params: {
              callback.launchActivityWithParams(item);
              return true;
            }
            case R.id.action_activity_launch_with_root: {
              callback.launchActivity(item);
              return true;
            }
          }
          return false;
        });

      });
    }
  }

  public static final DiffUtil.ItemCallback<ActivityModel> DIFF_CALLBACK =
    new DiffUtil.ItemCallback<ActivityModel>() {

      @Override
      public boolean areItemsTheSame(ActivityModel oldItem, ActivityModel newItem) {
        return oldItem.equals(newItem);
      }

      @Override
      public boolean areContentsTheSame(ActivityModel oldItem, ActivityModel newItem) {
        return oldItem.equals(newItem);
      }
    };
}
