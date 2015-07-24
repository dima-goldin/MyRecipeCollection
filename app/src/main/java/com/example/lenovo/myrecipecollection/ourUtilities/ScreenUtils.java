package com.example.lenovo.myrecipecollection.ourUtilities;

import android.app.Activity;
import android.content.res.Configuration;

public class ScreenUtils {

    public int getMaxWidthDp (Activity activity) {
        Configuration config = activity.getResources().getConfiguration();
        return config.screenWidthDp;
    }
}
