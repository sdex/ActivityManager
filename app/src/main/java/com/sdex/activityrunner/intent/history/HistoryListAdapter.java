package com.sdex.activityrunner.intent.history;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sdex.activityrunner.R;
import com.sdex.activityrunner.db.history.HistoryModel;
import java.util.ArrayList;
import java.util.List;

public class HistoryListAdapter extends
  RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

  private final Callback callback;
  private List<HistoryModel> items = new ArrayList<>();

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
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  @Override
  public long getItemId(int position) {
    return items.get(position).getId();
  }

  public void setItems(List<HistoryModel> items) {
    this.items = items;
    notifyDataSetChanged();
  }

  public interface Callback {

    void onItemClicked(int position);

    boolean onItemLongClicked(int position);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public final TextView title;

    public ViewHolder(View itemView) {
      super(itemView);
      this.title = itemView.findViewById(R.id.title);
    }

    public void bind(HistoryModel item, Callback callback) {
      title.setText(item.toString());
      itemView.setOnClickListener(v -> callback.onItemClicked(getAdapterPosition()));
      itemView.setOnLongClickListener(v -> callback.onItemLongClicked(getAdapterPosition()));
    }
  }
}
