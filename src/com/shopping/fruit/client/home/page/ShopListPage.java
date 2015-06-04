package com.shopping.fruit.client.home.page;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.shopping.fruit.client.base.AbsAdapter;
import com.shopping.fruit.client.base.MyListPage;
import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.pagestack.TaskManagerFactory;
import com.shopping.fruit.client.common.CommonApi;
import com.shopping.fruit.client.common.Config;
import com.shopping.fruit.client.entity.Shop;
import com.shopping.fruit.client.home.adapter.ShopListAdapter;
import com.shopping.fruit.client.manager.LocData;
import com.shopping.fruit.client.manager.LocationEvent;
import com.shopping.fruit.client.manager.LocationManager;
import com.shopping.fruit.client.shop.ShopDetailActivity;
import com.shopping.fruit.client.shop.page.ProductListPage;
import com.shopping.fruit.client.shop.page.ShopDetailPage;
import com.shopping.fruit.client.util.Log;
import com.sinaapp.whutec.util.common.SharedPreUtil;

import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * 商店列表页
 * @author keshuangjie
 * @date 2015-3-18 11:52
 */
public class ShopListPage extends MyListPage<Shop> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        LocationManager.getInstance().start(getActivity());
    }

    @Override
    protected boolean isOnStartLoad() {
        return false;
    }

    private void onEventMainThread(LocationEvent event) {
        LocData data = LocationManager.getInstance().getLocData();
        setTitle(data.mDetailAddr);
        initData();
    }

    @Override
    protected View onCreatePageContent(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_shop_list, container,
                false);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Log.i("kshj", "ShopListPage -> onItemClick()");
        Shop item = (Shop) arg0.getAdapter().getItem(arg2);
        if(item != null){
            Log.i("kshj", "ShopListPage -> onItemClick() -> salerId:" + item.shopId);
            Intent intent = new Intent(getActivity(), ShopDetailActivity.class);
            Bundle bundle = new Bundle();
//            bundle.putInt("salerId", item.shopId);
            bundle.putInt("salerId", 1);
            bundle.putString("name", item.name);
            bundle.putString("description", item.description);
            bundle.putString("headImg", item.headImg);
            intent.putExtras(bundle);
//            startActivity(intent);
            navigateTo(ShopDetailPage.class.getName(), bundle);
        }
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
        LocData data = LocationManager.getInstance().getLocData();
        double lng = data.mGeoLng;
        double lat = data.mGeoLat;
        return CommonApi.NEARBY_SALER_LIST + "?longitude=" + lng + "&latitude=" + lat;
    }

    @Override
    public boolean isWidgetPage() {
        return true;
    }
}
