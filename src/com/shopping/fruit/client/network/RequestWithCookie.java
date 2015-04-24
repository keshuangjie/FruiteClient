package com.shopping.fruit.client.network;

import android.text.TextUtils;
import android.webkit.CookieManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.shopping.fruit.client.util.Log;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-04-12 14:58
 */
public class RequestWithCookie extends JsonObjectRequest{

    public RequestWithCookie(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        Response<JSONObject> superResponse = super.parseNetworkResponse(response);
        Map<String, String> responseHeaders = response.headers;
        if (responseHeaders != null){
            String rawCookies = responseHeaders.get("Set-Cookie");
            if (!TextUtils.isEmpty(rawCookies)){
                LibCookieManager.setCookieValue(rawCookies);
            }
            Log.i("kshj", "RequestWithCookie -> parseNetWorkResponse -> rawCookies: " + rawCookies);
        }
        return superResponse;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        String cookie = LibCookieManager.getCookies();
        Log.i("kshj", "RequestWithCookie -> getHeaders() -> rawCookies: " + cookie);
        if (!TextUtils.isEmpty(cookie)) {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("cookie", cookie);
            return headers;
        }
        return super.getHeaders();
    }

}
