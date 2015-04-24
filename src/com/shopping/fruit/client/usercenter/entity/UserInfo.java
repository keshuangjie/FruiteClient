package com.shopping.fruit.client.usercenter.entity;

import android.text.TextUtils;

import org.json.JSONObject;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-04-20 20:39
 */
public class UserInfo {

    public String name;
    public String phone;

    public UserInfo(){
    }

    public static UserInfo parseUserInfo(JSONObject json) {
        if (json != null && !TextUtils.isEmpty(json.toString())) {
            UserInfo user = new UserInfo();
            user.name = json.optString("nickName");
            user.phone = json.optString("telephone");
            return user;
        }

        return null;
    }

}
