package com.juaracoding.contohpost;

import android.app.Application;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

@Database(name = ContohPostApplication.NAME, version = ContohPostApplication.VERSION)
public class ContohPostApplication extends Application {
    public static final String NAME = "ContohPost";
    public static final int VERSION = 1;
    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(new FlowConfig.Builder(this).build());
    }


}
