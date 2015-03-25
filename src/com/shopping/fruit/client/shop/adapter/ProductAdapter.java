package com.shopping.fruit.client.shop.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.AbsAdapter;
import com.shopping.fruit.client.entity.Product;

/**
 * 商品列表适配器
 *
 * @author keshuangjie
 * @date 2015-03-25 20:08
 */
public class ProductAdapter extends AbsAdapter<Product> {

    public ProductAdapter(Context context){
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.page_product_list_item, parent, false);
        }

        Product item = mContent.get(position);
        if (item != null) {
            TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
            TextView tv_description = ViewHolder.get(convertView, R.id.tv_description);
            TextView tv_price = ViewHolder.get(convertView, R.id.tv_price);
            TextView tv_totalSold = ViewHolder.get(convertView, R.id.tv_totalSold);
            tv_name.setText(item.name);
            tv_description.setText(item.description);
            tv_price.setText(item.price + "$/斤");
            tv_totalSold.setText("已售出" + item.totalSold + "份");
        }

        return convertView;
    }

}
