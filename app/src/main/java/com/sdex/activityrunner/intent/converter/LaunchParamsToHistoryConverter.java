package com.sdex.activityrunner.intent.converter;

import com.sdex.activityrunner.db.history.HistoryModel;
import com.sdex.activityrunner.intent.LaunchParams;
import java.util.ArrayList;

public class LaunchParamsToHistoryConverter implements Converter<HistoryModel> {

  private final LaunchParams launchParams;

  public LaunchParamsToHistoryConverter(LaunchParams launchParams) {
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

    IntegerListSerializer serializer = new IntegerListSerializer();

    final HistoryModel historyModel = new HistoryModel();
    historyModel.setTimestamp(System.currentTimeMillis());
    historyModel.setPackageName(packageName);
    historyModel.setClassName(className);
    historyModel.setAction(action);
    historyModel.setData(data);
    historyModel.setMimeType(mimeType);
    historyModel.setCategories(serializer.serialize(categories));
    historyModel.setFlags(serializer.serialize(flags));

    // TODO save extras

    return historyModel;
  }
}
