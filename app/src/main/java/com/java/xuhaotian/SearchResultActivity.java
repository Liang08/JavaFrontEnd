package com.java.xuhaotian;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.java.xuhaotian.adapter.EntityListAdapter;
import com.java.xuhaotian.adapter.SearchListAdapter;

import org.jetbrains.annotations.Contract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private ImageButton mIbReturn;
    private TextView mTvTitle;
    private String error_message;
    private String searchKey;
    private SearchListAdapter mAdapter;
    private ListView mLvResult;
    private String course;
    ArrayList<HashMap<String, String>> searchResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Intent intent = getIntent();
        searchKey = intent.getStringExtra("searchKey");
        course = intent.getStringExtra("course");

        getData();
        initView(searchKey);
        initEvent();
    }

    void initView(String keyword) {
        mLvResult = findViewById(R.id.lv_result);
        mIbReturn = findViewById(R.id.ib_return);
        mTvTitle = findViewById(R.id.tv_title);
        mTvTitle.setText("在 " + course + " 中搜索 \'" + keyword + "\' 结果");
        initList();
    }

    void initList(){
        mAdapter = new SearchListAdapter(SearchResultActivity.this, searchResult);
        mLvResult.setAdapter(mAdapter);
    }

    void initEvent() {
        mIbReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        mLvResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchResultActivity.this, EntityDetailActivity.class);
                intent.putExtra("course", course);
                intent.putExtra("name", searchResult.get(position).get("name"));
                HashMap<String, String> map = searchResult.get(position);
                map.put("visited", "yes");
                searchResult.set(position, map);
                startActivityForResult(intent, 2);
            }
        });
    }

    @NonNull
    @Contract(pure = true)
    private String getFileName() {
        return course + "_xht_" + searchKey + ".instance";
    }

    private void saveInstance(JSONArray property){
        File file = new File(getCacheDir(), getFileName());
        ObjectOutputStream oos = null;
        try {
            if (!file.exists() && !file.createNewFile()){
                return;
            }
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(property.toString());
            oos.close();
        }catch (IOException e){
            Log.d("test", Log.getStackTraceString(e));
        }finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    Log.d("test", Log.getStackTraceString(e));
                }
            }
        }
    }

    private boolean readInstance(JSONArray[] property){
        File file = new File(getCacheDir(), getFileName());
        ObjectInputStream ois = null;
        boolean ok = true;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            property[0] = new JSONArray((String) ois.readObject());
            ois.close();
        } catch (Throwable e) {
            ok = false;
        }finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    Log.d("test", Log.getStackTraceString(e));
                }
            }
        }
        Log.d("test", "write cache");
        return ok;
    }

    void getData(){
        searchResult = new ArrayList<>();
        error_message = null;
        JSONArray[] property = new JSONArray[1];
        if (readInstance(property)){
            for (int i = 0; i < property[0].length(); i++) {
                try {
                    JSONObject obj = property[0].getJSONObject(i);
                    String entityType = obj.getString("category");
                    String name = obj.getString("label");
                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", name);
                    map.put("visited", "no");
                    map.put("entityType", entityType);
                    searchResult.add(map);
                    Log.d("test", String.valueOf(obj));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.d("test", "read cache");
        }
        else {
            try {
                HashMap<String, Object> params = new HashMap<>();
                params.put("token", Consts.getToken());
                params.put("course", Consts.getSubjectName(course));
                params.put("searchKey", searchKey);
                params.put("limit", 2147483647);
                HttpRequest.MyResponse response = new HttpRequest().getRequest(Consts.backendURL + "getInstanceList", params);
                if (response.code() == 200) {
                    JSONArray jsonArray = new JSONArray(response.string());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String entityType = obj.getString("category");
                        String name = obj.getString("label");
                        HashMap<String, String> map = new HashMap<>();
                        map.put("name", name);
                        map.put("visited", "no");
                        map.put("entityType", entityType);
                        searchResult.add(map);
                        Log.d("test", String.valueOf(obj));
                    }
                    saveInstance(jsonArray);
                } else if (response.code() == 401) {
                    Log.d("test", "401");
                    JSONObject obj = new JSONObject(response.string());
                    error_message = obj.getString("message") + "";
                } else {
                    Log.d("test", String.valueOf(response.code()));
                    error_message = "请求失败(" + response.code() + ")";
                }
            } catch (Exception e) {
                Log.d("test", "fail");
                e.printStackTrace();
            }
        }
        Log.d("test", "token:" + Consts.getToken());
    }

    int compare(HashMap<String, String> hashMap1, HashMap<String, String> hashMap2){
        if (hashMap1.get("name").compareTo(hashMap2.get("name")) > 0){
            return 1;
        }else{
            return -1;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initList();
    }
}