package com.shopping.fruit.client.shop.page;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.CommonPage;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-04-22 16:26
 */
public class ShopDetailPage extends CommonPage {

    private String shopName;
    private int salerId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            salerId = bundle.getInt("salerId");
            shopName = bundle.getString("name");
        }

        setTitle(shopName);
        setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected View onCreatePageContent(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_shopdetail, container, false);
        return view;
    }

    @Override
    protected void onFindViews(View view) {
        super.onFindViews(view);
        initView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView() {
        addProductListView();
        addShoppintCartView();
    }

    private void addProductListView() {
        Fragment fragment = new ProductListPage();
        addWidgetFrament(R.id.fl_product_list, fragment, getArguments());
    }

    private void addShoppintCartView(){
        Fragment fragment = new ShoppingCartPage();
        addWidgetFrament(R.id.fl_shoppingcart, fragment, getArguments());
    }
}
