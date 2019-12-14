package com.example.adintegrate.network;

import okhttp3.MediaType;

/**
 * Created by dell on 2019/12/2 18:24
 * Description:
 * Emain: 1187278976@qq.com
 */
public class Constant {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /*设备随机等待一段时间(5分钟-15分钟)*/
    //最小时间（s）
    public static final int MIN = 300;
    //最小时间（s）
    public static final int MAX = 900;
    //毫秒
    public static final int MILLISECOND = 1000;

    //带参标识
    public static final String FLAG = "data";

    //默认UA
    public static final String DEFAULT_UA = "android";

    //imp_url
    public static final String URL_JUMP = "imp_url";
    //html/js
    public static final String HTML_JS = "html/js";
    //求情头部替换
    public static final String USER_AGENT = "User-Agent";
    public static final String REFERER = "Referer";

    //DIRECT:只替换浏览器直接发出的请求;
    public static final String DIRECT = "DIRECT";
    //INDIRECT:只替换引用的资源请求；
    public static final String INDIRECT = "INDIRECT";
    //ALL:替换包括DIRECT和INDIRECT覆盖的所有请求
    public static final String ALL = "ALL";

    public static final String ID = "id";
    //os=0的时候是imei，os=1的时候是idfa
    public static final String OS = "__OS__";
    public static final String IP = "__IP__";
    public static final String IMEI = "__IMEI__";
    public static final String IDFA = "__IDFA__";

    //基础URL
    public static final String BASE_URL = "http://172.16.0.188:9220/";
    public static final String TASK_URL =  BASE_URL+"task";
    public static final String REPORT_URL = BASE_URL+"report";



}
