package com.shopping.fruit.client.order.page;

import com.shopping.fruit.client.base.AbsAdapter;
import com.shopping.fruit.client.base.MyListPage;
import com.shopping.fruit.client.common.CommonApi;
import com.shopping.fruit.client.order.adapter.OrderListAdapter;
import com.shopping.fruit.client.order.entity.Order;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 我的订单页面
 *
 * @author keshuangjie
 * @date 2015-04-26 15:20
 */
public class OrderListPage extends MyListPage<Order> {

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected String buildUrl() {
        return CommonApi.GET_USER_ORDERS;
    }

    @Override
    protected ArrayList<Order> parseData(JSONObject json, int type) {
        return Order.parse(json);
    }

    @Override
    protected AbsAdapter<Order> initAdapter() {
        return new OrderListAdapter(getActivity());
    }
}
