package com.shopping.fruit.client.usercenter.page;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.CommonPage;
import com.shopping.fruit.client.common.CommonApi;
import com.shopping.fruit.client.entity.ResultEntity;
import com.shopping.fruit.client.network.RequestWithCookie;
import com.shopping.fruit.client.usercenter.entity.AddressInfo;
import com.shopping.fruit.client.util.Log;
import com.shopping.fruit.client.widget.CustomDialog;
import com.shopping.fruit.client.widget.DialogUtil;
import com.sinaapp.whutec.util.common.StringUtil;
import com.sinaapp.whutec.util.common.ToastUtil;

import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * 新增送货地址
 *
 * @author keshuangjie
 * @date 2015-04-02 20:49
 */
public class AddAddressPage extends CommonPage {

    private AutoCompleteTextView tv_name, tv_phone, tv_addr, tv_addr_detail;
    private ImageView iv_name_del, iv_phone_del, iv_userAdress_del;

    private boolean mIsEdit = false;
    private AddressInfo mInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = getArguments();
        if (data != null) {
            mInfo = data.getParcelable("address");
            if (mInfo != null) {
                mIsEdit = true;
            }
        }

        if (mIsEdit) {
            setTitle("编辑地址");
        } else {
            setTitle("新增地址");
        }
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
        tv_name = (AutoCompleteTextView) view.findViewById(R.id.address_username);
        tv_phone = (AutoCompleteTextView) view.findViewById(R.id.address_userphone);
        tv_addr = (AutoCompleteTextView) view.findViewById(R.id.address_useraddress);
        tv_addr_detail = (AutoCompleteTextView) view.findViewById(R.id.detail_useraddress);

        iv_name_del = (ImageView) view.findViewById(R.id.address_username_del);
        iv_phone_del = (ImageView) view.findViewById(R.id.address_userphone_del);
        iv_userAdress_del = (ImageView) view.findViewById(R.id.address_useraddress_del);

        tv_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    iv_name_del.setVisibility(View.GONE);
                } else {
                    iv_name_del.setVisibility(View.VISIBLE);
                }
            }
        });

        tv_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    iv_phone_del.setVisibility(View.GONE);
                } else {
                    iv_phone_del.setVisibility(View.VISIBLE);
                }
            }
        });

        tv_addr_detail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    iv_userAdress_del.setVisibility(View.GONE);
                } else {
                    iv_userAdress_del.setVisibility(View.VISIBLE);
                }
            }
        });


        iv_name_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_name.setText("");
            }
        });
        iv_phone_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_phone.setText("");
            }
        });
        iv_userAdress_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_addr_detail.setText("");
            }
        });

        if (mIsEdit && mInfo != null) {
            tv_name.setText(mInfo.name);
            tv_phone.setText(mInfo.phone);
            tv_addr.setText(mInfo.address);
            tv_addr_detail.setText(mInfo.detail);

            iv_name_del.setVisibility(View.VISIBLE);
            iv_phone_del.setVisibility(View.VISIBLE);
            iv_userAdress_del.setVisibility(View.VISIBLE);

            Button btn_del = (Button) view.findViewById(R.id.btn_del);
            btn_del.setVisibility(View.VISIBLE);
            btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtil.showCustomDialog(getActivity(), "确认删除此条地址", new CustomDialog.OnEventListener() {

                        @Override
                        public void onConfirm() {
                            delete();
                        }

                        @Override
                        public void onCancle() {
                        }
                    });
                }
            });
        }
    }

    private void delete() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = CommonApi.DELETE_ADDRESS + "?userAddressId=" + mInfo.id;
        RequestWithCookie request = new RequestWithCookie(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject jsonObject) {
                ResultEntity result = new ResultEntity(jsonObject);
                if (result.isSuccess()) {
                    ToastUtil.getInstance().toast("删除地址成功");
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("addressInfo", mInfo);
                    bundle.putInt(AddrssListPage.KEY_BACK, AddrssListPage.EVENT_DELETE);
                    goBack(bundle);
                } else if (result.isNotLogin()) {
                    ToastUtil.getInstance().toast("未登录");
                } else {
                    ToastUtil.getInstance().toast("删除地址失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                ToastUtil.getInstance().toast("删除地址失败");
            }
        });
        queue.add(request);
    }

    private void commit(){
        String name = tv_name.getText().toString();
        String phone = tv_phone.getText().toString();
        String address = tv_addr.getText().toString();
        String detailAddress = tv_addr_detail.getText().toString();

        if (TextUtils.isEmpty(name)) {
            ToastUtil.getInstance().toast("用户名不能为空");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            ToastUtil.getInstance().toast("联系方式不能为空");
            return;
        }

        if (!StringUtil.isMobileNumber(phone)) {
            ToastUtil.getInstance().toast("手机格式不正确");
            return;
        }

        if (TextUtils.isEmpty(address) || TextUtils.isEmpty(detailAddress)) {
            ToastUtil.getInstance().toast("地址不能为空");
            return;
        }

        if (mInfo == null) {
            mInfo = new AddressInfo();
        }

        mInfo.name = name;
        mInfo.address = address;
        mInfo.phone = phone;
        mInfo.detail = detailAddress;


        RequestQueue queue = Volley.newRequestQueue(getActivity());
        RequestWithCookie request = new RequestWithCookie(Request.Method.GET,
                buildUrl(),
                null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject jsonObject) {
                ResultEntity result = new ResultEntity(jsonObject);
                if (result.isSuccess()) {
                    JSONObject data = jsonObject.optJSONObject("data");
                    if (data != null && TextUtils.isEmpty(data.toString())) {
                        mInfo.id = data.optInt("id");
                    }
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("addressInfo", mInfo);
                    if (mIsEdit) {
                        bundle.putInt(AddrssListPage.KEY_BACK, AddrssListPage.EVENT_UPDATE);
                        ToastUtil.getInstance().toast("更新地址成功");
                    } else {
                        bundle.putInt(AddrssListPage.KEY_BACK, AddrssListPage.EVENT_ADD);
                        ToastUtil.getInstance().toast("添加地址成功");
                    }
                    goBack(bundle);
                } else if (result.isNotLogin()) {
                    ToastUtil.getInstance().toast("未登录");
                } else {
                    if (mIsEdit) {
                        ToastUtil.getInstance().toast("更新地址失败");
                    } else {
                        ToastUtil.getInstance().toast("添加地址失败");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (mIsEdit) {
                    ToastUtil.getInstance().toast("更新地址失败");
                } else {
                    ToastUtil.getInstance().toast("添加地址失败");
                }
            }
        });
        queue.add(request);

    }

    private String buildUrl(){
        StringBuilder builder = new StringBuilder();
        if (mIsEdit && mInfo != null) {
            builder.append(CommonApi.UPDATE_USER_ADDRESS);
            builder.append("?");
            builder.append("userAddressId=");
            builder.append(mInfo.id);
            builder.append("&");
        } else {
            builder.append(CommonApi.ADD_USER_ADDRESS);
            builder.append("?");
        }
        builder.append("name=");
        builder.append(mInfo.name);
        builder.append("&province=北京");
        builder.append("&city=北京");
        builder.append("&district=");
        builder.append(mInfo.address);
        builder.append("&detail=");
        builder.append(mInfo.detail);
        builder.append("&telephone=");
        builder.append(mInfo.phone);
        Log.i("kshj", "AddAddressPage -> buildUrl() -> url :" + builder.toString());
        return builder.toString();
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
