package com.shopping.fruit.client.manager;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.shopping.fruit.client.MyApplication;
import com.shopping.fruit.client.common.Config;
import com.shopping.fruit.client.util.Log;
import com.sinaapp.whutec.util.common.SharedPreUtil;

import de.greenrobot.event.EventBus;

/**
 * 定位管理类
 *
 * @author keshuangjie
 * @date 2015-05-23 10:42
 */
public class LocationManager implements AMapLocationListener {

    private LocationManagerProxy mLocationManagerProxy;

    private Context mContext;

    private LocData mLocData;

    private static final class Holder {
        public static final LocationManager sInstance = new LocationManager();
    }

    private LocationManager(){
    }

    public static LocationManager getInstance() {
        return Holder.sInstance;
    }

    public void start(Context context) {
        this.mContext = context;
        mLocationManagerProxy = LocationManagerProxy.getInstance(mContext);

        //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
        //在定位结束后，在合适的生命周期调用destroy()方法
        //其中如果间隔时间为-1，则定位只定一次
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, 60*1000, 15, this);

        mLocationManagerProxy.setGpsEnable(false);
    }

    public LocData getLocData() {
        return mLocData;
    }

    public void setLocData(LocData data) {
        this.mLocData = data;
    }

    public void saveLocData() {
        if (mLocData != null) {
            SharedPreUtil preUtil = new SharedPreUtil(Config.FILE_LOCATION);
            preUtil.putFloat("longitude", (float) mLocData.mGeoLng);
            preUtil.putFloat("latitude", (float) mLocData.mGeoLat);
            preUtil.putString("address", mLocData.mDetailAddr);
            preUtil.getEditor().commit();
        }
    }

    public LocData getLocDataCache() {
        SharedPreUtil preUtil = new SharedPreUtil(Config.FILE_LOCATION);
        float lng = preUtil.getFloat("longitude", -1);
        float lat = preUtil.getFloat("lattitude", -1);
        String mDetailAdress = preUtil.getString("address");
        LocData data = new LocData();
        data.mGeoLat = lat;
        data.mGeoLng = lng;
        data.mDetailAddr = mDetailAdress;
        return data;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
            //获取位置信息
            mLocData = LocData.parse(aMapLocation);
            saveLocData();
        } else {
            Log.e("kshj", "LocationManager -> " + aMapLocation.getAMapException().toString());
            aMapLocation.getAMapException().printStackTrace();
            mLocData = getLocDataCache();
        }
        stopLocation();
        EventBus.getDefault().post(new LocationEvent());
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void stopLocation() {
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.removeUpdates(this);
            mLocationManagerProxy.destory();
        }
        mLocationManagerProxy = null;
    }
}
