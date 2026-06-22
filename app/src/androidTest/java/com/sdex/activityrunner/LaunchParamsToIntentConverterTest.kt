package com.sdex.activityrunner;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.test.filters.MediumTest;
import androidx.test.runner.AndroidJUnit4;

import com.sdex.activityrunner.extensions.IntentExtensionsKt;
import com.sdex.activityrunner.intent.LaunchParams;
import com.sdex.activityrunner.intent.LaunchParamsExtensionsKt;
import com.sdex.activityrunner.intent.LaunchParamsExtra;
import com.sdex.activityrunner.intent.LaunchParamsExtraType;
import com.sdex.activityrunner.intent.converter.LaunchParamsToIntentConverter;
import com.sdex.activityrunner.intent.param.Category;
import com.sdex.activityrunner.intent.param.Flag;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class LaunchParamsToIntentConverterTest {

    @Test
    public void testAction() {
        LaunchParams launchParams = new LaunchParams();
        launchParams.setAction(Intent.ACTION_VIEW);

        LaunchParamsToIntentConverter converter = new LaunchParamsToIntentConverter(launchParams);
        Intent intent = converter.convert();

        Assert.assertEquals(Intent.ACTION_VIEW, intent.getAction());
    }

    @Test
    public void testCategories() {
        LaunchParams launchParams = new LaunchParams();
        launchParams.setCategories(getCategories());

        LaunchParamsToIntentConverter converter = new LaunchParamsToIntentConverter(launchParams);
        Intent intent = converter.convert();

        assertCategoriesEquals(launchParams, intent);
    }

    @Test
    public void testData() {
        LaunchParams launchParams = new LaunchParams();
        launchParams.setData("test_data_string");

        LaunchParamsToIntentConverter converter = new LaunchParamsToIntentConverter(launchParams);
        Intent intent = converter.convert();

        Assert.assertEquals(Uri.parse("test_data_string"), intent.getData());
    }

    @Test
    public void testMimeType() {
        LaunchParams launchParams = new LaunchParams();
        launchParams.setData("data");
        launchParams.setMimeType("image/png");

        LaunchParamsToIntentConverter converter = new LaunchParamsToIntentConverter(launchParams);
        Intent intent = converter.convert();

        Assert.assertEquals("image/png", intent.getType());
    }

    @Test
    public void testClassName() {
        LaunchParams launchParams = new LaunchParams();
        launchParams.setClassName("test_class_name");

        LaunchParamsToIntentConverter converter = new LaunchParamsToIntentConverter(launchParams);
        Intent intent = converter.convert();

        Assert.assertNull(intent.getComponent());
    }

    @Test
    public void testPackageName() {
        LaunchParams launchParams = new LaunchParams();
        launchParams.setPackageName("test_package_name");

        LaunchParamsToIntentConverter converter = new LaunchParamsToIntentConverter(launchParams);
        Intent intent = converter.convert();

        Assert.assertNull(intent.getComponent());
    }

    @Test
    public void testClassAndPackageName() {
        LaunchParams launchParams = new LaunchParams();
        launchParams.setClassName("test_class_name");
        launchParams.setPackageName("test_package_name");

        ComponentName expected = new ComponentName("test_package_name", "test_class_name");

        LaunchParamsToIntentConverter converter = new LaunchParamsToIntentConverter(launchParams);
        Intent intent = converter.convert();

        Assert.assertEquals(expected, intent.getComponent());
    }

    @Test
    public void testExtras() {
        LaunchParams launchParams = new LaunchParams();
        launchParams.setExtras(getExtras());

        LaunchParamsToIntentConverter converter = new LaunchParamsToIntentConverter(launchParams);
        Intent intent = converter.convert();

        assertExtrasEquals(intent);
    }

    @Test
    public void testFlags() {
        LaunchParams launchParams = new LaunchParams();
        launchParams.setFlags(getFlags());

        LaunchParamsToIntentConverter converter = new LaunchParamsToIntentConverter(launchParams);
        Intent intent = converter.convert();

        assertFlagsEquals(launchParams, intent);
    }

    @Test
    public void testSetFrom() {
        LaunchParams launchParams = new LaunchParams();
        launchParams.setAction(Intent.ACTION_ASSIST);
        launchParams.setClassName("cls_name");
        launchParams.setPackageName("pkg_name");
        launchParams.setData("data");
        launchParams.setMimeType("type");
        launchParams.setFlags(getFlags());
        launchParams.setCategories(getCategories());
        launchParams.setExtras(getExtras());

        LaunchParams launchParams2 = new LaunchParams();
        launchParams2.setFrom(launchParams);

        Assert.assertEquals(launchParams, launchParams2);
    }

    @Test
    public void testAll() {
        ComponentName expected = new ComponentName("pkg_name", "cls_name");

        LaunchParams launchParams = new LaunchParams();
        launchParams.setAction(Intent.ACTION_ASSIST);
        launchParams.setClassName(expected.getClassName());
        launchParams.setPackageName(expected.getPackageName());
        launchParams.setData("data");
        launchParams.setMimeType("type");
        launchParams.setFlags(getFlags());
        launchParams.setCategories(getCategories());
        launchParams.setExtras(getExtras());

        LaunchParamsToIntentConverter converter = new LaunchParamsToIntentConverter(launchParams);
        Intent intent = converter.convert();

        Assert.assertEquals(Intent.ACTION_ASSIST, intent.getAction());
        Assert.assertEquals(expected, intent.getComponent());
        Assert.assertEquals(Uri.parse("data"), intent.getData());
        Assert.assertEquals("type", intent.getType());
        assertFlagsEquals(launchParams, intent);
        assertCategoriesEquals(launchParams, intent);
        assertExtrasEquals(intent);
    }

    private static void assertCategoriesEquals(LaunchParams launchParams, Intent intent) {
        Set<String> intentCategories = intent.getCategories();
        List<String> categoriesValues = Category.INSTANCE.list(
                LaunchParamsExtensionsKt.getCategoriesValues(launchParams));

        Assert.assertEquals(categoriesValues.size(), intentCategories.size());

        for (String category : intentCategories) {
            Assert.assertTrue(categoriesValues.contains(category));
        }
    }

    private static void assertFlagsEquals(LaunchParams launchParams, Intent intent) {
        List<String> intentFlags = IntentExtensionsKt.getFlagsList(intent);
        for (Integer position : launchParams.getFlags()) {
            String flagName = Flag.INSTANCE.list().get(position);
            Assert.assertTrue(intentFlags.contains(flagName));
        }
    }

    private static void assertExtrasEquals(Intent intent) {
        Bundle intentExtras = intent.getExtras();

        Assert.assertNotNull(intentExtras);

        Assert.assertEquals("str", intentExtras.getString("k00"));
        Assert.assertEquals("str233", intentExtras.getString("k01"));

        Assert.assertEquals(1, intentExtras.getInt("k10"));
        Assert.assertEquals(0, intentExtras.getInt("k11"));
        Assert.assertEquals(-1, intentExtras.getInt("k12"));

        Assert.assertEquals(1, intentExtras.getLong("k20"));
        Assert.assertEquals(0, intentExtras.getLong("k21"));
        Assert.assertEquals(-1, intentExtras.getLong("k22"));

        Assert.assertEquals(Float.floatToIntBits(0.0f),
                Float.floatToIntBits(intentExtras.getFloat("k30")));
        Assert.assertEquals(Float.floatToIntBits(1.0f),
                Float.floatToIntBits(intentExtras.getFloat("k31")));
        Assert.assertEquals(Float.floatToIntBits(0.1f),
                Float.floatToIntBits(intentExtras.getFloat("k32")));
        Assert.assertEquals(Float.floatToIntBits(-0.2f),
                Float.floatToIntBits(intentExtras.getFloat("k33")));

        Assert.assertEquals(Double.doubleToLongBits(0.0),
                Double.doubleToLongBits(intentExtras.getDouble("k40")));
        Assert.assertEquals(Double.doubleToLongBits(1.0),
                Double.doubleToLongBits(intentExtras.getDouble("k41")));
        Assert.assertEquals(Double.doubleToLongBits(0.1),
                Double.doubleToLongBits(intentExtras.getDouble("k42")));
        Assert.assertEquals(Double.doubleToLongBits(-0.2),
                Double.doubleToLongBits(intentExtras.getDouble("k43")));

        Assert.assertTrue(intentExtras.getBoolean("k50"));
        Assert.assertFalse(intentExtras.getBoolean("k51"));
        Assert.assertFalse(intentExtras.getBoolean("k52"));
        Assert.assertFalse(intentExtras.getBoolean("k53"));
    }

    @NonNull
    private ArrayList<Integer> getCategories() {
        ArrayList<Integer> categories = new ArrayList<>();
        categories.add(Category.INSTANCE.list().indexOf("CATEGORY_APP_BROWSER"));
        categories.add(Category.INSTANCE.list().indexOf("CATEGORY_DEFAULT"));
        categories.add(Category.INSTANCE.list().indexOf("CATEGORY_LAUNCHER"));
        categories.add(Category.INSTANCE.list().indexOf("CATEGORY_PREFERENCE"));
        return categories;
    }

    @NonNull
    private ArrayList<Integer> getFlags() {
        ArrayList<Integer> flags = new ArrayList<>();
        flags.add(Flag.INSTANCE.list().indexOf("FLAG_ACTIVITY_CLEAR_TASK"));
        flags.add(Flag.INSTANCE.list().indexOf("FLAG_ACTIVITY_NEW_TASK"));
        flags.add(Flag.INSTANCE.list().indexOf("FLAG_ACTIVITY_NO_HISTORY"));
        flags.add(Flag.INSTANCE.list().indexOf("FLAG_ACTIVITY_SINGLE_TOP"));
        flags.add(Flag.INSTANCE.list().indexOf("FLAG_GRANT_READ_URI_PERMISSION"));
        return flags;
    }

    @NonNull
    private ArrayList<LaunchParamsExtra> getExtras() {
        ArrayList<LaunchParamsExtra> extras = new ArrayList<>();
        extras.add(new LaunchParamsExtra("k00", "str", LaunchParamsExtraType.STRING, false));
        extras.add(new LaunchParamsExtra("k01", "str233", LaunchParamsExtraType.STRING, false));

        extras.add(new LaunchParamsExtra("k10", "1", LaunchParamsExtraType.INT, false));
        extras.add(new LaunchParamsExtra("k11", "0", LaunchParamsExtraType.INT, false));
        extras.add(new LaunchParamsExtra("k12", "-1", LaunchParamsExtraType.INT, false));

        extras.add(new LaunchParamsExtra("k20", "1", LaunchParamsExtraType.LONG, false));
        extras.add(new LaunchParamsExtra("k21", "0", LaunchParamsExtraType.LONG, false));
        extras.add(new LaunchParamsExtra("k22", "-1", LaunchParamsExtraType.LONG, false));

        extras.add(new LaunchParamsExtra("k30", "0.0", LaunchParamsExtraType.FLOAT, false));
        extras.add(new LaunchParamsExtra("k31", "1.0", LaunchParamsExtraType.FLOAT, false));
        extras.add(new LaunchParamsExtra("k32", "0.1", LaunchParamsExtraType.FLOAT, false));
        extras.add(new LaunchParamsExtra("k33", "-0.2", LaunchParamsExtraType.FLOAT, false));

        extras.add(new LaunchParamsExtra("k40", "0.0", LaunchParamsExtraType.DOUBLE, false));
        extras.add(new LaunchParamsExtra("k41", "1.0", LaunchParamsExtraType.DOUBLE, false));
        extras.add(new LaunchParamsExtra("k42", "0.1", LaunchParamsExtraType.DOUBLE, false));
        extras.add(new LaunchParamsExtra("k43", "-0.2", LaunchParamsExtraType.DOUBLE, false));

        extras.add(new LaunchParamsExtra("k50", "true", LaunchParamsExtraType.BOOLEAN, false));
        extras.add(new LaunchParamsExtra("k51", "false", LaunchParamsExtraType.BOOLEAN, false));
        extras.add(new LaunchParamsExtra("k52", "kek", LaunchParamsExtraType.BOOLEAN, false));
        extras.add(new LaunchParamsExtra("k53", "33", LaunchParamsExtraType.BOOLEAN, false));
        return extras;
    }
}
