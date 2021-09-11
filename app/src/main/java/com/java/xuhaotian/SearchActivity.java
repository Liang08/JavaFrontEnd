package com.java.xuhaotian;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.Touch;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.java.xuhaotian.adapter.LinkAdapter;
import com.java.xuhaotian.adapter.SearchHistoryAdapter;
import com.java.xuhaotian.mainpage.MainPageActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {

    private Button mBtnCancel;
    private EditText mEtSearch;
    private TextView mTvDelete;
    private ListView mLvHistory;
    private String error_message;
    private SearchHistoryAdapter mAdapter;
    private ArrayList<Pair<String, String>> history = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void initView(){
        mBtnCancel = findViewById(R.id.btn_cancel);
        mEtSearch = findViewById(R.id.et_search);
        mTvDelete = findViewById(R.id.tv_deleteAll);
        mLvHistory = findViewById(R.id.lv_history);
        initList();
    }

    private void initEvent(){
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Search", "---cancel search---");
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        mTvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("token", Consts.getToken());
                    HttpRequest.MyResponse response = new HttpRequest().putRequest(Consts.backendURL + "clearSearchHistory", json);
                    if (response.code() == 200) {
                        Log.d("test", "success");
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
                Log.d("test", "token:" + Consts.getToken());
                initList();
            }
        });

        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    Bundle bundle = new Bundle();
                    String keyword = mEtSearch.getText().toString().trim();
                    bundle.putString("searchKey", keyword);
                    bundle.putString("course", Consts.getSubjectNow());

                    Log.d("Search", "---search: " + keyword + "---");
                    Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 0);
                    return true;
                }
                return false;
            }
        });
        mEtSearch.setFocusable(true);
        mEtSearch.setFocusableInTouchMode(true);
        mEtSearch.requestFocus();

        mLvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String historyCourse = Consts.getSubjectChineseName(history.get(position).second);
                String historyName = history.get(position).first;
                Bundle bundle = new Bundle();
                bundle.putString("searchKey", historyName);
                bundle.putString("course", historyCourse);

                Log.d("Search", "---search: " + historyName + "---");
                Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });
    }

    void initList(){
        history.clear();
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("token", Consts.getToken());
            HttpRequest.MyResponse response = new HttpRequest().getRequest(Consts.backendURL + "getSearchHistory", params);
            if (response.code() == 200) {
                JSONArray jsonArray = new JSONArray(response.string());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String course = obj.keys().next();
                    String name = obj.getString(course);
                    history.add(Pair.create(name, course));
                    Log.d("test", String.valueOf(obj));
                }

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
            error_message = "请求失败";
            e.printStackTrace();
        }
        Log.d("test", "token:" + Consts.getToken());
        if (error_message != null){
            Toast.makeText(SearchActivity.this, "获取历史搜索记录失败", Toast.LENGTH_SHORT).show();
        }
        mAdapter = new SearchHistoryAdapter(SearchActivity.this, history);
        mLvHistory.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initList();
    }
}