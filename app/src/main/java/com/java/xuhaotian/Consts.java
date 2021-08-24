package com.java.xuhaotian;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import okhttp3.MediaType;

public class Consts {
    public static final String backendURL = "http://183.172.79.84:8080/";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static String token;
    private static String userName;
    private static ArrayList<String> subject = new ArrayList<>(Arrays.asList("语文", "数学", "英语", "物理", "化学", "生物"));
    private static String subjectNow = "语文";

    private static HashMap<String, Integer> subjectIconResId = new HashMap<String, Integer>() {
        {
            put("chinese", R.drawable.chinese);
            put("math", R.drawable.math);
            put("english", R.drawable.english);
            put("physics", R.drawable.physics);
            put("chemistry", R.drawable.chemistry);
            put("biology", R.drawable.biology);
            put("history", R.drawable.history);
            put("politics", R.drawable.politics);
            put("geo", R.drawable.geography);
        }
    };

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

    public static Integer getSubjectIconResId(String subject) { return Consts.subjectIconResId.get(subject); }
}
