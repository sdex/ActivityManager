package com.sdex.activityrunner.intent;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sdex.activityrunner.R;
import java.util.ArrayList;
import java.util.List;

public class LaunchParamsExtraListAdapter extends
  RecyclerView.Adapter<LaunchParamsExtraListAdapter.ViewHolder> {

  private final Callback callback;
  private List<LaunchParamsExtra> items = new ArrayList<>();

  public LaunchParamsExtraListAdapter(Callback callback) {
    this.callback = callback;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    final View view = inflater.inflate(R.layout.item_launch_param_extra, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    final LaunchParamsExtra item = items.get(position);
    holder.bind(item, callback);
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  public void setItems(List<LaunchParamsExtra> items) {
    this.items.clear();
    this.items.addAll(items);
    notifyDataSetChanged();
  }

  public interface Callback {

    void onItemSelected(int position);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public final TextView keyView;
    public final TextView valueView;

    public ViewHolder(View itemView) {
      super(itemView);
      this.keyView = itemView.findViewById(R.id.key);
      this.valueView = itemView.findViewById(R.id.value);
    }

    public void bind(LaunchParamsExtra item, Callback callback) {
      keyView.setText(item.getKey());
      valueView.setText(item.getValue());
      itemView.setOnClickListener(v -> callback.onItemSelected(getAdapterPosition()));
    }
  }
}
