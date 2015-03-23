package com.shopping.fruit.client.home.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopping.fruit.client.base.AbsAdapter;
import com.shopping.fruit.client.R;
import com.shopping.fruit.client.entity.Shop;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-03-19 15:44
 */
public class ShopListAdapter extends AbsAdapter<Shop> {

    public ShopListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.page_shop_list_item, parent, false);
        }

        Shop item = mContent.get(position);
        if (item != null) {
            TextView name = ViewHolder.get(convertView, R.id.name);
            name.setText(item.name);
        }

        return convertView;
    }

}