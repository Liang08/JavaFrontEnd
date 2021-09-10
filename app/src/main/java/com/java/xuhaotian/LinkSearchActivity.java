package com.java.xuhaotian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.java.xuhaotian.adapter.LinkAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LinkSearchActivity extends AppCompatActivity {
    private Button mBtnReturn;
    private ListView mLvResult;
    private LinkAdapter mAdapter;
    ArrayList<HashMap<String, String>> searchResult = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_search);
        mLvResult = findViewById(R.id.lv_link_search);
        mBtnReturn = findViewById(R.id.btn_link_search_return);
        mBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        Intent intent = getIntent();
        String context = intent.getStringExtra("context");
        try {
            JSONObject json = new JSONObject();
            json.put("token", Consts.getToken());
            json.put("context", context);
            HttpRequest.MyResponse response = new HttpRequest().postRequest(Consts.backendURL + "linkInstance", json);
            if (response.code() == 200) {
                JSONArray jsonArray = new JSONArray(response.string());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String entityType = obj.getString("entity_type");
                    String name = obj.getString("entity");
                    String course = obj.getString("entity_course");
                    Log.d("test", course + entityType + ": " + name);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", name);
                    map.put("course", course);
                    map.put("entityType", entityType);
                    searchResult.add(map);
                }
            }
        } catch (Exception e) {
            Log.d("test", "fail");
            e.printStackTrace();
        }
        mAdapter = new LinkAdapter(LinkSearchActivity.this, searchResult);
        mLvResult.setAdapter(mAdapter);
        mLvResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LinkSearchActivity.this, EntityDetailActivity.class);
                intent.putExtra("course", searchResult.get(position).get("course"));
                intent.putExtra("name", searchResult.get(position).get("name"));
                startActivityForResult(intent, 1);
            }
        });
    }

}