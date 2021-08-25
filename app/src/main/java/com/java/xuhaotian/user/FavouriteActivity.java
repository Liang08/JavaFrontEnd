package com.java.xuhaotian.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.java.xuhaotian.Consts;
import com.java.xuhaotian.EntityDetailActivity;
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
    private String error_message;
    private FavouriteAdapter mAdapter;
    private View footView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        initViews();
        initEvents();
    }

    private void getFavourite() {
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
    }

    private void initViews() {
        mBtnReturn = findViewById(R.id.btn_favourite_return);
        ListView mLvList = findViewById(R.id.lv_favourite_list);

        getFavourite();

        if (error_message == null) {
            footView = LayoutInflater.from(this).inflate(R.layout.favourite_list_null_item, null, false);
            mLvList.addFooterView(footView);
            if (favourite.size() == 0) {
                footView.setVisibility(View.VISIBLE);
            }
            else {
                footView.setVisibility(View.INVISIBLE);
            }
            mAdapter = new FavouriteAdapter(favourite, FavouriteActivity.this);
            mAdapter.setDetailListener(v -> {
                int position = Integer.parseInt(v.getTag().toString());
                Intent intent = new Intent(FavouriteActivity.this, EntityDetailActivity.class);
                intent.putExtra("course", favourite.get(position).first);
                intent.putExtra("name", favourite.get(position).second);
                startActivityForResult(intent, 1);
            });

            mAdapter.setRemoveListenerListener(v -> {
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

    private void initEvents() {
        mBtnReturn.setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            getFavourite();
            if (error_message != null) {
                Toast.makeText(FavouriteActivity.this, "收藏夹获取失败：" + error_message, Toast.LENGTH_SHORT).show();
            }
            if (favourite.size() == 0) {
                footView.setVisibility(View.VISIBLE);
            }
            else {
                footView.setVisibility(View.INVISIBLE);
            }
            mAdapter.notifyDataSetChanged();
        }
    }
}
