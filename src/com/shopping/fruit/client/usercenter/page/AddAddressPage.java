package com.shopping.fruit.client.usercenter.page;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.CommonPage;
import com.shopping.fruit.client.common.CommonApi;
import com.shopping.fruit.client.entity.ResultEntity;
import com.shopping.fruit.client.network.RequestWithCookie;
import com.sinaapp.whutec.util.common.ToastUtil;

import org.json.JSONObject;

/**
 * 新增送货地址
 *
 * @author keshuangjie
 * @date 2015-04-02 20:49
 */
public class AddAddressPage extends CommonPage {

    private AutoCompleteTextView atv_name, atv_phone, atv_addr, atv_addr_detail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("添加收货地址");
        setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
    }

    @Override
    protected View onCreatePageContent(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_address_addedit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void onFindViews(View view) {
        super.onFindViews(view);
        initView(view);
    }

    private void initView(View view) {
        atv_name = (AutoCompleteTextView) view.findViewById(R.id.address_username);
        atv_phone = (AutoCompleteTextView) view.findViewById(R.id.address_userphone);
        atv_addr = (AutoCompleteTextView) view.findViewById(R.id.address_useraddress);
        atv_addr_detail = (AutoCompleteTextView) view.findViewById(R.id.detail_useraddress);
    }

    private void commit(){
        String name = atv_name.getText().toString();
        String phone = atv_phone.getText().toString();
        String address = atv_addr.getText().toString();
        String detailAddress = atv_addr_detail.getText().toString();

        if (TextUtils.isEmpty(name)) {
            ToastUtil.getInstance().toast("用户名不能为空");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            ToastUtil.getInstance().toast("联系方式不能为空");
            return;
        }

        if (TextUtils.isEmpty(address) || TextUtils.isEmpty(detailAddress)) {
            ToastUtil.getInstance().toast("地址不能为空");
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        RequestWithCookie request = new RequestWithCookie(Request.Method.GET,
                buildUrl(name, phone, address, detailAddress),
                null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject jsonObject) {
                ResultEntity result = new ResultEntity(jsonObject);
                if (result.isSuccess()) {
                    ToastUtil.getInstance().toast("添加地址成功");
                    goBack();
                } else if (result.isNotLogin()) {
                    ToastUtil.getInstance().toast("未登录");
                } else {
                    ToastUtil.getInstance().toast("添加地址失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                ToastUtil.getInstance().toast("添加地址失败");
            }
        });
        queue.add(request);

    }

    private String buildUrl(final String name, final String phone, final String address, final String detailAdress){
        return CommonApi.ADD_USER_ADDRESS + "?"
                + "name=" + name
                + "&province=" + "北京"
                + "&city=" + "北京"
                + "&district=" + address
                + "&detail=" + detailAdress
                + "&telephone=" + phone;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sure:
                commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
