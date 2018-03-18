package com.sdex.activityrunner;

import static org.junit.Assert.assertEquals;

import com.sdex.activityrunner.intent.LaunchParamsExtra;
import com.sdex.activityrunner.intent.LaunchParamsExtraType;
import com.sdex.activityrunner.intent.converter.ExtrasSerializer;
import java.util.ArrayList;
import org.junit.Test;

public class ExtrasSerializerTest {

  @Test
  public void serialize() {
    ArrayList<LaunchParamsExtra> list = new ArrayList<>();

    LaunchParamsExtra extra0 = new LaunchParamsExtra();
    extra0.setKey("k0");
    extra0.setValue("3.3");
    extra0.setType(LaunchParamsExtraType.DOUBLE);
    extra0.setArray(true);
    list.add(extra0);

    LaunchParamsExtra extra1 = new LaunchParamsExtra();
    extra1.setKey("k1");
    extra1.setValue("v1");
    extra1.setType(LaunchParamsExtraType.STRING);
    extra1.setArray(false);
    list.add(extra1);

    LaunchParamsExtra extra2 = new LaunchParamsExtra();
    extra2.setKey("k2");
    extra2.setValue("888");
    extra2.setType(LaunchParamsExtraType.INT);
    extra2.setArray(false);
    list.add(extra2);

    final String expected = "k0†3.3†4†true‡k1†v1†0†false‡k2†888†1†false";
    ExtrasSerializer extrasSerializer = new ExtrasSerializer();
    final String serialized = extrasSerializer.serialize(list);
    assertEquals(serialized, expected);
  }

}
