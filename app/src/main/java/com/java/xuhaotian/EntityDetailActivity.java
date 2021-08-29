package com.java.xuhaotian;

import android.content.Intent;
import android.os.Bundle;
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

public class EntityDetailActivity extends AppCompatActivity {
    private static final String TAG = "EntityDetailActivity";

    private TextView mTvName;
    private Button mBtnReturn;
    private Switch mSwitchFavourite;
    private String course, name;
    private String error_message;
    private JSONArray property = new JSONArray();
    private JSONArray content = new JSONArray();
    private JSONArray question = new JSONArray();
    private Boolean isFavourite = null;
    private final List<Question> questionList = new ArrayList<>();
    private final List<String> groupList = new ArrayList<>();
    private final List<List<Pair<String, String>>> itemList = new ArrayList<>();
    private int mContentHeight;
    private List<Integer> mContentExpandHeightList;
    private boolean isWaiting = false;

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
        float dip = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

        mBtnReturn = findViewById(R.id.btn_entity_detail_return);
        mTvName = findViewById(R.id.tv_entity_detail_name);
        mTvName.setText(name);

        mSwitchFavourite = findViewById(R.id.switch_entity_detail_favourite);
        mSwitchFavourite.setEnabled(false);
        TableLayout mTlProperty = findViewById(R.id.tl_entity_detail_property);
        ExpandableListView mExLvContentList = findViewById(R.id.exLv_entity_detail_content_list);
        ListView mLvQuestionList = findViewById(R.id.lv_entity_detail_question_list);

        getInfo();

        if (error_message == null) {
            // init property
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
            // init content
            groupList.clear();
            itemList.clear();
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

            mContentHeight = 10;
            mContentExpandHeightList = new ArrayList<>();
            for (int i = 0; i < mContentAdapter.getGroupCount(); i++) {
                View groupView = mContentAdapter.getGroupView(i, false, null, mExLvContentList);
                groupView.measure(0, 0);
                mContentHeight += groupView.getMeasuredHeight();
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
            mContentHeight += (mContentAdapter.getGroupCount() - 1) * mExLvContentList.getDividerHeight();
            setHeight(mExLvContentList, mContentHeight);
            mExLvContentList.setOnGroupExpandListener(groupPosition -> {
                mContentHeight += mContentExpandHeightList.get(groupPosition);
                setHeight(mExLvContentList, mContentHeight);
            });
            mExLvContentList.setOnGroupCollapseListener(groupPosition -> {
                mContentHeight -= mContentExpandHeightList.get(groupPosition);
                setHeight(mExLvContentList, mContentHeight);
            });
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
            // init favourite
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            getIsFavourite();
            if (isFavourite != null) {
                mSwitchFavourite.setChecked(isFavourite);
                mSwitchFavourite.setEnabled(true);
            }
            isWaiting = false;
            mBtnReturn.setEnabled(true);
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
            postHistory();
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
            else {
                Toast.makeText(EntityDetailActivity.this, "同步收藏状态失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Throwable e) {
            Log.d(TAG, Log.getStackTraceString(e));
        }
    }

    private void postHistory() {
        try {
            JSONObject params = new JSONObject();
            params.put("course", course);
            params.put("name", name);
            params.put("token", Consts.getToken());
            HttpRequest.MyResponse response = new HttpRequest().postRequest(Consts.backendURL + "addInstanceHistory", params);
            if (response.code() != 200) {
                Toast.makeText(EntityDetailActivity.this, "上传访问记录失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Throwable e) {
            Log.d(TAG, Log.getStackTraceString(e));
        }
    }

    private void setHeight(@NonNull View view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
        view.setLayoutParams(params);
    }
}