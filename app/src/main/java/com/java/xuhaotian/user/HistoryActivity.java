package com.java.xuhaotian.user;

import static com.java.xuhaotian.Consts.JSON;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.java.xuhaotian.Consts;
import com.java.xuhaotian.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HistoryActivity extends AppCompatActivity {
    private Button mBtnReturn;
    private ListView mLvList;
    private String error_message = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initViews();
        initEvents();
    }

    public void initViews() {
        mBtnReturn = findViewById(R.id.btn_history_return);
        mLvList = findViewById(R.id.lv_history_list);

        List<String> history = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                error_message = null;
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(Consts.backendURL + "getInstanceHistory?token=" + Consts.getToken())
                            .get()
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.code() == 200) {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        for (int i = 0; i < jsonArray.length(); i++)
                            history.add(jsonArray.getString(i));
                    }
                    else if (response.code() == 401) {
                        JSONObject obj = new JSONObject(response.body().string());
                        error_message = obj.getString("message") + "";
                    }
                    else {
                        error_message = "请求失败(" + response.code() + ")";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (error_message == null) {
            if (history.size() == 0) {
                history.add("当前没有浏览历史记录");
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this,android.R.layout.simple_expandable_list_item_1, history);
            mLvList.setAdapter(adapter);
        }
        else {
            Toast.makeText(HistoryActivity.this, "历史记录获取失败：" + error_message, Toast.LENGTH_SHORT).show();
        }

    }

    public void initEvents() {
        mBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}