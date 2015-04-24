package com.shopping.fruit.client.shop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.BaseActivity;
import com.shopping.fruit.client.shop.page.ProductListPage;
import com.shopping.fruit.client.shop.page.ShoppingCartPage;

/**
 * Created by keshuangjie on 2015/3/29.
 */
public class ShopDetailActivity extends BaseActivity{

    private String shopName;
    private int salerId;

    FragmentManager fm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_shopdetail);
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            finish();
        }
        shopName = bundle.getString("name");
        salerId = bundle.getInt("salerId");

        fm = getSupportFragmentManager();

        initView();
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(shopName);

        addProductListView();
        addShoppintCartView();
    }

    private void addProductListView() {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new ProductListPage();
        fragment.setArguments(getIntent().getExtras());
        ft.replace(R.id.fl_content, fragment).commit();
    }

    private void addShoppintCartView(){
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new ShoppingCartPage();
        fragment.setArguments(getIntent().getExtras());
        ft.replace(R.id.fl_shoppingcart, fragment).commit();
    }

}
