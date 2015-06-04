package com.shopping.fruit.client;

import android.app.Application;
import android.content.Context;

import com.sinaapp.whutec.util.Initializer;

/**
 * Created by keshuangjie on 2015/3/16.
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        Initializer.init(this);
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    public Context getContext() {
        return mInstance.getApplicationContext();
    }

}
