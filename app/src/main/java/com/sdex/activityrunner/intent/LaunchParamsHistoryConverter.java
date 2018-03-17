package com.sdex.activityrunner.intent;

import com.sdex.activityrunner.db.history.HistoryModel;
import java.util.ArrayList;
import java.util.List;

public class LaunchParamsHistoryConverter implements LaunchParamsConverter<HistoryModel> {

  private final LaunchParams launchParams;

  public LaunchParamsHistoryConverter(LaunchParams launchParams) {
    this.launchParams = launchParams;
  }

  @Override
  public HistoryModel convert() {
    final String packageName = launchParams.getPackageName();
    final String className = launchParams.getClassName();
    final int action = launchParams.getAction();
    final String data = launchParams.getData();
    final int mimeType = launchParams.getMimeType();
    final ArrayList<Integer> categories = launchParams.getCategories();
    final ArrayList<Integer> flags = launchParams.getFlags();

    final HistoryModel historyModel = new HistoryModel();
    historyModel.setTimestamp(System.currentTimeMillis());
    historyModel.setPackageName(packageName);
    historyModel.setClassName(className);
    historyModel.setAction(action);
    historyModel.setData(data);
    historyModel.setMimeType(mimeType);
    historyModel.setCategories(join(categories));
    historyModel.setFlags(join(flags));

    // TODO save extras

    return historyModel;
  }

  private String join(List<Integer> list) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < list.size(); i++) {
      stringBuilder.append(list.get(i));
      if (i != list.size() - 1) {
        stringBuilder.append(",");
      }
    }
    return stringBuilder.toString();
  }
}
