package com.java.xuhaotian;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import okhttp3.MediaType;

public class Consts {

    public static final String backendURL = "http://183.172.72.129:8080/";

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static String token;
    private static String userName;
    private static ArrayList<String> subject = new ArrayList<>(Arrays.asList("语文", "数学", "英语", "物理", "化学", "生物"));
    private static final ArrayList<String> total = new ArrayList<>(Arrays.asList("语文", "数学", "英语", "物理", "化学", "生物", "政治", "地理", "历史"));
    private static String subjectNow = subject.get(0);

    private static final HashMap<String, Integer> subjectIconResId = new HashMap<String, Integer>() {
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

    @NonNull
    @Contract(pure = true)
    public static String getSubjectName(@NonNull String subject) {
        switch (subject) {
            case "语文":
                return "chinese";
            case "数学":
                return "math";
            case "英语":
                return "english";
            case "物理":
                return "physics";
            case "化学":
                return "chemistry";
            case "生物":
                return "biology";
            case "政治":
                return "politics";
            case "地理":
                return "geo";
            case "历史":
                return "history";
            default:
                return "";
        }
    }

    public static String getSubjectChineseName(String subject){
        switch (subject) {
            case "chinese":
                return "语文";
            case "math":
                return "数学";
            case "english":
                return "英语";
            case "physics":
                return "物理";
            case "chemistry":
                return "化学";
            case "biology":
                return "生物";
            case "politics":
                return "政治";
            case "geo":
                return "地理";
            case "history":
                return "历史";
            default:
                return "";
        }
    }

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

    public static void setSubjectList(ArrayList<String> subjectList) {
        subject = subjectList;
    }

    public static ArrayList<String> getSubjectList() {
        return subject;
    }

    public static String getSubjectNow() {
        return subjectNow;
    }

    public static void setSubjectNow(String subjectNow) {
        Consts.subjectNow = subjectNow;
    }

    public static ArrayList<String> getTotal() {
        return total;
    }

    public static Integer getSubjectIconResId(String subject) {
        return Consts.subjectIconResId.get(subject);
    }

}
