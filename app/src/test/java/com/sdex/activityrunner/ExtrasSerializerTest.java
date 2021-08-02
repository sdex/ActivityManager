package com.sdex.activityrunner;

import com.sdex.activityrunner.intent.LaunchParamsExtra;
import com.sdex.activityrunner.intent.LaunchParamsExtraType;
import com.sdex.activityrunner.intent.converter.ExtrasSerializer;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ExtrasSerializerTest {

    private final static ArrayList<LaunchParamsExtra> EXPECTED_LIST = new ArrayList<>();
    private final static String EXPECTED_STRING = "k0†3.3†4†true‡k1†v1†0†false‡k2†888†1†false";

    @BeforeClass
    public static void setUp() {
        EXPECTED_LIST.clear();

        LaunchParamsExtra extra0 = new LaunchParamsExtra("k0", "3.3",
                LaunchParamsExtraType.DOUBLE, true);
        EXPECTED_LIST.add(extra0);

        LaunchParamsExtra extra1 = new LaunchParamsExtra("k1", "v1",
                LaunchParamsExtraType.STRING, false);
        EXPECTED_LIST.add(extra1);

        LaunchParamsExtra extra2 = new LaunchParamsExtra("k2", "888",
                LaunchParamsExtraType.INT, false);
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
