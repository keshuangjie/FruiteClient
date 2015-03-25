package com.shopping.fruit.client.shop;

import android.os.Bundle;
import android.text.TextUtils;

import com.shopping.fruit.client.base.AbsAdapter;
import com.shopping.fruit.client.base.MyListFragment;
import com.shopping.fruit.client.common.CommonApi;
import com.shopping.fruit.client.entity.Product;
import com.shopping.fruit.client.shop.adapter.ProductAdapter;
import com.shopping.fruit.client.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 商家详情页
 *
 * @author keshuangjie
 * @date 2015-03-25 19:59
 */
public class ShopDetailPage extends MyListFragment<Product> {

    private int salerId = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null){
            salerId = bundle.getInt("salerId");
        }
    }

    @Override
    protected AbsAdapter<Product> initAdapter() {
        return new ProductAdapter(getActivity());
    }

    @Override
    protected String buildUrl() {
        return CommonApi.SALER_DETAIL + "?salerId=" + salerId;
    }

    @Override
    protected ArrayList<Product> parseData(JSONObject json, int type) {
        Log.i("kshj - ShopDetailPage - parseData: ", json.toString());
        if(json == null || TextUtils.isEmpty(json.toString())){
            return null;
        }
        return Product.parse(json);
    }
}
