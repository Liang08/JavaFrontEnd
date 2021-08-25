package com.java.xuhaotian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.java.xuhaotian.Consts;
import com.java.xuhaotian.HttpRequest;
import com.java.xuhaotian.R;
import com.java.xuhaotian.user.HistoryActivity;
import com.java.xuhaotian.user.HistoryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EntityDetailActivity extends AppCompatActivity {
    private Button mBtnReturn;
    private TextView mTvName, mTvRelation, mTvKnowledge, mTvQuestion;
    private Switch mSwitchFavourite;
    private String course, name;

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
        mTvKnowledge = findViewById(R.id.tv_entity_detail_knowledge);
        mTvRelation = findViewById(R.id.tv_entity_detail_relation);
        mTvQuestion = findViewById(R.id.tv_entity_detail_question);

        String error_message;
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
                JSONArray jsonArray = new JSONArray(response2.string());
                mSwitchFavourite.setChecked(jsonObject.getBoolean("isFavourite"));
                mTvRelation.setText(jsonObject.getJSONArray("content").toString());
                mTvKnowledge.setText(jsonObject.getJSONArray("property").toString());
                mTvQuestion.setText(jsonArray.toString());
                error_message = null;
            }
            else {
                error_message = "请求失败(" + response1.code() + "||" + response2.code() + ")";
            }
        } catch (JSONException e) {
            error_message = "请求异常";
            e.printStackTrace();
        }

        if (error_message == null) {

            mSwitchFavourite.setEnabled(true);
        }
        else {
            Toast.makeText(EntityDetailActivity.this, "实体详情获取失败：" + error_message, Toast.LENGTH_SHORT).show();
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
            Intent intent = getIntent();
            setResult(RESULT_OK);
            finish();
        });
    }
}