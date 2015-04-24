package com.shopping.fruit.client.network;


import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.shopping.fruit.client.common.CommonApi;
import com.shopping.fruit.client.util.Log;
import com.sinaapp.whutec.util.Initializer;
import com.sinaapp.whutec.util.common.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Cookie Manager will synchronize the cookies with the application
 *
 * @author junfengli
 */
public final class LibCookieManager {
  // private static YrCookieManager sInstance;
  private static CookieManager sCookieManager;
  private static CookieSyncManager sCookieSyncManager;
  private static final String YR_COOKIE_VOXAUTH = "voxauth";
  private static final String YR_COOKIE_VA_SESS = "va_sess";
  private static final String YR_COOKIE_UID = "uid";
  private static final String[] REQUIRED_COOKIES = { YR_COOKIE_VOXAUTH, YR_COOKIE_VA_SESS,
      YR_COOKIE_UID };
  private static final String COOKIE_SEPARATOR = ";";
  public static final String KEYWORD = "Set-Cookie";
  private static String COOKIE_DOMAIN_URL = getCurrentDomain();

  /**
   * HACK(junfengli): When api < 14, webview will set cookie domain like yunyun.com. However, on the
   * other side, webview will set cookie domain like .yunyun.com. We should always check this while
   * a new android api version have been released.
   *
   * @return
   */
  public static String getCurrentDomain() {
    if (android.os.Build.VERSION.SDK_INT > 13) {
      return CommonApi.HOST;
    }
    return CommonApi.HOST;
  }

  public static void init(Context applicationContext) {
    CookieSyncManager.createInstance(applicationContext);
    sCookieManager = CookieManager.getInstance();
    sCookieSyncManager = CookieSyncManager.getInstance();
  }

  /**
   * 根据传入的url，得到该url下的cookie，然后提取出该cookie的各个字段名
   *
   * @param cookie
   *          cookie样式如下："cookieName1=cookieValue1; cookieName2=cookieValue2; ..."
   * @return cookie的各字段名的vector
   */
  private static Vector<String> getCookieNamesByUrl(String cookie) {
    if (TextUtils.isEmpty(cookie)) {
      return null;
    }
    String[] cookieField = cookie.split(";");
    int len = cookieField.length;
    for (int i = 0; i < len; i++) {
      cookieField[i] = cookieField[i].trim();
    }
    Vector<String> allCookieField = new Vector<String>();
    for (int i = 0; i < len; i++) {
      if (TextUtils.isEmpty(cookieField[i])) {
        continue;
      }
      if (!cookieField[i].contains("=")) {
        continue;
      }
      String[] singleCookieField = cookieField[i].split("=");
      allCookieField.add(singleCookieField[0]);
    }
    if (allCookieField.isEmpty()) {
      return null;
    }
    return allCookieField;
  }

  /**
   * 清除指定url下的cookie。
   *
   * @param url
   */
  private static void clearCookieByUrlInternal(String url) {
    if (TextUtils.isEmpty(url)) {
      return;
    }
    String cookieString = getYrCookieManager().getCookie(url);
    Vector<String> cookie = getCookieNamesByUrl(cookieString);
    if (cookie == null || cookie.isEmpty()) {
      Log.v("clearCookieByUrlInternal", "cookie is null");
      return;
    }
    int len = cookie.size();
    for (int i = 0; i < len; i++) {
      getYrCookieManager().setCookie(url, cookie.get(i) + "=-1");
    }
    sCookieSyncManager.sync();
  }

  /**
   * 清除url下的cookie，主要针对android2.0和4.0版本中网易微博单独设置一种host会使得设置cookie无效
   * 系统的CookieManager会通过参数url计算出相应的cookie的Domain, 2.X下，系统认为类似weibo.com是有效Domain，而
   * 在4.X系统下，会认为.weibo.com为有效域名 在android2.x cookie的域名样式如：http://hostname,
   * 在android4.x下cookie的域名样式如：http://.hostname HACK(shituzheng):
   * 根据一个给定的URL产生一个新的URL，清除这两个URL下的cookie
   *
   * @param url
   */
  public static void clearCookieByUrl(String url) {
    Uri uri = Uri.parse(url);
    String host = uri.getHost();
    clearCookieByUrlInternal(url);
    clearCookieByUrlInternal("http://." + host);
  }

