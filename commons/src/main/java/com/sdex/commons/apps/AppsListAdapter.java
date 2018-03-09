package com.sdex.commons.apps;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sdex.commons.R;
import com.sdex.commons.util.AppUtils;
import com.sdex.commons.util.UIUtils;
import java.util.List;

public class AppsListAdapter extends RecyclerView.Adapter<AppsListAdapter.ViewHolder> {

  private final List<AppItem> items;

  public AppsListAdapter(List<AppItem> items) {
    this.items = items;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    return new ViewHolder(inflater.inflate(R.layout.item_app, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    AppItem item = items.get(position);
    holder.bind(item);
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    final ImageView icon;
    final TextView name;
    final TextView description;
    final View itemClickView;

    public ViewHolder(View itemView) {
      super(itemView);
      icon = itemView.findViewById(R.id.item_app_icon);
      name = itemView.findViewById(R.id.item_app_name);
      description = itemView.findViewById(R.id.item_app_description);
      itemClickView = itemView.findViewById(R.id.item_app);
    }

    public void bind(final AppItem item) {
      name.setText(item.getName());
      description.setText(item.getDescription());
      icon.setImageDrawable(UIUtils.getDrawable(item.getIcon(), itemView.getContext()));
      itemClickView.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          AppUtils.openApp(itemView.getContext(), item.getPackageName());
        }
      });
    }
  }
}
