package com.shopping.fruit.client.entity;

import java.util.ArrayList;

/**
 * 购物车实体类
 *
 * @author keshuangjie
 * @date 2015-04-01 19:46
 */
public class ShoppingCartEntity {

    public int shopId;

    public int count;   // 商品数量

    public double totalPrice;   //商品总价

    public ArrayList<Product> products; //商品详细

    public void saveToJsonFile(){

    }

    public void parseFromJsonFile(){

    }

}