  /**
   * synchronize cookie between RAM and SDcard.
   *
   * @param
   */
  public static void synchCookies() {
    sCookieSyncManager.sync();
  }

  /**
   * @return all cookies stored locally.
   */
  public static String getCookies() {
    String cookie = getYrCookieManager().getCookie(COOKIE_DOMAIN_URL);
    return cookie;
  }

  public static String getCookiesByUrl(String url) {
    String cookie = getYrCookieManager().getCookie(url);
    return cookie;
  }


  /**
   * Check id Yr cookie is exist.
   */
  public static boolean isCookieExist() {
    String cookies = getYrCookieManager().getCookie(COOKIE_DOMAIN_URL);
    if (cookies == null) {
      return false;
    }
    return true;
  }

  /**
   * Clear all cookies stored locally.
   */
  public static void clearCookie() {
    getYrCookieManager().removeAllCookie();
  }

  public static String getCookieVauleFromCookieStringByKey(String key, String cookieStr) {
    if (TextUtils.isEmpty(key) || TextUtils.isEmpty(cookieStr)) {
      return null;
    }

    String result = null;
    // Get value of key from cookie.
    if (cookieStr.contains(key)) {
      int ssidIndex = cookieStr.indexOf(key);
      int equalIndex = ssidIndex += key.length();
      int semicolonIndex = cookieStr.indexOf(COOKIE_SEPARATOR, equalIndex + 1);
      if (semicolonIndex != -1) {
        result = cookieStr.substring(equalIndex + 1, semicolonIndex);
      } else {
        result = cookieStr.substring(equalIndex + 1);
      }
    }
    Log.i("getCookieValueByKey() cookie = ", cookieStr);
    Log.i("getCookieValueByKey() " + key + " = ", result);
    return result;
  }

  public static String getCookieValueByKey(String key) {
    if (!isCookieExist()) {
      return null;
    }
    String cookies = getCookies();
    String result = null;
    // Get value of key from cookie.
    if (cookies.contains(key)) {
      int ssidIndex = cookies.indexOf(key);
      int equalIndex = ssidIndex += key.length();
      int semicolonIndex = cookies.indexOf(COOKIE_SEPARATOR, equalIndex + 1);
      if (semicolonIndex != -1) {
        result = cookies.substring(equalIndex + 1, semicolonIndex);
      } else {
        result = cookies.substring(equalIndex + 1);
      }
    }
    Log.i("getCookieValueByKey() cookie = ", cookies);
    Log.i("getCookieValueByKey() " + key + " = ", result);
    return result;
  }


  /**
   * 通过key设置cookie
   *
   * @return void
   */
  public static void setCookieValueByKey(String key, String value) {
    getYrCookieManager().setCookie(COOKIE_DOMAIN_URL, key + "=" + value);
    Log.i("setCookieValueByKey() " + key + " = ", value);
    synchCookies();
  }

  /**
   * 直接设置cookie
   *
   * @return void
   */
  public static void setCookieValue(String value) {
    getYrCookieManager().setCookie(COOKIE_DOMAIN_URL, value);
    Log.i("setCookieValue() cookie = ", value);
    synchCookies();
  }

  /**
   * Set a cookie to a given url.
   *
   * @param url
   * @param value
   */
  public static void setCookieValueToUrl(String url, String value) {
    getYrCookieManager().setCookie(url, value);
    Log.i("setCookieValue() cookie = ", value);
    synchCookies();
  }

  /**
   * Get cookie list from a given string.
   *
   * @param cookieStr
   * @return Map, if the string is null, will return a empty Map
   */
  public static Map<String, String> getCookieList(String cookieStr) {
    Map<String, String> cookieMap = new HashMap<String, String>();
    if (!TextUtils.isEmpty(cookieStr)) {
      String[] cookieList = cookieStr.split(";");
      if (!CommonUtil.isArrayEmpty(cookieList)) {
        for (int i = 0; i < cookieList.length; i++) {
          String[] tempArr = cookieList[i].split("=");
          if (!CommonUtil.isArrayEmpty(tempArr)) {
            if (tempArr.length == 2) {
              if (!TextUtils.isEmpty(tempArr[0]) && !TextUtils.isEmpty(tempArr[1])) {
                cookieMap.put(tempArr[0].trim(), tempArr[1].trim());
              }
            }
          }
        }
      }
    }
    return cookieMap;
  }

