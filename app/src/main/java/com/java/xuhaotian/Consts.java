package com.java.xuhaotian;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;

public class Consts {
    public static final String backendURL = "http://183.172.49.18:8080/";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static String token;
    private static ArrayList<String> subject = new ArrayList<>(Arrays.asList("语文", "数学", "英语", "物理", "化学", "生物"));
    private static String subjectNow = "语文";


    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Consts.token = token;
    }

    public static void setSubjectList(ArrayList<String> subjectList){
        subject = subjectList;
    }

    public static ArrayList<String> getSubjectList(){
        return subject;
    }

    public static String getSubjectNow() {
        return subjectNow;
    }

    public static void setSubjectNow(String subjectNow) {
        Consts.subjectNow = subjectNow;
    }
}
