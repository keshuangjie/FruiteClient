package com.shopping.fruit.client.order.entity;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-04-26 15:21
 */
public class Order {

    public int orderId;
    public String skuInfo;
    public String salerName;
    public int salerId;
    public double priceTotal;
    /**
    (u'TBCT','toBeCounted'),#0，待配送
            (u'TBCF','toBeConfirmed'),	#3，待确认
            (u'TBCM','toBeCommented'),#4，待评价
            (u'FND','finished'),	#5，已完成
     */
    public int status;
    public String dateTime;

    public static ArrayList<Order> parse(JSONObject json){
        ArrayList<Order> list = new ArrayList<Order>();
        if (json != null && !TextUtils.isEmpty(json.toString())) {
            JSONObject data = json.optJSONObject("data");
            if (data != null && !TextUtils.isEmpty(data.toString())) {
                JSONArray array = data.optJSONArray("orders");
                if(array != null && array.length() > 0){
                    for (int i=0; i<array.length(); i++){
                        JSONObject jsonItem = array.optJSONObject(i);
                        Order item = new Order();
                        item.orderId = jsonItem.optInt("id");
                        item.priceTotal = jsonItem.optDouble("priceTotal");
                        item.salerName = jsonItem.optString("salerName");
                        item.salerId = jsonItem.optInt("salerId");
                        item.skuInfo = jsonItem.optString("skuInfo");
                        item.dateTime = jsonItem.optString("dateTime");
                        list.add(item);
                    }
                }
            }
        }

        return list;
    }

    public String getStatus() {
        String statuName = "未知";
        switch (status) {
            case 0:
                statuName = "待配送";
                break;
            case 3:
                statuName = "待确定";
                break;
            case 4:
                statuName = "待评价";
                break;
            case 5:
                statuName = "已完成";
                break;

        }

        return statuName;
    }
}
