package com.shopping.fruit.client.usercenter.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.CommonPage;
import com.shopping.fruit.client.home.page.MainPage;
import com.shopping.fruit.client.network.LibCookieManager;

/**
 * 设置页面
 *
 * @author keshuangjie
 * @date 2015-06-06 12:42
 */
public class SettingPage extends CommonPage implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("设置");
        setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        view.findViewById(R.id.user_info_activity_logout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_info_activity_logout:
                LibCookieManager.clearCookie();
                goToMainPage(MainPage.PAGE_SHOPLIST_INDEX, false);
                break;
        }
    }
}
