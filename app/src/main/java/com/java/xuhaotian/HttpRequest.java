package com.java.xuhaotian;

import static com.java.xuhaotian.Consts.JSON;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequest {
    private final static OkHttpClient myClient = new OkHttpClient.Builder().
            callTimeout(5 * 1000, TimeUnit.MILLISECONDS).
            build();

    @NonNull
    private String getUrl(@NonNull Map<String, Object> params) {
        final StringBuffer str = new StringBuffer("?");
        params.forEach((K, V) -> str.append(K).append("=").append(V.toString()).append("&"));
        return str.toString();
    }

    public static class MyResponse {
        private final int code;
        private final String string;

        public MyResponse(int code, String string) {
            this.code = code;
            this.string = string;
        }

        public int code() {
            return code;
        }

        public String string() {
            return string;
        }
    }

    public MyResponse getRequest(String url, Map<String, Object> params) {
        final StringBuffer str = new StringBuffer();
        final Integer[] code = new Integer[1];
        Thread thread = new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(2, TimeUnit.SECONDS)
                        .build();
                Request request = new Request.Builder()
                        .url(url + getUrl(params))
                        .get()
                        .build();
                Response response = myClient.newCall(request).execute();
                try {
                    code[0] = response.code();
                    str.append(Objects.requireNonNull(response.body()).string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new MyResponse(code[0], str.toString());
    }

    public MyResponse putRequest(String url, JSONObject params) {
        final StringBuffer str = new StringBuffer();
        final Integer[] code = new Integer[1];
        Thread thread = new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(2, TimeUnit.SECONDS)
                        .build();
                RequestBody body = RequestBody.create(String.valueOf(params), JSON);
                Request request = new Request.Builder()
                        .url(url)
                        .put(body)
                        .build();
                Response response = myClient.newCall(request).execute();
                try {
                    code[0] = response.code();
                    str.append(Objects.requireNonNull(response.body()).string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new MyResponse(code[0], str.toString());
    }

    public MyResponse postRequest(String url, JSONObject params) {
        final StringBuffer str = new StringBuffer();
        final Integer[] code = new Integer[1];
        Thread thread = new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(2, TimeUnit.SECONDS)
                        .build();
                RequestBody body = RequestBody.create(String.valueOf(params), JSON);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = myClient.newCall(request).execute();
                try {
                    code[0] = response.code();
                    str.append(Objects.requireNonNull(response.body()).string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new MyResponse(code[0], str.toString());
    }

    public Call getRequestCall(String url, Map<String, Object> params) {
        Request request = new Request.Builder()
                .url(url + getUrl(params))
                .get()
                .build();
        return myClient.newCall(request);
    }

    public Call putRequestCall(String url, JSONObject params) {
        RequestBody body = RequestBody.create(String.valueOf(params), JSON);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        return myClient.newCall(request);
    }

    public Call postRequestCall(String url, JSONObject params) {
        RequestBody body = RequestBody.create(String.valueOf(params), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return myClient.newCall(request);
    }
}
