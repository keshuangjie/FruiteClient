package com.shopping.fruit.client.base;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.shopping.fruit.client.base.pagestack.BaseTask;

/**
 * Created by keshuangjie on 2015/3/16.
 */
public class BaseActivity extends BaseTask{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                return true;
//        }
//        return false;
//    }
}
