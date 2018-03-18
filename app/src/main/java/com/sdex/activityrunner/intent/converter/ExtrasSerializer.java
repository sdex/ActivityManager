package com.sdex.activityrunner.intent.converter;

import android.text.TextUtils;
import com.sdex.activityrunner.intent.LaunchParamsExtra;
import java.util.ArrayList;
import java.util.List;

public final class ExtrasSerializer {

  private static final String DELIMITER_KEY_VALUE = "†";
  private static final String DELIMITER_EXTRA = "‡";

  public ExtrasSerializer() {
  }

  public String serialize(List<LaunchParamsExtra> list) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < list.size(); i++) {
      LaunchParamsExtra extra = list.get(i);
      stringBuilder.append(extra.getKey()).append(DELIMITER_KEY_VALUE)
        .append(extra.getValue()).append(DELIMITER_KEY_VALUE)
        .append(extra.getType()).append(DELIMITER_KEY_VALUE)
        .append(extra.isArray());
      if (i != list.size() - 1) {
        stringBuilder.append(DELIMITER_EXTRA);
      }
    }
    return stringBuilder.toString();
  }

  public ArrayList<LaunchParamsExtra> deserialize(String input) {
    if (TextUtils.isEmpty(input)) {
      return new ArrayList<>();
    }
    final String[] extras = input.split(DELIMITER_EXTRA);
    ArrayList<LaunchParamsExtra> output = new ArrayList<>(extras.length);
    for (String extra : extras) {
      final String[] values = extra.split(DELIMITER_KEY_VALUE);
      LaunchParamsExtra paramsExtra = new LaunchParamsExtra();
      paramsExtra.setKey(values[0]);
      paramsExtra.setValue(values[1]);
      paramsExtra.setType(Integer.parseInt(values[2]));
      paramsExtra.setArray(Boolean.parseBoolean(values[3]));
      output.add(paramsExtra);
    }
    return output;
  }
}
