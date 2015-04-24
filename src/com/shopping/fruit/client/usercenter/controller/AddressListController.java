package com.shopping.fruit.client.usercenter.controller;

import android.content.Context;
import android.os.Bundle;

import com.shopping.fruit.client.order.page.AccountPage;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-04-23 20:59
 */
public class AddressListController {

    public static final int FROM_ACCOUT_PAGE = 1; // 来自结算页面

    private String mFrom;

    private Context mContext;

    public AddressListController(Context context) {
        this.mContext = context;
    }

    public void initData(Bundle argument) {
        if (argument != null) {
            mFrom = argument.getString("from");
        }
    }

    public boolean isFromAccoutPage() {
        return AccountPage.class.getName().equals(mFrom);
    }

}
