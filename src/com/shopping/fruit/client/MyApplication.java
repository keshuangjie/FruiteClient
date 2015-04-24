package com.shopping.fruit.client;

import android.app.Application;

import com.sinaapp.whutec.util.Initializer;

/**
 * Created by keshuangjie on 2015/3/16.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Initializer.init(this);
    }

}
