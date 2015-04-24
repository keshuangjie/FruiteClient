package com.shopping.fruit.client.usercenter.page;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.shopping.fruit.client.util.TimerControler;
import com.sinaapp.whutec.util.common.StringUtil;
import com.sinaapp.whutec.util.common.ToastUtil;

import org.json.JSONObject;

/**
 * 登录界面
 *
 * @author keshuangjie
 * @date 2015-04-12 11:36
 */
public class LoginPage extends CommonPage implements TimerControler.OnChangeDurationLinter, View.OnClickListener {

    private EditText et_phone;
    private EditText et_code;
    private ImageView im_clear;
    private TextView btn_getPassword, btn_login;

    private TimerControler mTimerControler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTimerControler = TimerControler.getInstance();
        mTimerControler.setDuration( 60 * 1000);
        mTimerControler.setOnChangeDurationLinter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fast, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        et_phone = (EditText) view.findViewById(R.id.et_phone);
        et_code = (EditText) view.findViewById(R.id.et_code);
        im_clear = (ImageView) view.findViewById(R.id.im_clear);
        btn_getPassword = (TextView) view.findViewById(R.id.btn_get_password);
        btn_login = (TextView) view.findViewById(R.id.btn_login);
        im_clear.setOnClickListener(this);
        btn_getPassword.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_clear:
                break;
            case R.id.btn_get_password:
                getPassword();
                break;
            case R.id.btn_login:
                login();
                break;
        }

    }

    private void getPassword(){
        String phone = et_phone.getText().toString();
        if (TextUtils.isEmpty(phone)){
            ToastUtil.getInstance().toast("手机号码不能为空");
            return;
        }

        if (!StringUtil.isMobileNumber(phone)) {
            ToastUtil.getInstance().toast("手机号码格式不对");
            return;
        }

        String url = CommonApi.SEND_VERIFYCODE + "?telephone=" + phone;
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject jsonObject) {
//                parseResponse(jsonObject, type);
                mTimerControler.start();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
//                sendHandlerMessage(MESSAGE_NET_ERROR, null, type);
            }
        });
        queue.add(request);

    }

    private void login(){
        String phone = et_phone.getText().toString();
        String code = et_code.getText().toString();
        if (TextUtils.isEmpty(phone)){
            ToastUtil.getInstance().toast("手机号码不能为空");
            return;
        }

        if (!StringUtil.isMobileNumber(phone)) {
            ToastUtil.getInstance().toast("手机号码格式不对");
            return;
        }

        if(TextUtils.isEmpty(code)){
            ToastUtil.getInstance().toast("请输入动态密码");
        }

        String url = CommonApi.LOGIN + "?telephone=" + phone + "&verifyCode=" + code;
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        RequestWithCookie request = new RequestWithCookie(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject jsonObject) {
//                parseResponse(jsonObject, type);
                ResultEntity entity = new ResultEntity(jsonObject);
                if (entity.isSuccess()) {
                    ToastUtil.getInstance().toast("登录成功");
                    getActivity().finish();
                } else {
                    ToastUtil.getInstance().toast("服务端缓存没有该手机号的验证码，需要重新发送验证码");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
//                sendHandlerMessage(MESSAGE_NET_ERROR, null, type);
                ToastUtil.getInstance().toast("登录错误");
            }
        }


        );
        queue.add(request);

    }

    @Override
    public void changeDuration(long duration) {
        this.btn_getPassword.setEnabled(false);
        this.btn_getPassword.setText("发送动态密码(" + (duration / 1000) + ")");
    }

    @Override
    public void stop() {
        this.btn_getPassword.setEnabled(true);
        this.btn_getPassword.setText("发送动态密码");
    }

    @Override
    public void timeOut() {
        this.btn_getPassword.setEnabled(true);
        this.btn_getPassword.setText("发送动态密码");
    }
}