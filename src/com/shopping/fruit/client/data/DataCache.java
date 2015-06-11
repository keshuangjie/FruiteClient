package com.shopping.fruit.client.data;

import android.util.SparseArray;

import com.shopping.fruit.client.entity.Product;

import java.util.ArrayList;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-06-06 15:02
 */
public class DataCache {

    private SparseArray<ArrayList<Product>> mProductCache;

    private static final class Holder {
        public static final DataCache sInstance = new DataCache();
    }

    private DataCache() {
        mProductCache = new SparseArray();
    }

    public static DataCache getInstance() {
        return Holder.sInstance;
    }

    public void addProductCache(int key, ArrayList<Product> value) {
        if (mProductCache == null) {
            mProductCache = new SparseArray();
        }
        if (value == null || value.size() == 0) {
            mProductCache.remove(key);
        } else {
            mProductCache.put(key, value);
        }
    }

    public ArrayList<Product> getProductCache(int key) {
        if (mProductCache == null || mProductCache.size() == 0) {
            return null;
        }
        return mProductCache.get(key);
    }
}
