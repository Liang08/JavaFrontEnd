package com.java.xuhaotian;

import okhttp3.MediaType;

public class Consts {
    public static final String backendURL = "http://183.172.12.80:8080/";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static String token;
    private static String userName;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Consts.token = token;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        Consts.userName = userName;
    }

}
