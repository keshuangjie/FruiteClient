package com.shopping.fruit.client.usercenter.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.AbsAdapter;
import com.shopping.fruit.client.base.MyListPage;
import com.shopping.fruit.client.common.CommonApi;
import com.shopping.fruit.client.usercenter.adapter.AddressAdapter;
import com.shopping.fruit.client.usercenter.controller.AddressListController;
import com.shopping.fruit.client.usercenter.entity.AddressInfo;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 我的地址列表页面
 *
 * @author keshuangjie
 * @date 2015-04-12 12:56
 */
public class AddrssListPage extends MyListPage<AddressInfo> {

    AddressListController mController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mController = new AddressListController(getActivity());
        mController.initData(getPageArguments());

        setTitle("我的地址");
    }

    @Override
    protected View onCreatePageContent(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_shop_list, container,
                false);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (mController.isFromAccoutPage()) {
            AddressInfo item = (AddressInfo) arg0.getAdapter().getItem(arg2);
            Bundle bundle = new Bundle();
            if (item != null) {
                bundle.putString("from", AddrssListPage.class.getName());
                bundle.putInt("id", item.id);
                bundle.putString("name", item.name);
                bundle.putString("telephone", item.phone);
                bundle.putString("address", item.address);
            }
            goBack(bundle);
        }
    }

    @Override
    protected String buildUrl() {
        return CommonApi.GET_USER_ADRESS_LIST;
    }

    @Override
    protected ArrayList parseData(JSONObject json, int type) {
        return AddressInfo.parseAdressList(json);
    }

    @Override
    protected AbsAdapter initAdapter() {
        AddressAdapter adapter = new AddressAdapter(getActivity());
        adapter.setController(mController);
        return adapter;
    }
}
