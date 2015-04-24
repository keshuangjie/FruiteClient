package com.shopping.fruit.client.usercenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.BaseActivity;
import com.shopping.fruit.client.usercenter.page.LoginPage;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-04-20 20:20
 */
public class LoginActivity extends BaseActivity{

    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fm = getSupportFragmentManager();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("登录");

        setContentView(R.layout.activity_container);

        initView();
    }

    private void initView() {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new LoginPage();
        ft.add(R.id.fl_content, fragment).commit();
    }

}
