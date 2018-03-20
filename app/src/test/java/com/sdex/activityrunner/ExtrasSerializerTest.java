package com.sdex.activityrunner;

import static org.junit.Assert.assertEquals;

import com.sdex.activityrunner.intent.LaunchParamsExtra;
import com.sdex.activityrunner.intent.LaunchParamsExtraType;
import com.sdex.activityrunner.intent.converter.ExtrasSerializer;
import java.util.ArrayList;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExtrasSerializerTest {

  private final static ArrayList<LaunchParamsExtra> EXPECTED_LIST = new ArrayList<>();
  private final static String EXPECTED_STRING = "k0†3.3†4†true‡k1†v1†0†false‡k2†888†1†false";

  @BeforeClass
  public static void setUp() {
    LaunchParamsExtra extra0 = new LaunchParamsExtra();
    extra0.setKey("k0");
    extra0.setValue("3.3");
    extra0.setType(LaunchParamsExtraType.DOUBLE);
    extra0.setArray(true);
    EXPECTED_LIST.add(extra0);

    LaunchParamsExtra extra1 = new LaunchParamsExtra();
    extra1.setKey("k1");
    extra1.setValue("v1");
    extra1.setType(LaunchParamsExtraType.STRING);
    extra1.setArray(false);
    EXPECTED_LIST.add(extra1);

    LaunchParamsExtra extra2 = new LaunchParamsExtra();
    extra2.setKey("k2");
    extra2.setValue("888");
    extra2.setType(LaunchParamsExtraType.INT);
    extra2.setArray(false);
    EXPECTED_LIST.add(extra2);
  }

  @Test
  public void serialize() {
    ExtrasSerializer extrasSerializer = new ExtrasSerializer();
    String actual = extrasSerializer.serialize(EXPECTED_LIST);
    assertEquals(actual, EXPECTED_STRING);
  }

  @Test
  public void serializeEmpty() {
    ExtrasSerializer extrasSerializer = new ExtrasSerializer();
    String actual = extrasSerializer.serialize(new ArrayList<>());
    assertEquals(actual, "");
  }

  @Test
  public void deserialize() {
    ExtrasSerializer extrasSerializer = new ExtrasSerializer();
    ArrayList<LaunchParamsExtra> actual = extrasSerializer.deserialize(EXPECTED_STRING);
    assertEquals(EXPECTED_LIST.size(), actual.size());
    for (int i = 0; i < actual.size(); i++) {
      LaunchParamsExtra extra = actual.get(i);
      LaunchParamsExtra expected = EXPECTED_LIST.get(i);
      assertEquals(expected.getKey(), extra.getKey());
      assertEquals(expected.getValue(), extra.getValue());
      assertEquals(expected.getType(), extra.getType());
      assertEquals(expected.isArray(), extra.isArray());
    }
  }

  @Test
  public void deserializeEmpty() {
    ExtrasSerializer extrasSerializer = new ExtrasSerializer();
    ArrayList<LaunchParamsExtra> actual = extrasSerializer.deserialize("");
    assertEquals(actual.size(), 0);
  }
}
