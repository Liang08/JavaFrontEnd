package com.java.xuhaotian;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.Contract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityDetailActivity extends AppCompatActivity {
    private static final String TAG = "EntityDetailActivity";

    private Button mBtnReturn;
    private Switch mSwitchFavourite;
    private String course, name;
    private String error_message;
    private JSONArray property = new JSONArray();
    private JSONArray content = new JSONArray();
    private JSONArray question = new JSONArray();
    private Boolean isFavourite = null;
    private final List<Question> questionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_detail);

        Intent intent = getIntent();
        course = intent.getStringExtra("course");
        name = intent.getStringExtra("name");

        initViews();
        initEvents();
    }

    private void initViews() {
        mBtnReturn = findViewById(R.id.btn_entity_detail_return);
        TextView mTvName = findViewById(R.id.tv_entity_detail_name);
        mTvName.setText(name);
        mSwitchFavourite = findViewById(R.id.switch_entity_detail_favourite);
        mSwitchFavourite.setEnabled(false);
        TableLayout mTlProperty = findViewById(R.id.tl_entity_detail_property);
        TextView mTvRelation = findViewById(R.id.tv_entity_detail_relation);
        ListView mLvQuestionList = findViewById(R.id.lv_entity_detail_question_list);

        getInfo();

        if (error_message == null) {
            // init property
            for (int i = 0; i < property.length(); i++) {
                try {
                    JSONObject obj = property.getJSONObject(i);
                    String predicate = obj.getString("predicateLabel");
                    String object = obj.getString("object");

                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(5, 6, 5, 6);
                    TableRow tableRow = new TableRow(this);
                    TextView tv1 = new TextView(this);
                    tv1.setText(predicate);
                    tableRow.addView(tv1, layoutParams);
                    TextView tv2 = new TextView(this);
                    tv2.setText(Html.fromHtml(object, Html.FROM_HTML_MODE_LEGACY));
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).setMargins(3, 3, 3, 3);
                    tableRow.addView(tv2, layoutParams);
                    mTlProperty.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                } catch (JSONException e) {
                    Log.d(TAG, Log.getStackTraceString(e));
                }
            }
            // init content
            mTvRelation.setText(content.toString());
            // init question
            questionList.clear();
            for (int i = 0; i < question.length(); i++) {
                try {
                    JSONObject obj = question.getJSONObject(i);
                    questionList.add(new Question(
                            obj.getString("qBody"),
                            obj.getString("qAnswer"),
                            obj.getString("A"),
                            obj.getString("B"),
                            obj.getString("C"),
                            obj.getString("D"),
                            obj.getInt("id")
                    ));
                } catch (JSONException e) {
                    Log.d(TAG, Log.getStackTraceString(e));
                }
            }

            EntityDetailQuestionAdapter mAdapter = new EntityDetailQuestionAdapter(questionList, EntityDetailActivity.this);
            mLvQuestionList.setAdapter(mAdapter);
            if (isFavourite != null) {
                mSwitchFavourite.setChecked(isFavourite);
                mSwitchFavourite.setEnabled(true);
            }
        }
        else {
            Toast.makeText(EntityDetailActivity.this, error_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void initEvents() {
        mSwitchFavourite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isEnabled()) {
                return;
            }
            buttonView.setEnabled(false);
            JSONObject params = new JSONObject();
            try {
                params.put("course", course);
                params.put("name", name);
                params.put("token", Consts.getToken());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            String url;
            if (isChecked) url = Consts.backendURL + "setFavourite";
            else url = Consts.backendURL + "resetFavourite";
            HttpRequest.MyResponse response = new HttpRequest().putRequest(url, params);
            if (response.code() != 200) {
                Toast.makeText(EntityDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                buttonView.setChecked(!isChecked);
            }
            buttonView.setEnabled(true);
        });
        mBtnReturn.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            mSwitchFavourite.setEnabled(false);
            getIsFavourite();
            if (isFavourite != null) {
                mSwitchFavourite.setChecked(isFavourite);
                mSwitchFavourite.setEnabled(true);
            }
        }
    }

    @NonNull
    @Contract(pure = true)
    private String getFileName() {
        return course + "_xht_" + name + ".instance";
    }

    private void saveInstance() {
        File file = new File(getCacheDir(), getFileName());
        ObjectOutputStream oos = null;
        try {
            if (!file.exists() && !file.createNewFile()) {
                return;
            }
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(property.toString());
            oos.writeObject(content.toString());
            oos.writeObject(question.toString());
            oos.close();
        } catch (IOException e) {
            Log.d(TAG, Log.getStackTraceString(e));
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    Log.d(TAG, Log.getStackTraceString(e));
                }
            }
        }
    }

    private boolean readInstance() {
        File file = new File(getCacheDir(), getFileName());
        ObjectInputStream ois = null;
        boolean ok = true;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            property = new JSONArray((String)ois.readObject());
            content = new JSONArray((String)ois.readObject());
            question = new JSONArray((String)ois.readObject());
            ois.close();
        } catch (Throwable e) {
            ok = false;
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    Log.d(TAG, Log.getStackTraceString(e));
                }
            }
        }
        return ok;
    }

    private void getInfo() {
        if (readInstance()) {
            getIsFavourite();
            error_message = null;
        }
        else {
            try {
                Map<String, Object> params1 = new HashMap<>();
                params1.put("course", course);
                params1.put("name", name);
                params1.put("token", Consts.getToken());
                HttpRequest.MyResponse response1 = new HttpRequest().getRequest(Consts.backendURL + "getInfoByInstanceName", params1);
                Map<String, Object> params2 = new HashMap<>();
                params2.put("uriName", name);
                params2.put("token", Consts.getToken());
                HttpRequest.MyResponse response2 = new HttpRequest().getRequest(Consts.backendURL + "getQuestionListByUriName", params2);

                if (response1.code() == 200 && response2.code() == 200) {
                    JSONObject jsonObject = new JSONObject(response1.string());
                    property = jsonObject.getJSONArray("property");
                    content = jsonObject.getJSONArray("content");
                    question = new JSONArray(response2.string());
                    isFavourite = jsonObject.getBoolean("isFavourite");
                    error_message = null;
                    saveInstance();
                }
                else {
                    error_message = "请求失败(" + response1.code() + "||" + response2.code() + ")";
                }
            } catch (JSONException e) {
                Log.d(TAG, Log.getStackTraceString(e));
                error_message = "请求异常";
            }
        }

    }

    private void getIsFavourite() {
        isFavourite = null;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("course", course);
            params.put("name", name);
            params.put("token", Consts.getToken());
            HttpRequest.MyResponse response = new HttpRequest().getRequest(Consts.backendURL + "isFavourite", params);
            if (response.code() == 200) {
                isFavourite = Boolean.parseBoolean(response.string());
            }
        } catch (Throwable e) {
            Log.d(TAG, Log.getStackTraceString(e));
        }
    }

}