package com.shopping.fruit.client.entity;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-03-19 13:43
 */
public class Shop {

    public int shopId;
    public String name;
    public String descrption;


    public static ArrayList<Shop> parse(JSONObject json){
        ArrayList<Shop> list = new ArrayList<Shop>();
        if (json != null && !TextUtils.isEmpty(json.toString())) {
            JSONObject data = json.optJSONObject("data");
            if (data != null && !TextUtils.isEmpty(data.toString())) {
                JSONArray array = data.optJSONArray("salers");
                if(array != null && array.length() > 0){
                    for (int i=0; i<array.length(); i++){
                        JSONObject jsonItem = array.optJSONObject(i);
                        Shop item = new Shop();
                        item.name = jsonItem.optString("name");
                        list.add(item);
                    }
                }
            }
        }

        return list;
    }

}
