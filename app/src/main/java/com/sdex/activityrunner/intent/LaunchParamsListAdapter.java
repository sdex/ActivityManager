package com.sdex.activityrunner.intent;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sdex.activityrunner.R;
import java.util.ArrayList;
import java.util.List;

public class LaunchParamsListAdapter extends
  RecyclerView.Adapter<LaunchParamsListAdapter.ViewHolder> {

  private List<String> items = new ArrayList<>();

  public LaunchParamsListAdapter() {
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    TextView v = (TextView) inflater.inflate(R.layout.item_launch_param, parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.textView.setText(items.get(position));
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  public void setItems(List<String> items) {
    this.items.clear();
    this.items.addAll(items);
    notifyDataSetChanged();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    public TextView textView;

    public ViewHolder(TextView textView) {
      super(textView);
      this.textView = textView;
    }
  }
}
