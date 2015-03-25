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

    public int shopId; // 店铺id
    public String name; // 店主名字
    public String description; // 描述
    public int distance; // 距离
    public String headImg; // 店铺logo
    public String category; // 果蔬类型

    public double longitude; // 经度
    public double latitude; // 纬度

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
                        item.shopId = jsonItem.optInt("salerId");
                        item.description = jsonItem.optString("description");
                        item.distance = jsonItem.optInt("distance");
                        item.headImg = jsonItem.optString("headImage");
                        item.category = jsonItem.optString("salerType");
                        item.longitude = jsonItem.optDouble("longitude");
                        item.latitude = jsonItem.optDouble("latitude");
                        list.add(item);
                    }
                }
            }
        }

        return list;
    }

}
