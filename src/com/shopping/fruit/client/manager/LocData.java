package com.shopping.fruit.client.manager;

import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.shopping.fruit.client.util.Log;

/**
 * 定位数据类
 *
 * @author keshuangjie
 * @date 2015-05-23 11:06
 */
public class LocData {

    // 定位类型 "lbs"为网络定位 "gps"为GPS定位
    public String mProvider;

    public double mGeoLat;
    public double mGeoLng;

    public double mSpeed;
    public float mBearing;

    public String mProvince;
    public String mCity;
    public String mCityCode;
    public String mDistrict;
    public String mDistrictCode;
    public String mStreet;
    public String mDetailAddr;
    public String mExtras;

    public static LocData parse(AMapLocation location) {
        LocData item = new LocData();
        item.mProvider = location.getProvider();
        item.mGeoLat = location.getLatitude();
        item.mGeoLng = location.getLongitude();
        if (item.isGPSProvider()) {
            item.mSpeed = location.getSpeed();
            item.mBearing = location.getBearing();
        } else {
            item.mProvince = location.getProvince();
            item.mCity = location.getCity();
            item.mCityCode = location.getCityCode();
            item.mDistrict = location.getDistrict();
            item.mDistrictCode = location.getAdCode();
            item.mStreet = location.getStreet();
            item.mDetailAddr = location.getAddress();
            Bundle locBundle = location.getExtras();
            if (locBundle != null) {
                item.mExtras = locBundle.getString("desc");
            }
        }
        Log.i("kshj", "LocData -> parse() -> lat:" + item.mGeoLat + " lng:" + item.mGeoLng);
        return item;
    }

    public boolean isGPSProvider() {
        return "gps".equals(mProvider);
    }

}
