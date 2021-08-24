package com.java.xuhaotian.user;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.java.xuhaotian.Consts;
import com.java.xuhaotian.HttpRequest;
import com.java.xuhaotian.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {
    private Button mBtnReturn;
    private final List<Pair<String, String>> favourite = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        initViews();
        initEvents();
    }

    public void initViews() {
        mBtnReturn = findViewById(R.id.btn_favourite_return);
        ListView mLvList = findViewById(R.id.lv_favourite_list);

        String error_message;
        favourite.clear();

        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("token", Consts.getToken());
            HttpRequest.MyResponse response = new HttpRequest().getRequest(Consts.backendURL + "getFavourite", params);
            if (response.code() == 200) {
                JSONArray jsonArray = new JSONArray(response.string());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String course = obj.keys().next();
                    String name = obj.getString(course);
                    favourite.add(Pair.create(course, name));
                }
                error_message = null;
            }
            else if (response.code() == 401) {
                JSONObject obj = new JSONObject(response.string());
                error_message = obj.getString("message") + "";
            }
            else {
                error_message = "请求失败(" + response.code() + ")";
            }
        } catch (Exception e) {
            error_message = "请求异常";
            e.printStackTrace();
        }


        if (error_message == null) {
            View footView = LayoutInflater.from(this).inflate(R.layout.favourite_list_null_item, null, false);
            mLvList.addFooterView(footView);
            if (favourite.size() == 0) {
                footView.setVisibility(View.VISIBLE);
            }
            else {
                footView.setVisibility(View.INVISIBLE);
            }
            FavouriteAdapter mAdapter = new FavouriteAdapter(favourite, FavouriteActivity.this);
            mAdapter.setDetailListener(
                    v -> Toast.makeText(FavouriteActivity.this, "查看细节：Pos=" + v.getTag(), Toast.LENGTH_SHORT).show());
            mAdapter.setRemoveListenerListener(
                    v -> {
                        int position = Integer.parseInt(v.getTag().toString());
                        Pair<String, String> target = favourite.get(position);

                        JSONObject params = new JSONObject();
                        try {
                            params.put("course", target.first);
                            params.put("name", target.second);
                            params.put("token", Consts.getToken());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        HttpRequest.MyResponse response = new HttpRequest().putRequest(Consts.backendURL + "resetFavourite", params);
                        if (response.code() == 200) {
                            favourite.remove(position);
                            if (favourite.size() == 0) {
                                footView.setVisibility(View.VISIBLE);
                            }
                            else {
                                footView.setVisibility(View.INVISIBLE);
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        else {
                            Toast.makeText(FavouriteActivity.this, "取消收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    });
            mLvList.setAdapter(mAdapter);
        }
        else {
            Toast.makeText(FavouriteActivity.this, "收藏夹获取失败：" + error_message, Toast.LENGTH_SHORT).show();
        }

    }

    public void initEvents() {
        mBtnReturn.setOnClickListener(v -> finish());
    }
}
