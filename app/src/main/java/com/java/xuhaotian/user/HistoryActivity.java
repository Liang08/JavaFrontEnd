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

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "HistoryActivity";

    private Button mBtnClear, mBtnReturn;
    private ListView mLvList;
    private View footView;

    private final List<Pair<String, String>> history = new ArrayList<>();
    private HistoryAdapter mAdapter = null;

    private Call historyCall = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initViews();
        initEvents();
        initData();
    }

    @Override
    protected void onDestroy() {
        if (historyCall != null) historyCall.cancel();
        super.onDestroy();
    }

    private void initViews() {
        mBtnClear = findViewById(R.id.btn_history_clear);
        mBtnClear.setEnabled(false);
        mBtnReturn = findViewById(R.id.btn_history_return);
        mLvList = findViewById(R.id.lv_history_list);
        footView = LayoutInflater.from(this).inflate(R.layout.history_list_null_item, null, false);
        mLvList.addFooterView(footView);
        footView.setVisibility(View.INVISIBLE);
    }

    private void initEvents() {
        mBtnClear.setOnClickListener(v -> {
            JSONObject params = new JSONObject();
            try {
                params.put("token", Consts.getToken());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            HttpRequest.MyResponse response = new HttpRequest().putRequest(Consts.backendURL + "clearInstanceHistory", params);
            if (response.code() == 200) {
                history.clear();
                footView.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
            }
            else {
                Toast.makeText(HistoryActivity.this, "清除浏览历史失败", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnReturn.setOnClickListener(v -> finish());
    }

    private void initData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("token", Consts.getToken());
        historyCall = new HttpRequest().getRequestCall(Consts.backendURL + "getInstanceHistory", params);
        historyCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(HistoryActivity.this, "请求异常", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(Objects.requireNonNull(response.body()).string());
                        new Handler(Looper.getMainLooper()).post(() ->initHistory(jsonArray));
                    } catch (NullPointerException | JSONException e) {
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(HistoryActivity.this, "请求异常", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(HistoryActivity.this, "请求失败", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void initHistory(@NonNull JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject obj = jsonArray.getJSONObject(i);
                String course = obj.keys().next();
                String name = obj.getString(course);
                history.add(Pair.create(course, name));
            } catch (JSONException e) {
                Log.d(TAG, Log.getStackTraceString(e));
            }
        }

        if (history.size() == 0) {
            footView.setVisibility(View.VISIBLE);
        }
        else {
            footView.setVisibility(View.INVISIBLE);
        }

        if (mAdapter == null) {
            mAdapter = new HistoryAdapter(history, HistoryActivity.this);
            mAdapter.setDetailListener(v -> {
                int position = Integer.parseInt(v.getTag().toString());
                Intent intent = new Intent(HistoryActivity.this, EntityDetailActivity.class);
                intent.putExtra("course", history.get(position).first);
                intent.putExtra("name", history.get(position).second);
                startActivityForResult(intent, 1);
            });
            mLvList.setAdapter(mAdapter);
        }
        else {
            mAdapter.notifyDataSetChanged();
        }
        mBtnClear.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            mBtnClear.setEnabled(false);
            footView.setVisibility(View.INVISIBLE);
            history.clear();
            mAdapter.notifyDataSetChanged();
            initData();
        }
    }
}