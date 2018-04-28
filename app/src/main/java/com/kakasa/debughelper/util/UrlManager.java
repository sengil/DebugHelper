package com.kakasa.debughelper.util;

/**
 * Created by Seng on 2018/4/26.
 */

public class UrlManager {
    public static final String BASE_URL_DEBUG = "http://47.104.102.195/";
    public static final String BASE_URL_RELEASE = "https://www.btc98.vip/";
    private static String BASE_URL = BASE_URL_RELEASE;

    public static String getBaseUrl(){
        return BASE_URL;
    }
    public static void setBaseUrl(String url) {
        BASE_URL = url;
    }
}
