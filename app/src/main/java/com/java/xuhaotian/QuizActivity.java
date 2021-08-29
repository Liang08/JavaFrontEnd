package com.java.xuhaotian;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    /**
     * name is "": combined quiz
     * name isn't "": special quiz
     */
    private String name;

    private String error_message;

    private final List<Question> questionList = new ArrayList<>();
    private final List<String> answerList = new ArrayList<>();

    private Button mBtnSubmit, mBtnExit;
    private QuizAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        initViews();
        initEvents();
    }

    private void initViews() {
        ViewPager2 mVpQuestionList = findViewById(R.id.vp_quiz_question_list);
        TextView mTvNotice = findViewById(R.id.tv_quiz_notice);
        mBtnSubmit = findViewById(R.id.btn_quiz_submit);
        mBtnSubmit.setEnabled(false);
        mBtnExit = findViewById(R.id.btn_quiz_exit);

        getQuestionList();
        if (error_message == null) {
            if (questionList.size() == 0) {
                mVpQuestionList.setVisibility(View.GONE);
                return;
            }
            mTvNotice.setVisibility(View.GONE);
            for (int i = 0; i < questionList.size(); i++) answerList.add("");
            mAdapter = new QuizAdapter(questionList, QuizActivity.this, answerList);
            mVpQuestionList.setAdapter(mAdapter);

            mBtnSubmit.setEnabled(true);
        }
        else {
            Toast.makeText(QuizActivity.this, error_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void initEvents() {
        mBtnSubmit.setOnClickListener(v -> {
            mBtnSubmit.setEnabled(false);
            postAnswerList();
            int score = 0;
            for (int i = 0; i < questionList.size(); i++) {
                if (questionList.get(i).getQAnswer().equals(answerList.get(i)))
                    score += 10;
            }
            final String str;
            if (score == 10 * questionList.size()) str = "表现完美！";
            else if (score >= 8 * questionList.size()) str = "成绩优异！";
            else if (score >= 6 * questionList.size()) str = "表现不错！";
            else if (score >= 3 * questionList.size()) str = "再接再厉！";
            else str = "加油！";
            AlertDialog alertDialog = new AlertDialog.Builder(QuizActivity.this)
                    .setMessage(str + "\n得分：" + score + " / " + (10 * questionList.size()))
                    .setNegativeButton("确定", (dialog, which) -> {}).create();
            alertDialog.show();
            mAdapter.disable();
        });
        mBtnExit.setOnClickListener(v -> finish());
    }

    private void getQuestionList() {
        error_message = null;

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("token", Consts.getToken());
        String url;
        if (name.equals("")) url = "getRecommendQuestionList";
        else url = "getSpecialTopicQuestionList";
        HttpRequest.MyResponse response = new HttpRequest().getRequest(Consts.backendURL + url, params);

        if (response.code() == 200) {
            try {
                JSONArray question = new JSONArray(response.string());
                questionList.clear();
                for (int i = 0; i < question.length(); i++) {
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
                }
            } catch (JSONException e) {
                error_message = "请求异常";
                Log.d(TAG, Log.getStackTraceString(e));
            }
        }
        else {
            error_message = "请求失败(" + response.code() + ")";
        }
    }

    private void postAnswerList() {
        try {
            JSONObject params = new JSONObject();
            JSONArray answer = new JSONArray();
            for (int i = 0; i < questionList.size(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("id", questionList.get(i).getId());
                obj.put("isCorrect", questionList.get(i).getQAnswer().equals(answerList.get(i)));
                answer.put(obj);
            }
            params.put("answer", answer);
            params.put("token", Consts.getToken());
            HttpRequest.MyResponse response = new HttpRequest().postRequest(Consts.backendURL + "answerQuestion", params);
            if (response.code() != 200) {
                Toast.makeText(QuizActivity.this, "上传做题记录失败", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.d(TAG, Log.getStackTraceString(e));
            Toast.makeText(QuizActivity.this, "上传做题记录失败", Toast.LENGTH_SHORT).show();
        }
    }

}