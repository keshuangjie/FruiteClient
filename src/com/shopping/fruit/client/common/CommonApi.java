package com.shopping.fruit.client.common;

/**
 * TODO
 *
 * @author keshuangjie
 * @date 2015-03-25 20:30
 */
public class CommonApi {

    public static final String HOST = "http://123.57.134.241";

    public static final String NEARBY_SALER_LIST = HOST + "/getSalersNearby"; // 获取附近商户信息
    public static final String SALER_DETAIL = HOST + "/getSalerDetail"; // 获取商户详情信息
    public static final String GO_TO_ACCOUNT = HOST + "/goToAccountPage"; // 结算页
    public static final String ADD_USER_ADDRESS = HOST + "/addUserAddress"; // 添加用户地址
    public static final String UPDATE_USER_ADDRESS = HOST + "/updateUserAddress";// 更新用户地址
    public static final String DELETE_ADDRESS = HOST + "/delUserAddress"; // 删除地址
    public static final String SEND_VERIFYCODE = HOST + "/sendVerifyCode"; // 发送验证码
    public static final String LOGIN = HOST + "/verifyTelephone"; // 登录
    public static final String GET_USER_ADRESS_LIST = HOST + "/getUserAddressList"; // 获取用户地址列表
    public static final String GET_USER_INFO = HOST + "/getOwnDetail"; // 获取用户信息
    public static final String SUBMIT_ORDER = HOST + "/submitOrder"; // 下单
    public static final String GET_USER_ORDERS = HOST + "/getUserOrders"; // 获取订单

}
