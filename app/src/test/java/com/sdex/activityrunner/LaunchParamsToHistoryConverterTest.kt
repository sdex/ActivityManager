package com.sdex.activityrunner;

import com.sdex.activityrunner.db.history.HistoryModel;
import com.sdex.activityrunner.intent.LaunchParams;
import com.sdex.activityrunner.intent.converter.LaunchParamsToHistoryConverter;

import org.junit.Assert;
import org.junit.Test;

public class LaunchParamsToHistoryConverterTest {

    @Test
    public void testConvertEmpty() {
        LaunchParams launchParams = new LaunchParams();

        LaunchParamsToHistoryConverter converter = new LaunchParamsToHistoryConverter(launchParams);
        HistoryModel historyModel = converter.convert();

        Assert.assertEquals(launchParams.getAction(), historyModel.getAction());
        Assert.assertEquals(launchParams.getData(), historyModel.getData());
        Assert.assertEquals(launchParams.getMimeType(), historyModel.getMimeType());
        Assert.assertEquals(launchParams.getPackageName(), historyModel.getPackageName());
        Assert.assertEquals(launchParams.getClassName(), historyModel.getClassName());
    }

    @Test
    public void testConvert() {
        LaunchParams launchParams = new LaunchParams();
        launchParams.setAction("action");
        launchParams.setData("data");
        launchParams.setMimeType("type");
        launchParams.setPackageName("pkg");
        launchParams.setClassName("cls");

        LaunchParamsToHistoryConverter converter = new LaunchParamsToHistoryConverter(launchParams);
        HistoryModel historyModel = converter.convert();

        Assert.assertEquals(launchParams.getAction(), historyModel.getAction());
        Assert.assertEquals(launchParams.getData(), historyModel.getData());
        Assert.assertEquals(launchParams.getMimeType(), historyModel.getMimeType());
        Assert.assertEquals(launchParams.getPackageName(), historyModel.getPackageName());
        Assert.assertEquals(launchParams.getClassName(), historyModel.getClassName());
    }
}
