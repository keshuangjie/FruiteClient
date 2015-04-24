package com.shopping.fruit.client.order.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.AbsAdapter;
import com.shopping.fruit.client.entity.Product;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-04-12 16:34
 */
public class AccoutAdapter extends AbsAdapter<Product>{

    public AccoutAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.page_account_list_item, parent, false);
        }
        Product item = mContent.get(position);
        if(item != null){
            TextView tv_left = ViewHolder.get(convertView, R.id.tv_left);
            TextView tv_right = ViewHolder.get(convertView, R.id.tv_right);

            tv_left.setText(item.name + ":");
            tv_right.setText("￥" + item.selectedCount + "*" + item.price);
        }
        return convertView;
    }
}
