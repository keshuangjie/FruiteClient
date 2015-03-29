package com.shopping.fruit.client.shop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.widget.TextView;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.BaseActivity;
import com.shopping.fruit.client.shop.page.ShopDetailPage;

/**
 * Created by keshuangjie on 2015/3/29.
 */
public class ShopDetailActivity extends BaseActivity{

    private String shopName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopdetail);
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            finish();
        }
        shopName = bundle.getString("name");
        initView();
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(shopName);

        addProductListView();
    }

    private void addProductListView() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new ShopDetailPage();
        fragment.setArguments(getIntent().getExtras());
        ft.add(R.id.fl_content, fragment).commit();
    }

}
