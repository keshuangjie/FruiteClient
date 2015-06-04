package com.shopping.fruit.client.order.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.AbsAdapter;
import com.shopping.fruit.client.order.entity.Order;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-04-26 15:58
 */
public class OrderListAdapter extends AbsAdapter<Order> {

    public OrderListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.page_order_list_item, parent, false);
        }

        Order item = mContent.get(position);
        if (item != null) {
            TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
            TextView tv_price = ViewHolder.get(convertView, R.id.tv_price);
            TextView tv_statu = ViewHolder.get(convertView, R.id.tv_statu);
            TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
            TextView tv_skuinfo = ViewHolder.get(convertView, R.id.tv_skuIfo);
            tv_name.setText(item.salerName);
            tv_statu.setText(item.getStatus());
            tv_price.setText(item.priceTotal + "￥");
            tv_time.setText(item.dateTime);
            tv_skuinfo.setText(item.skuInfo);
        }

        return convertView;
    }
}
