package com.java.xuhaotian;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    /**
     * name is "": combined quiz
     * name isn't "": special quiz
     */
    private String name;

    private final List<Question> questionList = new ArrayList<>();
    private final List<String> answerList = new ArrayList<>();

    ViewPager2 mVpQuestionList;
    TextView mTvNotice;
    private Button mBtnSubmit, mBtnExit;
    private QuizAdapter mAdapter;

    private Call answerCall = null;
    private Call questionCall = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        initViews();
        initEvents();
        initData();
    }

    @Override
    protected void onDestroy() {
        if (answerCall != null) answerCall.cancel();
        if (questionCall != null) questionCall.cancel();
        super.onDestroy();
    }

    private void initViews() {
        mVpQuestionList = findViewById(R.id.vp_quiz_question_list);
        mVpQuestionList.setVisibility(View.INVISIBLE);
        mTvNotice = findViewById(R.id.tv_quiz_notice);
        mTvNotice.setVisibility(View.INVISIBLE);
        mBtnSubmit = findViewById(R.id.btn_quiz_submit);
        mBtnSubmit.setEnabled(false);
        mBtnExit = findViewById(R.id.btn_quiz_exit);
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

    private void initData() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("token", Consts.getToken());
        String url;
        if (name.equals("")) url = "getRecommendQuestionList";
        else url = "getSpecialTopicQuestionList";
        questionCall = new HttpRequest().getRequestCall(Consts.backendURL + url, params);
        questionCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(QuizActivity.this, "请求异常", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray question = new JSONArray(Objects.requireNonNull(response.body()).string());
                        new Handler(Looper.getMainLooper()).post(() -> initQuestion(question));
                    } catch (NullPointerException | JSONException e) {
                        Log.d(TAG, Log.getStackTraceString(e));
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(QuizActivity.this, "请求异常", Toast.LENGTH_SHORT).show());
                    }
                }
                else {
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(QuizActivity.this, "请求失败", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void initQuestion(@NonNull JSONArray question) {
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

        if (questionList.size() == 0) {
            mVpQuestionList.setVisibility(View.GONE);
            mTvNotice.setVisibility(View.VISIBLE);
            return;
        }
        mTvNotice.setVisibility(View.GONE);
        for (int i = 0; i < questionList.size(); i++) answerList.add("");
        mAdapter = new QuizAdapter(questionList, QuizActivity.this, answerList);
        mVpQuestionList.setAdapter(mAdapter);
        mVpQuestionList.setVisibility(View.VISIBLE);

        mBtnSubmit.setEnabled(true);
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
            answerCall = new HttpRequest().postRequestCall(Consts.backendURL + "answerQuestion", params);
            answerCall.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(QuizActivity.this, "上传做题记录失败", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    if (!response.isSuccessful()) {
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(QuizActivity.this, "上传做题记录失败", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (JSONException e) {
            Log.d(TAG, Log.getStackTraceString(e));
            Toast.makeText(QuizActivity.this, "上传做题记录失败", Toast.LENGTH_SHORT).show();
        }
    }

}