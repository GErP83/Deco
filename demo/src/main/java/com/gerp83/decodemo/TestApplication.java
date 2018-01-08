package com.gerp83.decodemo;

import android.app.Application;

import com.gerp83.deco.DecoOptions;

/**
 * Created by GErP83
 */

public class TestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DecoOptions.getInstance(getApplicationContext())
                .setImageSizeBound(getApplicationContext(), 1024)
                .setCacheSize(getApplicationContext(), DecoOptions.DEFAULT_CACHE_SIZE)
                .setCachePolicy(getApplicationContext(), DecoOptions.CACHE_POLICY_NORMAL)
                .setStorageType(getApplicationContext(), DecoOptions.STORAGE_EXTERNAL);

    }

}
