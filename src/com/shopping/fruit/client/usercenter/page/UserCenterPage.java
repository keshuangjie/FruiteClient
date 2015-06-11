package com.shopping.fruit.client.usercenter.page;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.CommonPage;
import com.shopping.fruit.client.common.CommonApi;
import com.shopping.fruit.client.entity.ResultEntity;
import com.shopping.fruit.client.home.page.MainPage;
import com.shopping.fruit.client.network.LibCookieManager;
import com.shopping.fruit.client.network.RequestWithCookie;
import com.shopping.fruit.client.usercenter.LoginActivity;
import com.shopping.fruit.client.usercenter.entity.UserInfo;
import com.sinaapp.whutec.util.common.ToastUtil;

import org.json.JSONObject;

/**
 * 个人主页
 *
 * @author keshuangjie
 * @date 2015-04-20 20:25
 */
public class UserCenterPage extends CommonPage implements View.OnClickListener {
    private static final int MESSAGE_UPDATE = 1;
    private static final int MESSAGE_NOT_LOGIN = 2;
    private static final int Message_ERROR = 3;

    private LinearLayout ll_user;
    private TextView tv_name;
    private Button btn_login;

    private UserInfo mUserInfo;

    private boolean mIsFirst = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE:
                    mUserInfo = (UserInfo) msg.obj;
                    updateData();
                    break;
                case MESSAGE_NOT_LOGIN:
                    showNoLogin();
                    break;
                case Message_ERROR:
                    showNoLogin();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_usercenter, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mIsFirst && LibCookieManager.isCookieChange()) {
            getUserInfo();
        }

        mIsFirst = false;
    }

    private void initView(View view) {
        ll_user = (LinearLayout) view.findViewById(R.id.ll_user);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        btn_login = (Button) view.findViewById(R.id.btn_login);
        ll_user.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        view.findViewById(R.id.tv_address).setOnClickListener(this);
        view.findViewById(R.id.tv_setting).setOnClickListener(this);
    }

    private void updateData(){
        if (mUserInfo != null) {
            tv_name.setText(mUserInfo.name);
            ll_user.setVisibility(View.VISIBLE);
            btn_login.setVisibility(View.GONE);
        }
    }

    private void showNoLogin() {
        btn_login.setVisibility(View.VISIBLE);
        ll_user.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();

        getUserInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_address:
                navigateTo(AddrssListPage.class.getCanonicalName(), null);
                break;
            case R.id.btn_login:
                goToLoginPage();
                break;
            case R.id.tv_setting:
                navigateTo(SettingPage.class.getCanonicalName(), null);
                break;
            case R.id.ll_user:
                navigateTo(UserInfoPage.class.getCanonicalName(), null);
        }
    }

    private void getUserInfo() {
        showProgress();
        String url = CommonApi.GET_USER_INFO;
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        RequestWithCookie request = new RequestWithCookie(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject jsonObject) {
                dismissProgress();
                ResultEntity entity = new ResultEntity(jsonObject);
                Message msg = mHandler.obtainMessage();
                if (entity.isSuccess()) {
                    UserInfo info = UserInfo.parseUserInfo(jsonObject.optJSONObject("data"));
                    msg.what = MESSAGE_UPDATE;
                    msg.obj = info;
                    mHandler.sendMessage(msg);
                } else if (entity.isNotLogin()) {
                    ToastUtil.getInstance().toast("服务端缓存没有该手机号的验证码，需要重新发送验证码");
                    msg.what = MESSAGE_NOT_LOGIN;
                    mHandler.sendMessage(msg);
                } else {
                    ToastUtil.getInstance().toast("网络异常");
                    msg.what = Message_ERROR;
                    mHandler.sendMessage(msg);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismissProgress();
                volleyError.printStackTrace();
                ToastUtil.getInstance().toast("网络异常");
                Message msg = mHandler.obtainMessage();
                msg.what = Message_ERROR;
                mHandler.sendMessage(msg);
            }
        }


        );
        queue.add(request);
    }

    @Override
    public boolean isWidgetPage() {
        return true;
    }
}
