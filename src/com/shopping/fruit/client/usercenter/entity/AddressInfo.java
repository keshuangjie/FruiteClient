package com.shopping.fruit.client.usercenter.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-04-12 12:58
 */
public class AddressInfo implements Parcelable {

    public int id;
    public String name;
    public String phone;
    public String address;
    public String detail;

    public AddressInfo() {

    }

    public static ArrayList<AddressInfo> parseAdressList(JSONObject json){
        ArrayList<AddressInfo> list = new ArrayList<AddressInfo>();
        if (json != null && !TextUtils.isEmpty(json.toString())) {
            JSONObject data = json.optJSONObject("data");
            if (data != null && !TextUtils.isEmpty(data.toString())) {
                JSONArray array = data.optJSONArray("addressList");
                if(array != null && array.length() > 0){
                    for (int i=0; i<array.length(); i++){
                        JSONObject jsonItem = array.optJSONObject(i);
                        AddressInfo item = new AddressInfo();
                        item.id = jsonItem.optInt("id");
                        item.name = jsonItem.optString("name");
                        item.phone = jsonItem.optString("telephone");
                        item.address = jsonItem.optString("address");
                        item.detail = jsonItem.optString("detail");
                        list.add(item);
                    }
                }
            }
        }

        return list;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(detail);
    }

    // 带参构造器方法私用化，本构造器仅供类的方法createFromParcel调用
    private AddressInfo(Parcel source) {
        id = source.readInt();
        name = source.readString();
        phone = source.readString();
        address = source.readString();
        detail = source.readString();
    }

    // 必须要创建一个名叫CREATOR的常量。
    public static final Parcelable.Creator<AddressInfo> CREATOR = new Parcelable.Creator<AddressInfo>() {
        @Override
        public AddressInfo createFromParcel(Parcel source) {
            return new AddressInfo(source);
        }
        //重写createFromParcel方法，创建并返回一个获得了数据的user对象
        @Override
        public AddressInfo[] newArray(int size) {
            return new AddressInfo[size];
        }
    };
}
