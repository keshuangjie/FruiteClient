package com.shopping.fruit.client.order.page;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.AbsAdapter;
import com.shopping.fruit.client.base.MyListPage;
import com.shopping.fruit.client.common.CommonApi;
import com.shopping.fruit.client.entity.Product;
import com.shopping.fruit.client.home.page.MainPage;
import com.shopping.fruit.client.network.RequestWithCookie;
import com.shopping.fruit.client.order.adapter.AccoutAdapter;
import com.shopping.fruit.client.usercenter.controller.AddressListController;
import com.shopping.fruit.client.usercenter.entity.AddressInfo;
import com.shopping.fruit.client.usercenter.page.AddAddressPage;
import com.shopping.fruit.client.usercenter.page.AddrssListPage;
import com.shopping.fruit.client.util.Log;
import com.sinaapp.whutec.util.common.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 结算页
 *
 * @author keshuangjie
 * @date 2015-04-11 16:16
 */
public class AccountPage extends MyListPage<Product> implements View.OnClickListener {

    public static final String BACK_FROM_KEY = "key_from";

    public static final int BACK_FROM_ADDRESS = 1;

    private int shopId;
    private String skus;
    private double priceTotal;

    private ArrayList<Product> mProducts;

    private TextView tv_priceTotal, tv_comfirm;
    private View mHeaderView;
    private AddressInfo mAddressInfo;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateData();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null){
            shopId = bundle.getInt("shopId");
            skus = bundle.getString("skuList");
            priceTotal = bundle.getDouble("priceTotal");
            Log.i("kshj", "AccountPage -> onCreate() -> shopId: " + shopId);
            Log.i("kshj", "AccountPage -> onCreate() -> skus: " + skus);
        }

        setTitle("结算页");
        setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected View onCreatePageContent(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_account, container, false);
        return view;
    }

    @Override
    protected void onFindViews(View view) {
        super.onFindViews(view);
        initBottomView(view);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initBottomView(View view) {
        tv_priceTotal = (TextView) view.findViewById(R.id.tv_totalPrice);
        tv_comfirm = (TextView) view.findViewById(R.id.tv_comfirm);
        tv_comfirm.setOnClickListener(this);
    }

    @Override
    protected void addHeaderView() {
        mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.page_accout_header, null);
        mHeaderView.setVisibility(View.GONE);
        mListView.addHeaderView(mHeaderView, null, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_comfirm:
                submitOrder();
                break;
            case R.id.tv_add_address:
                navigateTo(AddAddressPage.class.getName(), null);
                break;
            case R.id.rl_address_item:
                Bundle bundle = new Bundle();
                bundle.putString("from", AccountPage.class.getName());
                navigateTo(AddrssListPage.class.getName(), bundle);
                break;
        }
    }

    private void submitOrder() {
        if (mAddressInfo == null) {
            ToastUtil.getInstance().toast("请添加收货地址");
            return;
        }

        if (mProducts == null || mProducts.size() == 0) {
            ToastUtil.getInstance().toast("订单商品不能为空");
        }

        String url = CommonApi.SUBMIT_ORDER + "?salerId=" + shopId + "&skuList=" + skus + "&priceTotal="
                + priceTotal + "&userAddressId=" + 1 + "&sendDay=20150426" + "&sendTimeSpan=20:00~21:40";

        Log.i("kshj", "AccountPage -> submitOrder -> url: " + url);

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        RequestWithCookie request = new RequestWithCookie(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(jsonObject != null){
                    Log.i("kshj", "AccountPage -> submitOrder -> json: " + jsonObject.toString());
                }
                ToastUtil.getInstance().toast("下单成功");
                goToMainPage(MainPage.PAGE_ORDER_INDEX, true);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                ToastUtil.getInstance().toast("下单失败");
            }
        });
        queue.add(request);
    }

    private void updateData(){
        tv_priceTotal.setText("总价：￥" + priceTotal);

        updateAddressView();
    }

    private void updateAddressView(){
        mHeaderView.setVisibility(View.VISIBLE);
        TextView tv_add_address = (TextView) mHeaderView.findViewById(R.id.tv_add_address);
        RelativeLayout rl_address_item = (RelativeLayout) mHeaderView.findViewById(R.id.rl_address_item);
        if (mAddressInfo == null){
            tv_add_address.setVisibility(View.VISIBLE);
            tv_add_address.setOnClickListener(this);
            rl_address_item.setVisibility(View.GONE);
        } else {
            tv_add_address.setVisibility(View.GONE);
            rl_address_item.setVisibility(View.VISIBLE);
            TextView tv_name = (TextView) rl_address_item.findViewById(R.id.tv_name);
            TextView tv_phone = (TextView) rl_address_item.findViewById(R.id.tv_phone);
            TextView tv_address = (TextView) rl_address_item.findViewById(R.id.tv_address);
            ImageView im_arrow = (ImageView) rl_address_item.findViewById(R.id.iv_edit);
            im_arrow.setImageResource(R.drawable.mypage_list_icon_arrow);
            tv_name.setText(mAddressInfo.name);
            tv_phone.setText(mAddressInfo.phone);
            tv_address.setText(mAddressInfo.address);
            rl_address_item.setOnClickListener(this);
        }
    }

    private void goToAddressListPage() {
//        Intent intent = new Intent(getActivity(), )
    }

    @Override
    public void onBackFromOtherPage(Bundle args) {
        if(args != null) {
            String from = args.getString("from");
            if (!TextUtils.isEmpty(from)){
                if(AddrssListPage.class.getName().equals(from)){
                    mAddressInfo = new AddressInfo();
                    mAddressInfo.id = args.getInt("id");
                    mAddressInfo.name = args.getString("name");
                    mAddressInfo.phone = args.getString("telephone");
                    mAddressInfo.address = args.getString("address");
                    updateAddressView();
                }
            }
        }
    }

    @Override
    protected String buildUrl() {
        String url = CommonApi.GO_TO_ACCOUNT + "?salerId=" + shopId + "&skuList=" + skus + "&priceTotal=" + priceTotal;
        Log.i("kshj", "AccountPage -> buildUrl() -> url: " + url);
        return url;
    }

    @Override
    protected ArrayList<Product> parseData(JSONObject json, int type) {
        JSONObject data = json.optJSONObject("data");
        if(data != null && !TextUtils.isEmpty(data.toString())){
            Log.i("kshj", "AccoutPage -> parseData -> json: " + json.toString());
            priceTotal = data.optDouble("priceTotal");
            JSONObject addressJson = data.optJSONObject("address");
            if (addressJson.optInt("exist") == 0){
                mAddressInfo = new AddressInfo();
                mAddressInfo.name = addressJson.optString("name");
                mAddressInfo.phone = addressJson.optString("telephone");
                mAddressInfo.address = addressJson.optString("address");
            }
            JSONArray array = data.optJSONArray("skuIdList");
            if (array != null) {
               skus = array.toString();
                Log.i("kshj", "AccoutPage -> parseData() -> skus: " + skus);
            }
            mProducts = Product.parseAccount(data);
        }

        Message message = mHandler.obtainMessage();
        mHandler.sendMessage(message);

        return mProducts;
    }

    @Override
    protected AbsAdapter<Product> initAdapter() {
        return new AccoutAdapter(getActivity());
    }

}