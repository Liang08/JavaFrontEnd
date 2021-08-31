package com.java.xuhaotian;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EntityDetailActivity extends AppCompatActivity {
    private static final String TAG = "EntityDetailActivity";

    private TextView mTvName;
    private Button mBtnReturn, mBtnQuiz;
    private Switch mSwitchFavourite;
    TableLayout mTlProperty;
    ExpandableListView mExLvContentList;
    ListView mLvQuestionList;

    private String course, name;

    private boolean isWaiting = false;

    private Call favouriteCall = null;
    private Call historyCall = null;
    private Call instanceCall = null;
    private Call questionCall = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_detail);

        Intent intent = getIntent();
        course = intent.getStringExtra("course");
        name = intent.getStringExtra("name");

        initViews();
        initEvents();
        initData();
    }

    @Override
    protected void onDestroy() {
        if (favouriteCall != null) favouriteCall.cancel();
        if (historyCall != null) historyCall.cancel();
        if (instanceCall != null) instanceCall.cancel();
        if (questionCall != null) questionCall.cancel();
        super.onDestroy();
    }

    private void initViews() {
        mBtnReturn = findViewById(R.id.btn_entity_detail_return);
        mBtnQuiz = findViewById(R.id.btn_entity_detail_quiz);
        mTvName = findViewById(R.id.tv_entity_detail_name);
        mTvName.setText(name);

        mSwitchFavourite = findViewById(R.id.switch_entity_detail_favourite);
        mSwitchFavourite.setEnabled(false);
        mTlProperty = findViewById(R.id.tl_entity_detail_property);
        mExLvContentList = findViewById(R.id.exLv_entity_detail_content_list);
        mLvQuestionList = findViewById(R.id.lv_entity_detail_question_list);
    }

    private void initProperty(@NonNull JSONArray property) {
        float dip = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        for (int i = 0; i < property.length(); i++) {
            try {
                JSONObject obj = property.getJSONObject(i);
                String predicate = obj.getString("predicateLabel");
                String object = obj.getString("object");
                TableRow tableRow = new TableRow(this);
                TextView tv1 = new TextView(this);
                tv1.setText(predicate);
                tv1.setTextSize(20);
                tv1.setBackgroundColor(getColor(R.color.white));
                TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams1.setMargins((int) (2 * dip), (int) (1 * dip), (int) (1 * dip), (int) (1 * dip));
                tableRow.addView(tv1, layoutParams1);
                TextView tv2 = new TextView(this);
                tv2.setText(Html.fromHtml(object, Html.FROM_HTML_MODE_LEGACY));
                tv2.setTextSize(16);
                tv2.setBackgroundColor(getColor(R.color.white));
                TableRow.LayoutParams layoutParams2 = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams2.setMargins((int) (1 * dip), (int) (1 * dip), (int) (2 * dip), (int) (1 * dip));
                tableRow.addView(tv2, layoutParams2);
                tableRow.setBackgroundColor(getColor(R.color.lightSteelBlue));
                mTlProperty.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } catch (JSONException e) {
                Log.d(TAG, Log.getStackTraceString(e));
            }
        }
    }

    private void initContent(@NonNull JSONArray content) {
        final List<String> groupList = new ArrayList<>();
        final List<List<Pair<String, String>>> itemList = new ArrayList<>();
        for (int i = 0; i < content.length(); i++) {
            try {
                JSONObject obj = content.getJSONObject(i);
                String predicate = obj.getString("predicate_label");
                JSONArray jsonArray = obj.getJSONArray("content");
                List<Pair<String, String>> list = new ArrayList<>();
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject obj2 = jsonArray.getJSONObject(j);
                    if (obj2.has("subject_label")) {
                        list.add(Pair.create(obj2.getString("subject_course"), obj2.getString("subject_label")));
                    }
                    else if (obj2.has("object_label")) {
                        list.add(Pair.create(obj2.getString("object_course"), obj2.getString("object_label")));
                    }
                }
                groupList.add(predicate);
                itemList.add(list);
            } catch (JSONException e) {
                Log.d(TAG, Log.getStackTraceString(e));
            }
        }
        EntityDetailContentAdapter mContentAdapter = new EntityDetailContentAdapter(groupList, itemList, EntityDetailActivity.this);
        mContentAdapter.setItemListener(v -> {
            if (!isWaiting) {
                Intent intent = new Intent(EntityDetailActivity.this, EntityDetailActivity.class);
                intent.putExtra("course", ((Pair<String, String>)v.getTag()).first);
                intent.putExtra("name", ((Pair<String, String>)v.getTag()).second);
                isWaiting = true;
                mSwitchFavourite.setEnabled(false);
                mBtnReturn.setEnabled(false);
                startActivityForResult(intent, 1);
            }
        });
        mExLvContentList.setAdapter(mContentAdapter);

        AtomicInteger mContentHeight = new AtomicInteger();
        List<Integer> mContentExpandHeightList;
        mContentHeight.set(10);
        mContentExpandHeightList = new ArrayList<>();
        for (int i = 0; i < mContentAdapter.getGroupCount(); i++) {
            View groupView = mContentAdapter.getGroupView(i, false, null, mExLvContentList);
            groupView.measure(0, 0);
            mContentHeight.addAndGet(groupView.getMeasuredHeight());
            int sum = -groupView.getMeasuredHeight();
            groupView = mContentAdapter.getGroupView(i, true, null, mExLvContentList);
            groupView.measure(0, 0);
            sum += groupView.getMeasuredHeight();
            for (int j = 0; j < mContentAdapter.getChildrenCount(i); j++) {
                View itemView = mContentAdapter.getChildView(i, j, false, null, mExLvContentList);
                itemView.measure(0, 0);
                sum += itemView.getMeasuredHeight();
            }
            mContentExpandHeightList.add(sum);
        }
        mContentHeight.addAndGet((mContentAdapter.getGroupCount() - 1) * mExLvContentList.getDividerHeight());
        setHeight(mExLvContentList, mContentHeight.get());
        mExLvContentList.setOnGroupExpandListener(groupPosition -> {
            mContentHeight.addAndGet(mContentExpandHeightList.get(groupPosition));
            setHeight(mExLvContentList, mContentHeight.get());
        });
        mExLvContentList.setOnGroupCollapseListener(groupPosition -> {
            mContentHeight.addAndGet(-mContentExpandHeightList.get(groupPosition));
            setHeight(mExLvContentList, mContentHeight.get());
        });
    }

    private void initQuestion(@NonNull JSONArray question) {
        final List<Question> questionList = new ArrayList<>();
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
        EntityDetailQuestionAdapter mQuestionAdapter = new EntityDetailQuestionAdapter(questionList, EntityDetailActivity.this);
        mLvQuestionList.setAdapter(mQuestionAdapter);
        mQuestionAdapter.setShareListener(v -> {
            int position = Integer.parseInt(v.getTag().toString());
            Toast.makeText(EntityDetailActivity.this, "分享:" + questionList.get(position).getQBody(), Toast.LENGTH_SHORT).show();
            return true;
        });
        int mQuestionHeight = 10;
        for (int i = 0; i < mQuestionAdapter.getCount(); i++) {
            View itemView = mQuestionAdapter.getView(i, null, mLvQuestionList);
            itemView.measure(0, 0);
            mQuestionHeight += itemView.getMeasuredHeight();
        }
        mQuestionHeight += (mQuestionAdapter.getCount() - 1) * mLvQuestionList.getDividerHeight();
        setHeight(mLvQuestionList, mQuestionHeight);
    }

    private void initEvents() {
        mTvName.setOnLongClickListener(v -> {
            Toast.makeText(EntityDetailActivity.this, "分享:" + name, Toast.LENGTH_SHORT).show();
            return true;
        });
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
        mBtnQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(EntityDetailActivity.this, QuizActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            initIsFavourite();
            isWaiting = false;
            mBtnReturn.setEnabled(true);
        }
    }

    @NonNull
    @Contract(pure = true)
    private String getFileName() {
        return course + "_xht_" + name + ".instance";
    }

    private void saveInstance(JSONArray property, JSONArray content, JSONArray question) {
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

    private boolean readInstance(JSONArray[] property, JSONArray[] content, JSONArray[] question) {
        File file = new File(getCacheDir(), getFileName());
        ObjectInputStream ois = null;
        boolean ok = true;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            property[0] = new JSONArray((String)ois.readObject());
            content[0] = new JSONArray((String)ois.readObject());
            question[0] = new JSONArray((String)ois.readObject());
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

    private void initData() {
        final JSONArray[] property = new JSONArray[1];
        final JSONArray[] content = new JSONArray[1];
        final JSONArray[] question = new JSONArray[1];
        if (readInstance(property, content, question)) {
            initProperty(property[0]);
            initContent(content[0]);
            initQuestion(question[0]);
            initIsFavourite();
            postHistory(course, name);
        }
        else {
            AtomicInteger flag = new AtomicInteger();

            Map<String, Object> params1 = new HashMap<>();
            params1.put("course", course);
            params1.put("name", name);
            params1.put("token", Consts.getToken());
            if (instanceCall != null) instanceCall.cancel();
            instanceCall = new HttpRequest().getRequestCall(Consts.backendURL + "getInfoByInstanceName", params1);
            instanceCall.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(EntityDetailActivity.this, "请求异常", Toast.LENGTH_SHORT).show());
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                            property[0] = jsonObject.getJSONArray("property");
                            content[0] = jsonObject.getJSONArray("content");
                            flag.addAndGet(1);
                            boolean isFavourite = jsonObject.getBoolean("isFavourite");
                            new Handler(Looper.getMainLooper()).post(() -> {
                                initProperty(property[0]);
                                initContent(content[0]);
                                setSwitchFavourite(isFavourite);
                            });
                        } catch (NullPointerException | JSONException e) {
                            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(EntityDetailActivity.this, "请求异常", Toast.LENGTH_SHORT).show());
                        }
                    }
                    else {
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(EntityDetailActivity.this, "请求失败（ + " + response.code() + " ）", Toast.LENGTH_SHORT).show());
                    }
                }
            });
            Map<String, Object> params2 = new HashMap<>();
            params2.put("uriName", name);
            params2.put("token", Consts.getToken());
            if (questionCall != null) questionCall.cancel();
            questionCall = new HttpRequest().getRequestCall(Consts.backendURL + "getQuestionListByUriName", params2);
            questionCall.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(EntityDetailActivity.this, "请求异常", Toast.LENGTH_SHORT).show());
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            question[0] = new JSONArray(Objects.requireNonNull(response.body()).string());
                            flag.addAndGet(2);
                            new Handler(Looper.getMainLooper()).post(() -> initQuestion(question[0]));
                        } catch (NullPointerException | JSONException e) {
                            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(EntityDetailActivity.this, "请求异常", Toast.LENGTH_SHORT).show());
                        }
                    }
                    else {
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(EntityDetailActivity.this, "请求失败（ + " + response.code() + " ）", Toast.LENGTH_SHORT).show());
                    }
                }
            });

            Thread thread = new Thread(() -> {
                while (!isDestroyed()) {
                    if (flag.get() == 3) {
                        saveInstance(property[0], content[0], question[0]);
                        break;
                    }
                }
            });
            thread.start();
        }
    }

    private void initIsFavourite() {
        Map<String, Object> params = new HashMap<>();
        params.put("course", course);
        params.put("name", name);
        params.put("token", Consts.getToken());
        if (favouriteCall != null) favouriteCall.cancel();
        favouriteCall = new HttpRequest().getRequestCall(Consts.backendURL + "isFavourite", params);
        favouriteCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(EntityDetailActivity.this, "同步收藏状态失败", Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        boolean isFavourite = Boolean.parseBoolean(Objects.requireNonNull(response.body()).string());
                        new Handler(Looper.getMainLooper()).post(() -> setSwitchFavourite(isFavourite));
                    } catch (NullPointerException e) {
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(EntityDetailActivity.this, "同步收藏状态失败", Toast.LENGTH_SHORT).show());
                    }
                }
                else {
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(EntityDetailActivity.this, "同步收藏状态失败", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void setSwitchFavourite(boolean isFavourite) {
        mSwitchFavourite.setChecked(isFavourite);
        mSwitchFavourite.setEnabled(true);
    }

    private void postHistory(String course, String name) {
        JSONObject params = new JSONObject();
        try {
            params.put("course", course);
            params.put("name", name);
            params.put("token", Consts.getToken());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        if (historyCall != null) historyCall.cancel();
        historyCall = new HttpRequest().postRequestCall(Consts.backendURL + "addInstanceHistory", params);
        historyCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(EntityDetailActivity.this, "上传访问记录失败", Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (!response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(EntityDetailActivity.this, "上传访问记录失败", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void setHeight(@NonNull View view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
        view.setLayoutParams(params);
    }
}