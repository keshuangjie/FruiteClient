package com.shopping.fruit.client.shop.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.base.AbsAdapter;
import com.shopping.fruit.client.entity.Product;
import com.shopping.fruit.client.event.ShoppintCartEvent;
import com.shopping.fruit.client.util.Log;
import com.shopping.fruit.client.widget.PlusView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * 商品列表适配器
 *
 * @author keshuangjie
 * @date 2015-03-25 20:08
 */
public class ProductAdapter extends AbsAdapter<Product> implements PlusView.OnChangeListener{

    private ArrayList<Product> mSelectedProducets;

    public ProductAdapter(Context context){
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.page_product_list_item, parent, false);
        }

        Product item = mContent.get(position);
        if (item != null) {
            TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
            TextView tv_description = ViewHolder.get(convertView, R.id.tv_description);
            TextView tv_price = ViewHolder.get(convertView, R.id.tv_price);
            TextView tv_totalSold = ViewHolder.get(convertView, R.id.tv_totalSold);
            ImageView im_head = ViewHolder.get(convertView, R.id.im_head);
            tv_name.setText(item.name);
            tv_description.setText(item.description);
            tv_price.setText(item.price + "$/斤");
            tv_totalSold.setText("已售出" + item.totalSold + "份");
            Picasso.with(mContext).load(item.img).into(im_head);

            PlusView plusView = ViewHolder.get(convertView, R.id.plusView);
            plusView.setTag(item);
            plusView.setOnChangeListener(this);
            plusView.updateView(item.selectedCount);
        }

        return convertView;
    }

    @Override
    public void setContents(ArrayList<Product> contents) {
        if (contents == null) {
            contents = new ArrayList<Product>();
        }
        mContent = contents;
        updateSelectedProduct();
        notifyDataSetChanged();
    }

    @Override
    public void onChange(View view, int count) {
        Product item = (Product) view.getTag();
        if(item == null){
            return;
        }
        item.selectedCount = count;
        changeSelectedProducts(item);
    }

    public void updateSelectedProduct() {
        if (mSelectedProducets == null || mSelectedProducets.size() <=0){
            return;
        }
        if (mContent == null || mContent.size() <= 0) {
            return;
        }
        Log.i("kshj", "ProductAdapter -> updateSelectedProducts() ");

        for (Product p1 : mContent) {
            for (Product p2 : mSelectedProducets) {
                if (p1.id == p2.id) {
                    p1.selectedCount = p2.selectedCount;
                }
            }
        }
    }

    public void setSelectedProducts(ArrayList<Product> products) {
        this.mSelectedProducets = products;
    }

    public ArrayList<Product> getSelectedProducts() {
        return mSelectedProducets;
    }

    private void changeSelectedProducts(Product item){
        if(mSelectedProducets == null){
            mSelectedProducets = new ArrayList<Product>();
        }
        boolean isContain = false;
        for (Product p : mSelectedProducets){
            if (p.id == item.id){
                isContain = true;
                p.selectedCount = item.selectedCount;
                if(p.selectedCount <= 0){
                    mSelectedProducets.remove(p);
                }
                break;
            }
        }
        if (!isContain){
            mSelectedProducets.add(item);
        }

        EventBus.getDefault().post(new ShoppintCartEvent(mSelectedProducets));
    }


}
