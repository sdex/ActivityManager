package com.sdex.activityrunner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.sdex.activityrunner.app.ActivitiesListActivity;
import com.sdex.activityrunner.db.application.ApplicationModel;
import com.sdex.activityrunner.glide.GlideApp;
import com.sdex.activityrunner.manifest.ManifestViewerActivity;
import com.sdex.activityrunner.util.IntentUtils;

public class ApplicationsListAdapter extends ListAdapter<ApplicationModel,
  ApplicationsListAdapter.AppViewHolder> {

  private final RequestManager glide;

  protected ApplicationsListAdapter(Context context) {
    super(DIFF_CALLBACK);
    this.glide = GlideApp.with(context);
  }

  @NonNull
  @Override
  public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_application, parent, false);
    return new AppViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
    holder.bindTo(getItem(position), glide);
  }

  static class AppViewHolder extends RecyclerView.ViewHolder {

    private final TextView name;
    private final TextView packageName;
    private final ImageView icon;
    private final View overflowMenu;

    public AppViewHolder(View itemView) {
      super(itemView);
      name = itemView.findViewById(android.R.id.text1);
      packageName = itemView.findViewById(android.R.id.text2);
      icon = itemView.findViewById(android.R.id.icon);
      overflowMenu = itemView.findViewById(R.id.app_menu);
    }

    public void bindTo(ApplicationModel item, RequestManager glide) {
      name.setText(item.getName());
      packageName.setText(item.getPackageName());

      Context context = itemView.getContext();

      glide.load(item)
        .apply(new RequestOptions().fitCenter())
        .into(icon);

      itemView.setOnClickListener(v -> ActivitiesListActivity.start(context, item));

      overflowMenu.setOnClickListener(v -> {
        PopupMenu popup = new PopupMenu(context, v);
        popup.inflate(R.menu.application_item_menu);
        popup.show();
        popup.setOnMenuItemClickListener(menuItem -> {
          final String packageName = item.getPackageName();
          switch (menuItem.getItemId()) {
            case R.id.action_open_app: {
              // TODO open the app
              return true;
            }
            case R.id.action_open_app_info: {
              IntentUtils.openApplicationInfo(context, packageName);
              return true;
            }
            case R.id.action_open_app_manifest: {
              ManifestViewerActivity.start(context, packageName, item.getName());
              return true;
            }
          }
          return false;
        });

      });
    }
  }

  public static final DiffUtil.ItemCallback<ApplicationModel> DIFF_CALLBACK =
    new DiffUtil.ItemCallback<ApplicationModel>() {

      @Override
      public boolean areItemsTheSame(ApplicationModel oldItem, ApplicationModel newItem) {
        return oldItem.getPackageName().equals(newItem.getPackageName());
      }

      @Override
      public boolean areContentsTheSame(ApplicationModel oldItem, ApplicationModel newItem) {
        return oldItem.equals(newItem);
      }
    };
}
