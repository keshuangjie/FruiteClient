package com.shopping.fruit.client.home.page;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shopping.fruit.client.base.AbsAdapter;
import com.shopping.fruit.client.base.MyListFragment;
import com.shopping.fruit.client.R;
import com.shopping.fruit.client.common.CommonApi;
import com.shopping.fruit.client.entity.Shop;
import com.shopping.fruit.client.home.adapter.ShopListAdapter;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 商店列表页
 * @author keshuangjie
 * @date 2015-3-18 11:52
 */
public class ShopListPage extends MyListFragment<Shop> {

    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.page_shop_list, container,
                false);
        return view;
    }

    @Override
    protected AbsAdapter initAdapter() {
        return new ShopListAdapter(getActivity());
    }

    @Override
    protected ArrayList<Shop> parseData(JSONObject json, int type) {
        return Shop.parse(json);
    }

    @Override
    protected String buildUrl() {
        return CommonApi.NEARBY_SALER_LIST + "?longitude=116.24&latitude=39.95&index=0&limit=10";
    }
}
