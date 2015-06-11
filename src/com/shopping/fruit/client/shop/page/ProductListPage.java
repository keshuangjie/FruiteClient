package com.shopping.fruit.client.shop.page;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.AbsAdapter;
import com.shopping.fruit.client.base.MyListPage;
import com.shopping.fruit.client.common.CommonApi;
import com.shopping.fruit.client.data.DataCache;
import com.shopping.fruit.client.entity.Product;
import com.shopping.fruit.client.shop.adapter.ProductAdapter;
import com.shopping.fruit.client.util.Log;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 商家详情页
 *
 * @author keshuangjie
 * @date 2015-03-25 19:59
 */
public class ProductListPage extends MyListPage<Product> {

    private int salerId;
    private String shopName, shopDescription, shopLogo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null){
            salerId = bundle.getInt("salerId");
            shopName = bundle.getString("name");
            shopDescription = bundle.getString("description");
            shopLogo = bundle.getString("headImg");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            ProductAdapter adapter = (ProductAdapter) mAdapter;
            ArrayList<Product> listCache = DataCache.getInstance().getProductCache(salerId);
            Log.i("kshj", "ProductListPage -> onResume() -> listCache size :" + (listCache == null ? null : listCache.size()));
            adapter.setSelectedProducts(listCache);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            ArrayList<Product> listCache = ((ProductAdapter) mAdapter).getSelectedProducts();
            Log.i("kshj", "ProductListPage -> onPause() -> listCache size :" + (listCache == null ? null : listCache.size()));
            DataCache.getInstance().addProductCache(salerId, listCache);
        }
    }

    @Override
    protected void addHeaderView() {
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.page_shopdetail_header, null);
        TextView tv_name = (TextView) headView.findViewById(R.id.tv_name);
        TextView tv_description = (TextView) headView.findViewById(R.id.tv_description);
        ImageView im_head = (ImageView) headView.findViewById(R.id.im_head);

        tv_name.setText(shopName);
        tv_description.setText(shopDescription + "\n我的蔬菜特别新鲜\n我的肉是本地猪肉");
        mListView.addHeaderView(headView, null, true);
    }

    @Override
    protected AbsAdapter<Product> initAdapter() {
        return new ProductAdapter(getActivity());
    }

    @Override
    protected String buildUrl() {
        return CommonApi.SALER_DETAIL + "?salerId=" + salerId;
    }

    @Override
    protected ArrayList<Product> parseData(JSONObject json, int type) {
        Log.i("kshj - ShopDetailPage - parseData: ", json.toString());
        if(json == null || TextUtils.isEmpty(json.toString())){
            return null;
        }
        return Product.parse(json);
    }
}
