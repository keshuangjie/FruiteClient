package com.shopping.fruit.client.usercenter.entity;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-04-12 12:58
 */
public class AddressInfo {

    public int id;
    public String name;
    public String phone;
    public String address;

    public static ArrayList<AddressInfo> parseAdressList(JSONObject json){
            ArrayList<AddressInfo> list = new ArrayList<AddressInfo>();
            if (json != null && !TextUtils.isEmpty(json.toString())) {
                JSONObject data = json.optJSONObject("data");
                if (data != null && !TextUtils.isEmpty(data.toString())) {
                    JSONArray array = data.optJSONArray("addressList");
                    if(array != null && array.length() > 0){
                        for (int i=0; i<array.length(); i++){
                            JSONObject jsonItem = array.optJSONObject(i);
                            AddressInfo item = new AddressInfo();
                            item.id = jsonItem.optInt("id");
                            item.name = jsonItem.optString("name");
                            item.phone = jsonItem.optString("telephone");
                            item.address = jsonItem.optString("address");
                            list.add(item);
                        }
                    }
                }
            }

            return list;
        }
}
