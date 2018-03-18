package com.sdex.activityrunner.intent.converter;

import com.sdex.activityrunner.db.history.HistoryModel;
import com.sdex.activityrunner.intent.LaunchParams;

public class LaunchParamsToHistoryConverter implements Converter<HistoryModel> {

  private final LaunchParams launchParams;

  public LaunchParamsToHistoryConverter(LaunchParams launchParams) {
    this.launchParams = launchParams;
  }

  @Override
  public HistoryModel convert() {
    IntegerListSerializer integerListSerializer = new IntegerListSerializer();
    ExtrasSerializer extrasSerializer = new ExtrasSerializer();
    final HistoryModel historyModel = new HistoryModel();
    historyModel.setTimestamp(System.currentTimeMillis());
    historyModel.setPackageName(launchParams.getPackageName());
    historyModel.setClassName(launchParams.getClassName());
    historyModel.setAction(launchParams.getAction());
    historyModel.setData(launchParams.getData());
    historyModel.setMimeType(launchParams.getMimeType());
    historyModel.setCategories(integerListSerializer.serialize(launchParams.getCategories()));
    historyModel.setFlags(integerListSerializer.serialize(launchParams.getFlags()));
    historyModel.setExtras(extrasSerializer.serialize(launchParams.getExtras()));
    return historyModel;
  }
}
