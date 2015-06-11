package com.shopping.fruit.client.usercenter.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    public static String KEY_BACK = "back";
    public static final int EVENT_ADD = 0;
    public static final int EVENT_UPDATE = 1;
    public static final int EVENT_DELETE = 2;

    AddressListController mController;

    private Bundle mBundleFromBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mController = new AddressListController(getActivity());
        mController.initData(getPageArguments());

        setTitle("我的地址");
        setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mBundleFromBack != null) {
            int eventId = mBundleFromBack.getInt(KEY_BACK);
            final AddressInfo info = mBundleFromBack.getParcelable("addressInfo");
            if (info == null) {
                return;
            }
            switch (eventId) {
                case EVENT_ADD:
                    addAddress(info);
                    break;
                case EVENT_DELETE:
                    deleteAddress(info);
                    break;
                case EVENT_UPDATE:
                    editAddress(info);
                    break;
            }

            mBundleFromBack = null;
        }
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
        } else {
            AddressInfo item = (AddressInfo) arg0.getAdapter().getItem(arg2);
            Bundle bundle = new Bundle();
            bundle.putParcelable("address", item);
            navigateTo(AddAddressPage.class.getCanonicalName(), bundle);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.sure);
        item.setTitle("新增");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sure:
                navigateTo(AddAddressPage.class.getCanonicalName(), null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackFromOtherPage(Bundle args) {
        mBundleFromBack = args;
    }

    private void editAddress(AddressInfo info) {
        if (mContents == null) {
            mContents = new ArrayList<AddressInfo>();
        }
        for (AddressInfo address : mContents) {
            if (address.id == info.id) {
                address.name = info.name;
                address.phone = info.phone;
                address.address = info.address;
                address.detail = info.detail;
                break;
            }
        }
        mAdapter.setContents(mContents);
    }

    private void addAddress(AddressInfo info) {
        if (mContents == null) {
            mContents = new ArrayList<AddressInfo>();
        }
        mContents.add(info);
        mAdapter.setContents(mContents);
    }

    private void deleteAddress(AddressInfo info) {
        if (mContents == null) {
            mContents = new ArrayList<AddressInfo>();
        }
        for (AddressInfo address : mContents) {
            if (address.id == info.id) {
                mContents.remove(address);
                break;
            }
        }
        mAdapter.setContents(mContents);
    }
}
