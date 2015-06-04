package com.shopping.fruit.client.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.shopping.fruit.client.base.pagestack.BasePage;
import com.shopping.fruit.client.base.pagestack.TaskManagerFactory;
import com.shopping.fruit.client.home.page.MainPage;
import com.shopping.fruit.client.usercenter.LoginActivity;
import com.shopping.fruit.client.widget.DialogUtil;

/**
 * Created by keshuangjie on 2015/3/16.
 */
public class CommonPage extends BasePage{

    protected void showProgress() {
        DialogUtil.show(getActivity(), "", "");
    }

    protected void dismissProgress(){
        DialogUtil.dismiss();
    }

    protected ActionBar getActionBar(){
        ActionBarActivity activity = (ActionBarActivity) getActivity();
        if (activity != null && !activity.isFinishing()) {
            return activity.getSupportActionBar();
        }

        return null;
    }

    /**
     * ActionBar返回上一个页面是否显示
     * @param enabled
     */
    protected void setDisplayHomeAsUpEnabled(boolean enabled) {
        getActionBar().setDisplayHomeAsUpEnabled(enabled);
    }

    /**
     * 设置actionBar title
     * @param title
     */
    protected void setTitle(String title) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null){
            actionBar.setTitle(title);
        }
    }


    /**
     * 添加子fragment
     * @param resId
     * @param childPage
     * @param argument
     */
    protected void addWidgetFrament(int resId, Fragment childPage, Bundle argument) {
        if (childPage == null){
            throw new NullPointerException("childPage is null");
        }
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        childPage.setArguments(argument);
        ft.replace(resId, childPage).commit();
    }

    protected void navigateTo(String pageName, Bundle argument) {
        TaskManagerFactory.getTaskManager().navigateTo(getActivity(), pageName, argument);
    }

    protected void goToLoginPage() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    protected void goToMainPage(int index, boolean refresh) {
        Bundle bundle = new Bundle();
        bundle.putInt(MainPage.KEY_INDEX, index);
        bundle.putBoolean("refresh", refresh);
        navigateTo(MainPage.class.getCanonicalName(), bundle);
    }

}
