package com.sdex.activityrunner;

import com.sdex.activityrunner.db.history.HistoryModel;
import com.sdex.activityrunner.intent.LaunchParams;
import com.sdex.activityrunner.intent.converter.HistoryToLaunchParamsConverter;

import org.junit.Assert;
import org.junit.Test;

public class HistoryToLaunchParamsConverterTest {

    @Test
    public void testConvertEmpty() {
        HistoryModel historyModel = new HistoryModel(
            0, 0L, null, null, null, null,
            null, null, null, null, null
        );

        HistoryToLaunchParamsConverter converter = new HistoryToLaunchParamsConverter(historyModel);
        LaunchParams launchParams = converter.convert();

        Assert.assertEquals(launchParams.getAction(), historyModel.getAction());
        Assert.assertEquals(launchParams.getData(), historyModel.getData());
        Assert.assertEquals(launchParams.getMimeType(), historyModel.getMimeType());
        Assert.assertEquals(launchParams.getPackageName(), historyModel.getPackageName());
        Assert.assertEquals(launchParams.getClassName(), historyModel.getClassName());
    }

    @Test
    public void testConvert() {
        HistoryModel historyModel = new HistoryModel(
            0, 0L, null, "pkg", "cls", "action",
            "data", "type", null, null, null
        );

        HistoryToLaunchParamsConverter converter = new HistoryToLaunchParamsConverter(historyModel);
        LaunchParams launchParams = converter.convert();

        Assert.assertEquals(launchParams.getAction(), historyModel.getAction());
        Assert.assertEquals(launchParams.getData(), historyModel.getData());
        Assert.assertEquals(launchParams.getMimeType(), historyModel.getMimeType());
        Assert.assertEquals(launchParams.getPackageName(), historyModel.getPackageName());
        Assert.assertEquals(launchParams.getClassName(), historyModel.getClassName());
    }
}
