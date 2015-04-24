package com.shopping.fruit.client.shop.page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.CommonPage;
import com.shopping.fruit.client.entity.Product;
import com.shopping.fruit.client.entity.ShoppingCartEntity;
import com.shopping.fruit.client.event.ShoppintCartEvent;
import com.shopping.fruit.client.order.AccountActivity;
import com.shopping.fruit.client.order.page.AccountPage;
import com.shopping.fruit.client.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import de.greenrobot.event.EventBus;

/**
 * Created by keshuangjie on 2015/3/29.
 */
public class ShoppingCartPage extends CommonPage implements View.OnClickListener{

    private State mState = State.STATE_INIT;

    private TextView tv_count, tv_totalPrice, tv_begin, tv_remain, tv_commit;

    private ShoppingCartEntity mItem;

    private int shopId;

    private double mBeginDeliverPrice = 0; //起送价

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle argument = getArguments();
        if(argument != null){
            shopId = argument.getInt("salerId");
        }

        mItem = new ShoppingCartEntity();
        mItem.shopId = shopId;
    }

    @Override
    protected View onCreatePageContent(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_shoppingcart, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView(View view) {
        tv_count = (TextView) view.findViewById(R.id.tv_begin);
        tv_totalPrice = (TextView) view.findViewById(R.id.tv_totalPrice);
        tv_begin = (TextView) view.findViewById(R.id.tv_begin);
        tv_remain = (TextView) view.findViewById(R.id.tv_remain);
        tv_commit = (TextView) view.findViewById(R.id.tv_commit);
        tv_commit.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        updateView();
    }


    private void updateView(){
        showLeftView();
        showRightView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_commit:
                goToAccountPage();
                break;
        }
    }

    private void goToAccountPage(){
        String skuList = buildJsonObject();
        if (!TextUtils.isEmpty(skuList)) {
            Activity activity = getActivity();
//            Intent intent = new Intent(activity, AccountActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("shopId", shopId);
            bundle.putString("skuList", skuList);
            bundle.putDouble("priceTotal", mItem.totalPrice);
//            intent.putExtras(bundle);
//            activity.startActivity(intent);
            navigateTo(AccountPage.class.getName(), bundle);
        }
    }

    private String buildJsonObject() {
        if (mItem != null && mItem.products != null && mItem.products.size() > 0) {
            JSONArray array = new JSONArray();
            for(Product p : mItem.products){
                JSONObject item = new JSONObject();
                try {
                    item.put("id", p.id);
                    item.put("number", p.selectedCount);
                    array.put(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.i("kshj", "ShoppintCartPage -> buildJsonObject() -> array: " + array.toString());
            return array.toString();
        }
        return null;
    }

    /**
     * 购物车是空的
     */
    private void showLeftView(){
        if (mState == State.STATE_INIT || mItem == null){
//            tv_count.setVisibility(View.GONE);
            tv_totalPrice.setText("购物车是空的");
        }else{
            tv_totalPrice.setText("共" + mItem.totalPrice + "元");
        }
    }

    private void showRightView(){
        if (mState == State.STATE_INIT || mItem == null){
            tv_begin.setVisibility(View.VISIBLE);
            tv_begin.setText(mBeginDeliverPrice + "元起送");
            tv_remain.setVisibility(View.GONE);
            tv_commit.setVisibility(View.GONE);
        }else if (mState == State.STATE_REMAIN) {
            tv_remain.setText("还差" + (mBeginDeliverPrice - mItem.totalPrice) + "元起送");
            tv_remain.setVisibility(View.VISIBLE);
            tv_begin.setVisibility(View.GONE);
            tv_commit.setVisibility(View.GONE);
        }else {
            tv_commit.setVisibility(View.VISIBLE);
            tv_commit.setText("选好了");
            tv_begin.setVisibility(View.GONE);
            tv_remain.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void updateState(){
        if(mItem != null){
            if (mItem.totalPrice <= 0){
                mState = State.STATE_INIT;
            } else if (mItem.totalPrice < mBeginDeliverPrice){
                mState = State.STATE_REMAIN;
            }else{
                mState = State.STATE_COMMIT;
            }
        }
        updateView();
    }

    public void onEventMainThread(ShoppintCartEvent entity){
        Log.i("kshj", "ShoppintCarPage -> onMainEventThread ->" );
        if(mItem == null) {
            mItem = new ShoppingCartEntity();
            mItem.shopId = shopId;
        }
        mItem.products = entity.products;
        mItem.totalPrice = 0;
        mItem.count = 0;
        if (mItem.products != null && mItem.products.size() > 0) {
            for(Product p : mItem.products){
                double price = mul(p.price, p.selectedCount);
                mItem.totalPrice = sum(price, mItem.totalPrice);
            }
            mItem.count = mItem.products.size();
        }

        updateState();
    }

    private double sum(double d1, double d2){
        BigDecimal bd1 = new BigDecimal(String.valueOf(d1));
        BigDecimal bd2 = new BigDecimal(String.valueOf(d2));
        return bd1.add(bd2).doubleValue();
    }

    private double mul(double d1, double d2){
        BigDecimal bd1 = new BigDecimal(String.valueOf(d1));
        BigDecimal bd2 = new BigDecimal(String.valueOf(d2));
        return bd1.multiply(bd2).doubleValue();
    }

    public enum State{
        STATE_INIT,     /** 购物车为空状态 */
        STATE_REMAIN,   /** 购物车不为空，但总价没达到规定起送价 */
        STATE_COMMIT;   /** 购物车不为空，可以提交订单状态 */
    }

}
