package com.shopping.fruit.client.entity;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-03-19 15:29
 */
public class ResultEntity {

    /** 网络请求返回状态码 */
    private static final int CODE_SUCCESS = 0; // 网络请求成功
    private static final int CODE_ERROR = -1; //网络请求失败
    private static final int CODE_NOT_LOGIN = 1; // 未登录

    public int code = -111;

    public ArrayList data;

    public ResultEntity(JSONObject json){
        if(json != null){
            this.code = json.optInt("code");
        }
    }

    public boolean isSuccess(){
        return this.code == CODE_SUCCESS;
    }

    public boolean isNotLogin(){
        return this.code == CODE_NOT_LOGIN;
    }

}