  /**
   * 根据关键字以及header信息来解析cookie，并把cookie的各个字段的关键字以及内容保存在map中.
   *
   * @param keyword
   *          标识json中cookie字段的关键字
   * @param headerJson
   *          待解析的json内容
   */
  public static void getCookieFromHeader(String keyword, String headerJson,
      Map<String, String> userInfoMap) {
    if (userInfoMap == null || TextUtils.isEmpty(keyword) || TextUtils.isEmpty(headerJson)) {
      return;
    }
    userInfoMap.clear();
    try {
      JSONArray jsonArray = new JSONArray(headerJson);
      if (headerJson != null) {
        for (int i = 0; i < jsonArray.length(); i++) {
          JSONArray cookieJSONArray = jsonArray.getJSONArray(i);
          // Judge cookie host.
          if (cookieJSONArray.getString(0).equals(keyword)) {
            String cookieString = cookieJSONArray.getString(1);
            // TODO(shituzheng): 判断domain=yunyun.com的逻辑会在后期enable
            // String findKeyString = "domain=";
            // if (cookieString.contains(findKeyString)) {
            // int start = cookieString.indexOf(findKeyString) + new
            // String(findKeyString).length();
            // int end = cookieString.indexOf(";", cookieString.indexOf(findKeyString));
            // String domain = cookieString
            // .substring(start, end == -1 ? cookieString.length() : end);
            // if (Config.YR_HOST.contains(domain)) {
            int point = cookieString.indexOf(";");
            String cookieSection = "";
            if (point == -1) {
              cookieSection = cookieString;
            } else {
              cookieSection = cookieString.substring(0, point);
            }
            if (!TextUtils.isEmpty(cookieSection) && !TextUtils.isEmpty(cookieSection.trim())) {
              String[] temp = cookieSection.trim().split("=");
              userInfoMap.put(temp[0], temp[1]);
            }
            // }
            // }
          }
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    } catch (NullPointerException e) {
      e.printStackTrace();
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
    }
  }

  /**
   * Set cookie to cookie manager from cookie map
   *
   * @param userInfoMap
   */
  public static void setUserInfoCookie(Map<String, String> userInfoMap) {
    if (userInfoMap == null) {
      return;
    }
    for (String key : userInfoMap.keySet()) {
      // TODO(shituzheng): We should only sync just once.
      setCookieValue(key + "=" + userInfoMap.get(key));
    }
  }

  /**
   * parse header data's cookie section and set it
   *
   * @param header
   *          rawData's header data
   */
  public static void processHttpHeader(String header, String url) {
    if (!TextUtils.isEmpty(url)) {
      Uri uri = Uri.parse(url);
      if (uri != null) {
        COOKIE_DOMAIN_URL = uri.getScheme() + "://" + uri.getAuthority();
      }
    }
    String oldCookie = LibCookieManager.getCookies();
    Map<String, String> oldCookieMap = null;
    if (!TextUtils.isEmpty(oldCookie)) {
      oldCookieMap = getCookieList(oldCookie);
    }
    Map<String, String> userInfoMap = new HashMap<String, String>();
    getCookieFromHeader(KEYWORD, header, userInfoMap);
    setUserInfoCookie(userInfoMap);
//    String yrudString = userInfoMap.get(YR_COOKIE_UD);
//    if (!TextUtils.isEmpty(yrudString)) {
//      if (oldCookieMap != null && yrudString.equals(oldCookieMap.get(YR_COOKIE_UD))) {
//        return;
//      }
//      YrLog.d("yunyun cookie", "new cookie: " + YrCookieManager.getCookies());
//      YrLog.d("UserInfoModel", "user changed !");
//      // 更新用户登录状态信息
//      // TODO(junfengli): Cache cookie.
//    }
  }

  private static CookieManager getYrCookieManager() {
    if (sCookieManager == null) {
      LibCookieManager.init(Initializer.getContext());
    }
    if (sCookieManager == null) {
    }
    return sCookieManager;
  }
}