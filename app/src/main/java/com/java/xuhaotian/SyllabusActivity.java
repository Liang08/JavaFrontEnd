package com.java.xuhaotian;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class SyllabusActivity extends AppCompatActivity {
    private static final String TAG = "SyllabusActivity";

    private TextView mTvName;
    private Button mBtnReturn;
    private ListView mLvSubjectList, mLvValueList;

    private String name, course;

    private Call syllabusCall = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);

        Intent intent = getIntent();
        course = intent.getStringExtra("course");
        name = intent.getStringExtra("name");

        initViews();
        initEvents();
        initData();
    }

    @Override
    protected void onDestroy() {
        if (syllabusCall != null) syllabusCall.cancel();
        super.onDestroy();
    }

    private void initViews() {
        mBtnReturn = findViewById(R.id.btn_syllabus_return);
        mTvName = findViewById(R.id.tv_syllabus_name);
        mTvName.setText("加载中，请稍候");
        mLvSubjectList = findViewById(R.id.lv_syllabus_subject_list);
        mLvValueList = findViewById(R.id.lv_syllabus_value_list);
    }

    private void initEvents() {
        mBtnReturn.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
    }

    private void initData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("course", course);
        params.put("subjectName", name);
        params.put("token", Consts.getToken());
        syllabusCall = new HttpRequest().getRequestCall(Consts.backendURL + "getRelatedSubject", params);
        syllabusCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    mTvName.setText("加载失败");
                    Toast.makeText(SyllabusActivity.this, "请求异常", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(Objects.requireNonNull(response.body()).string());
                        new Handler(Looper.getMainLooper()).post(() -> {
                            mTvName.setText(name);
                            initSyllabus(jsonArray);
                        });
                    } catch (NullPointerException | JSONException e) {
                        Log.d(TAG, Log.getStackTraceString(e));
                        new Handler(Looper.getMainLooper()).post(() -> {
                            mTvName.setText("加载失败");
                            Toast.makeText(SyllabusActivity.this, "请求异常", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
                else {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        mTvName.setText("加载失败");
                        Toast.makeText(SyllabusActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void initSyllabus(@NonNull JSONArray jsonArray) {
        List<String> subjectList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();
        List<String> subjectPredicateList = new ArrayList<>();
        List<String> valuePredicateList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject obj = jsonArray.getJSONObject(i);
                String predicate = obj.getString("predicate");
                if (obj.has("subject")) {
                    subjectList.add(obj.getString("subject"));
                    subjectPredicateList.add(predicate);
                }
                else if (obj.has("value")) {
                    valueList.add(obj.getString("value"));
                    valuePredicateList.add(predicate);
                }
            } catch (JSONException e) {
                Log.d(TAG, Log.getStackTraceString(e));
            }
        }

        SyllabusAdapter subjectAdapter = new SyllabusAdapter(subjectPredicateList, SyllabusActivity.this);
        mLvSubjectList.setAdapter(subjectAdapter);
        mLvSubjectList.setItemsCanFocus(true);
        mLvSubjectList.setOnItemClickListener((parent, view, position, id) -> {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(SyllabusActivity.this);
            final View layout = getLayoutInflater().inflate(R.layout.syllabus_dialog, null);
            final TextView tv_content = (TextView) layout.findViewById(R.id.tv_syllabus_dialog_content);
            dialog.setTitle("... " + subjectPredicateList.get(position) + " " + name);
            tv_content.setText(Html.fromHtml(subjectList.get(position), Html.FROM_HTML_MODE_LEGACY));
            dialog.setView(layout);
            dialog.show();
        });


        SyllabusAdapter valueAdapter = new SyllabusAdapter(valuePredicateList, SyllabusActivity.this);
        mLvValueList.setAdapter(valueAdapter);
        mLvValueList.setItemsCanFocus(true);
        mLvValueList.setOnItemClickListener((parent, view, position, id) -> {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(SyllabusActivity.this);
            final View layout = getLayoutInflater().inflate(R.layout.syllabus_dialog, null);
            final TextView tv_content = (TextView) layout.findViewById(R.id.tv_syllabus_dialog_content);
            dialog.setTitle(name + " " + valuePredicateList.get(position) + " ...");
            tv_content.setText(Html.fromHtml(valueList.get(position), Html.FROM_HTML_MODE_LEGACY));
            dialog.setView(layout);
            dialog.show();
        });
    }
}