package com.shopping.fruit.client.entity;

import android.text.TextUtils;

import com.shopping.fruit.client.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 商品实体类
 *
 * @author keshuangjie
 * @date 2015-03-25 20:00
 */
public class Product {

    public int id; // 商品id
    public String name; // 商品名称
    public String img; // 商品图片地址
    public double oriPrice; // 原始价格
    public double price; // 现价
    public int categoryId; // 种类
    public int totalSold; // 已出售数量
    public String description;

    public int selectedCount; //选择的数量

    public Product(){
    }

    public static ArrayList<Product> parse(JSONObject json){
        ArrayList<Product> list = new ArrayList<Product>();
        if (json != null && !TextUtils.isEmpty(json.toString())) {
            JSONObject data = json.optJSONObject("data");
            if (data != null && !TextUtils.isEmpty(data.toString())) {
                JSONArray array = data.optJSONArray("skus");
                if(array != null && array.length() > 0){
                    for (int i=0; i<array.length(); i++){
                        JSONObject jsonItem = array.optJSONObject(i);
                        Product item = new Product();
                        item.id = jsonItem.optInt("skuId");
                        item.name = jsonItem.optString("skuName");
                        item.oriPrice = jsonItem.optDouble("oriPrice");
                        item.price = jsonItem.optDouble("salePrice");
                        item.categoryId = jsonItem.optInt("categoryId");
                        item.description = jsonItem.optString("description");
                        item.img = jsonItem.optString("img");
                        item.totalSold = jsonItem.optInt("soldTotally");
                        list.add(item);
                    }
                }
            }
        }

        return list;
    }

    public static ArrayList<Product> parseAccount(JSONObject json){
        ArrayList<Product> list = new ArrayList<Product>();
        if (json != null && !TextUtils.isEmpty(json.toString())) {
//            JSONObject data = json.optJSONObject("data");
//            if (data != null && !TextUtils.isEmpty(data.toString())) {
                JSONArray array = json.optJSONArray("skuIdList");
                if(array != null && array.length() > 0){
                    for (int i=0; i<array.length(); i++){
                        JSONObject jsonItem = array.optJSONObject(i);
                        Product item = new Product();
                        item.id = jsonItem.optInt("id");
                        item.name = jsonItem.optString("name");
                        item.price = jsonItem.optDouble("unitPrice");
                        item.selectedCount = jsonItem.optInt("number");
                        list.add(item);
                    }
                }
//            }
        }

        return list;
    }
}
