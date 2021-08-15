package com.java.xuhaotian;

import okhttp3.MediaType;

public class Consts {
    public static final String backendURL = "http://183.172.49.18:8080/";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static String token;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Consts.token = token;
    }
}
