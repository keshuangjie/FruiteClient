package com.shopping.fruit.client.usercenter.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.AbsAdapter;
import com.shopping.fruit.client.usercenter.controller.AddressListController;
import com.shopping.fruit.client.usercenter.entity.AddressInfo;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-04-12 13:08
 */
public class AddressAdapter extends AbsAdapter<AddressInfo> {

    private AddressListController mController;

    public AddressAdapter(Context context){
        super(context);
    }

    public void setController(AddressListController controller) {
        this.mController = controller;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.page_address_list_item, parent, false);
        }
        AddressInfo item = mContent.get(position);
        if(item != null){
            TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
            TextView tv_phone = ViewHolder.get(convertView, R.id.tv_phone);
            TextView tv_address = ViewHolder.get(convertView, R.id.tv_address);
            tv_name.setText(item.name);
            tv_phone.setText(item.phone);
            tv_address.setText(item.address);
        }
        return convertView;
    }
 }
