package com.sdex.activityrunner.intent.converter;

import com.sdex.activityrunner.db.history.HistoryModel;
import com.sdex.activityrunner.intent.LaunchParams;

public class HistoryToLaunchParamsConverter implements Converter<LaunchParams> {

  private final HistoryModel historyModel;

  public HistoryToLaunchParamsConverter(HistoryModel historyModel) {
    this.historyModel = historyModel;
  }

  @Override
  public LaunchParams convert() {
    IntegerListSerializer integerListSerializer = new IntegerListSerializer();
    ExtrasSerializer extrasSerializer = new ExtrasSerializer();
    LaunchParams params = new LaunchParams();
    params.setPackageName(historyModel.getPackageName());
    params.setClassName(historyModel.getClassName());
    params.setAction(historyModel.getAction());
    params.setData(historyModel.getData());
    params.setMimeType(historyModel.getMimeType());
    params.setCategories(integerListSerializer.deserialize(historyModel.getCategories()));
    params.setFlags(integerListSerializer.deserialize(historyModel.getFlags()));
    params.setExtras(extrasSerializer.deserialize(historyModel.getExtras()));
    return params;
  }
}
