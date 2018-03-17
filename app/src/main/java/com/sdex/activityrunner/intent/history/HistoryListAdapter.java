package com.sdex.activityrunner.intent.history;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sdex.activityrunner.R;
import com.sdex.activityrunner.db.history.HistoryModel;
import com.sdex.activityrunner.intent.dialog.source.ActionSource;
import com.sdex.activityrunner.intent.dialog.source.MimeTypeSource;
import java.util.ArrayList;
import java.util.List;

public class HistoryListAdapter extends
  RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

  public static final int MENU_ITEM_REMOVE = 0;

  private final Callback callback;
  private List<HistoryModel> items = new ArrayList<>();
  private int contextMenuItemPosition;

  public HistoryListAdapter(HistoryListAdapter.Callback callback) {
    this.callback = callback;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
    int viewType) {
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    final View view = inflater.inflate(R.layout.item_history, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    final HistoryModel item = items.get(position);
    holder.bind(item, callback);
    holder.itemView.setOnLongClickListener(v -> {
      setContextMenuItemPosition(holder.getAdapterPosition());
      return false;
    });
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  @Override
  public long getItemId(int position) {
    return items.get(position).getId();
  }

  @Override
  public void onViewRecycled(@NonNull ViewHolder holder) {
    holder.itemView.setOnLongClickListener(null);
    super.onViewRecycled(holder);
  }

  public HistoryModel getItem(int position) {
    return items.get(position);
  }

  public int getContextMenuItemPosition() {
    return contextMenuItemPosition;
  }

  public void setContextMenuItemPosition(int contextMenuItemPosition) {
    this.contextMenuItemPosition = contextMenuItemPosition;
  }

  public void setItems(List<HistoryModel> items) {
    this.items = items;
    notifyDataSetChanged();
  }

  public interface Callback {

    void onItemClicked(HistoryModel item, int position);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder
    implements View.OnCreateContextMenuListener {

    private final ActionSource actionSource = new ActionSource();
    private final MimeTypeSource mimeTypeSource = new MimeTypeSource();

    public final TextView packageName;
    public final TextView className;
    public final TextView action;
    public final TextView data;
    public final TextView mimeType;

    public ViewHolder(View itemView) {
      super(itemView);
      this.packageName = itemView.findViewById(R.id.package_name);
      this.className = itemView.findViewById(R.id.class_name);
      this.action = itemView.findViewById(R.id.action);
      this.data = itemView.findViewById(R.id.data);
      this.mimeType = itemView.findViewById(R.id.mime_type);
    }

    public void bind(HistoryModel item, Callback callback) {
      packageName.setText(getValueOrPlaceholder(item.getPackageName()));
      className.setText(getValueOrPlaceholder(item.getClassName()));
      action.setText(actionSource.getItem(item.getAction()));
      data.setText(getValueOrPlaceholder(item.getData()));
      mimeType.setText(mimeTypeSource.getItem(item.getMimeType()));

      itemView.setOnClickListener(v -> callback.onItemClicked(item, getAdapterPosition()));
      itemView.setOnCreateContextMenuListener(this);
    }

    private String getValueOrPlaceholder(String value) {
      if (TextUtils.isEmpty(value)) {
        return "NONE"; // TODO localization
      }
      return value;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      menu.add(Menu.NONE, MENU_ITEM_REMOVE, Menu.NONE, "Remove"); // TODO localization
    }
  }
}
