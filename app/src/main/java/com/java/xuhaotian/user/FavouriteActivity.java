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

public class FavouriteActivity extends AppCompatActivity {
    private Button mBtnReturn;
    private ListView mLvList;
    private String error_message = null;
    List<String> favourite = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        initViews();
        initEvents();
    }

    public void initViews() {
        mBtnReturn = findViewById(R.id.btn_favourite_return);
        mLvList = findViewById(R.id.lv_favourite_list);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                error_message = null;
                favourite.clear();
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(Consts.backendURL + "getFavourite?token=" + Consts.getToken())
                            .get()
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.code() == 200) {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        for (int i = 0; i < jsonArray.length(); i++)
                            favourite.add(jsonArray.getString(i));
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
            if (favourite.size() == 0) {
                favourite.add("收藏夹空空如也");
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this,android.R.layout.simple_expandable_list_item_1, favourite);
            mLvList.setAdapter(adapter);
        }
        else {
            Toast.makeText(FavouriteActivity.this, "收藏夹获取失败：" + error_message, Toast.LENGTH_SHORT).show();
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
