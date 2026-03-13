package com.sdex.activityrunner;

import com.sdex.activityrunner.intent.converter.IntegerListSerializer;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class IntegerListSerializerTest {

    private final static ArrayList<Integer> EXPECTED_LIST = new ArrayList<>();
    private final static String EXPECTED_STRING = "1,2,3,4,9,8,7,6,5,0,0,0";

    @BeforeClass
    public static void setUp() {
        EXPECTED_LIST.clear();

        EXPECTED_LIST.add(1);
        EXPECTED_LIST.add(2);
        EXPECTED_LIST.add(3);
        EXPECTED_LIST.add(4);
        EXPECTED_LIST.add(9);
        EXPECTED_LIST.add(8);
        EXPECTED_LIST.add(7);
        EXPECTED_LIST.add(6);
        EXPECTED_LIST.add(5);
        EXPECTED_LIST.add(0);
        EXPECTED_LIST.add(0);
        EXPECTED_LIST.add(0);
    }

    @Test
    public void serialize() {
        IntegerListSerializer serializer = new IntegerListSerializer();
        String actual = serializer.serialize(EXPECTED_LIST);
        assertEquals(actual, EXPECTED_STRING);
    }

    @Test
    public void serializeEmpty() {
        IntegerListSerializer serializer = new IntegerListSerializer();
        String actual = serializer.serialize(new ArrayList<>());
        assertEquals(actual, "");
    }

    @Test
    public void deserialize() {
        IntegerListSerializer serializer = new IntegerListSerializer();
        ArrayList<Integer> actual = serializer.deserialize(EXPECTED_STRING);
        assertEquals(actual.size(), EXPECTED_LIST.size());
        for (int i = 0; i < actual.size(); i++) {
            Integer integerActual = actual.get(i);
            Integer integerExpected = EXPECTED_LIST.get(i);
            assertEquals(integerActual, integerExpected);
        }
    }

    @Test
    public void deserializeEmpty() {
        IntegerListSerializer serializer = new IntegerListSerializer();
        ArrayList<Integer> actual = serializer.deserialize("");
        assertEquals(actual.size(), 0);
    }
}
