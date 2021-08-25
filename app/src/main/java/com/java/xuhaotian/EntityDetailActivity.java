package com.java.xuhaotian;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.java.xuhaotian.Consts;
import com.java.xuhaotian.HttpRequest;
import com.java.xuhaotian.R;
import com.java.xuhaotian.user.HistoryActivity;
import com.java.xuhaotian.user.HistoryAdapter;

import org.jetbrains.annotations.Contract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class EntityDetailActivity extends AppCompatActivity {
    private Button mBtnReturn;
    private TextView mTvName, mTvRelation, mTvQuestion;
    private TableLayout mTlProperty;
    private Switch mSwitchFavourite;
    private String course, name;
    private String error_message;
    private JSONArray property = new JSONArray();
    private JSONArray content = new JSONArray();
    private JSONArray question = new JSONArray();
    private Boolean isFavourite = null;

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
        mTvName = findViewById(R.id.tv_entity_detail_name);
        mTvName.setText(name);
        mSwitchFavourite = findViewById(R.id.switch_entity_detail_favourite);
        mSwitchFavourite.setEnabled(false);
        mTlProperty = findViewById(R.id.tl_entity_detail_property);
        mTvRelation = findViewById(R.id.tv_entity_detail_relation);
        mTvQuestion = findViewById(R.id.tv_entity_detail_question);

        getInfo();

        if (error_message == null) {
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
                    tv2.setText(Html.fromHtml(object, Html.FROM_HTML_MODE_COMPACT | Html.FROM_HTML_OPTION_USE_CSS_COLORS));
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).setMargins(3, 3, 3, 3);
                    tableRow.addView(tv2, layoutParams);
                    mTlProperty.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(EntityDetailActivity.this, Integer.toString(mTlProperty.getChildCount()), Toast.LENGTH_SHORT).show();

            mTvRelation.setText(content.toString());
            mTvQuestion.setText(question.toString());
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
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
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
            e.printStackTrace();
            ok = false;
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ok;
    }

    private void getInfo() {
        property = new JSONArray();
        content = new JSONArray();
        question = new JSONArray();
        isFavourite = null;

        if (readInstance()) {
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
                e.printStackTrace();
            }
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
                error_message = "请求异常";
                e.printStackTrace();
            }
        }

    }
}