package com.shopping.fruit.client.shop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.BaseFragment;

/**
 * Created by keshuangjie on 2015/3/29.
 */
public class ShoppingCartPage extends BaseFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_shoppingcart, container, false);
        return rootView;
    }

    public enum State{
        STATE_INIT, STATE_REMAIN, STATE_COMMIT;
    }

}
