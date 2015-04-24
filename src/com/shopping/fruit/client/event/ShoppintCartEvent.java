package com.shopping.fruit.client.event;

import com.shopping.fruit.client.entity.Product;

import java.util.ArrayList;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-04-01 20:55
 */
public class ShoppintCartEvent {

    public ArrayList<Product> products;

    public ShoppintCartEvent(ArrayList<Product> list){
        this.products = list;
    }

}
