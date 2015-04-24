package com.shopping.fruit.client.order;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.BaseActivity;
import com.shopping.fruit.client.order.page.AccountPage;

/**
 * 结算页面
 *
 * @author keshuangjie
 * @date 2015-04-12 15:59
 */
public class AccountActivity extends BaseActivity{

    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fm = getSupportFragmentManager();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("结算页");

        setContentView(R.layout.activity_accout);

        initView();
    }

    private void initView() {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new AccountPage();
        fragment.setArguments(getIntent().getExtras());
        ft.add(R.id.fl_content, fragment).commit();
    }



}
