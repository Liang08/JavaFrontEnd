package com.java.xuhaotian.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.java.xuhaotian.Consts;
import com.java.xuhaotian.EntityDetailActivity;
import com.java.xuhaotian.HttpRequest;
import com.java.xuhaotian.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FavouriteActivity extends AppCompatActivity {
    private static final String TAG = "FavouriteActivity";

    private Button mBtnReturn;
    private ListView mLvList;
    private View footView;

    private final List<Pair<String, String>> favourite = new ArrayList<>();
    private FavouriteAdapter mAdapter = null;

    private Call favouriteCall = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        initViews();
        initEvents();
        initData();
    }

    @Override
    protected void onDestroy() {
        if (favouriteCall != null) favouriteCall.cancel();
        super.onDestroy();
    }

    private void initViews() {
        mBtnReturn = findViewById(R.id.btn_favourite_return);
        mLvList = findViewById(R.id.lv_favourite_list);
        footView = LayoutInflater.from(this).inflate(R.layout.favourite_list_null_item, null, false);
        mLvList.addFooterView(footView);
        footView.setVisibility(View.INVISIBLE);
    }

    private void initEvents() {
        mBtnReturn.setOnClickListener(v -> finish());
    }

    private void initData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", Consts.getToken());
        favouriteCall = new HttpRequest().getRequestCall(Consts.backendURL + "getFavourite", params);
        favouriteCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(FavouriteActivity.this, "请求异常", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(Objects.requireNonNull(response.body()).string());
                        new Handler(Looper.getMainLooper()).post(() -> initFavourite(jsonArray));
                    } catch (NullPointerException | JSONException e) {
                        Log.d(TAG, Log.getStackTraceString(e));
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(FavouriteActivity.this, "请求异常", Toast.LENGTH_SHORT).show());
                    }
                }
                else {
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(FavouriteActivity.this, "请求失败", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void initFavourite(@NonNull JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject obj = jsonArray.getJSONObject(i);
                String course = obj.keys().next();
                String name = obj.getString(course);
                favourite.add(Pair.create(course, name));
            } catch (JSONException e) {
                Log.d(TAG, Log.getStackTraceString(e));
            }
        }

        if (favourite.size() == 0) {
            footView.setVisibility(View.VISIBLE);
        }
        else {
            footView.setVisibility(View.INVISIBLE);
        }

        if (mAdapter == null) {
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
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            footView.setVisibility(View.INVISIBLE);
            favourite.clear();
            mAdapter.notifyDataSetChanged();
            initData();
        }
    }
}
