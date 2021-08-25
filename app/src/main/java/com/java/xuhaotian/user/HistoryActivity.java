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

public class HistoryActivity extends AppCompatActivity {
    private Button mBtnClear, mBtnReturn;
    private final List<Pair<String, String>> history = new ArrayList<>();
    private String error_message;
    private HistoryAdapter mAdapter;
    private View footView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initViews();
        initEvents();
    }

    private void getHistory() {
        history.clear();
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("token", Consts.getToken());
            HttpRequest.MyResponse response = new HttpRequest().getRequest(Consts.backendURL + "getInstanceHistory", params);
            if (response.code() == 200) {
                JSONArray jsonArray = new JSONArray(response.string());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String course = obj.keys().next();
                    String name = obj.getString(course);
                    history.add(Pair.create(course, name));
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
        mBtnClear = findViewById(R.id.btn_history_clear);
        mBtnClear.setEnabled(false);
        mBtnReturn = findViewById(R.id.btn_history_return);
        ListView mLvList = findViewById(R.id.lv_history_list);
        footView = LayoutInflater.from(this).inflate(R.layout.history_list_null_item, null, false);
        getHistory();
        if (error_message == null) {
            mLvList.addFooterView(footView);
            if (history.size() == 0) {
                footView.setVisibility(View.VISIBLE);
            }
            else {
                footView.setVisibility(View.INVISIBLE);
            }
            mAdapter = new HistoryAdapter(history, HistoryActivity.this);
            mAdapter.setDetailListener(v -> {
                int position = Integer.parseInt(v.getTag().toString());
                Intent intent = new Intent(HistoryActivity.this, EntityDetailActivity.class);
                intent.putExtra("course", history.get(position).first);
                intent.putExtra("name", history.get(position).second);
                startActivityForResult(intent, 1);
            });

            mLvList.setAdapter(mAdapter);
            mBtnClear.setEnabled(true);
        }
        else {
            Toast.makeText(HistoryActivity.this, "历史记录获取失败：" + error_message, Toast.LENGTH_SHORT).show();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            mBtnClear.setEnabled(false);
            getHistory();
            if (error_message != null) {
                Toast.makeText(HistoryActivity.this, "历史记录获取失败：" + error_message, Toast.LENGTH_SHORT).show();
            }
            else {
                mBtnClear.setEnabled(true);
            }
            if (history.size() == 0) {
                footView.setVisibility(View.VISIBLE);
            }
            else {
                footView.setVisibility(View.INVISIBLE);
            }
            mAdapter.notifyDataSetChanged();
        }
    }
}