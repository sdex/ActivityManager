package com.sdex.activityrunner.intent.converter;

import java.util.ArrayList;
import java.util.List;

public final class IntegerListSerializer {

  private static final String DELIMITER = ",";

  public IntegerListSerializer() {
  }

  public String serialize(List<Integer> list) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < list.size(); i++) {
      stringBuilder.append(list.get(i));
      if (i != list.size() - 1) {
        stringBuilder.append(DELIMITER);
      }
    }
    return stringBuilder.toString();
  }

  public ArrayList<Integer> deserialize(String input) {
    if (input == null || input.isEmpty()) {
      return new ArrayList<>(0);
    }
    final String[] split = input.split(DELIMITER);
    ArrayList<Integer> output = new ArrayList<>(split.length);
    for (String s : split) {
      output.add(Integer.parseInt(s));
    }
    return output;
  }
}
