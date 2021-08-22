package com.java.xuhaotian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.java.xuhaotian.mainpage.MainPageActivity;

public class SearchActivity extends AppCompatActivity {

    private Button mBtnCancel;
    private EditText mEtSearch;
    private TextView mTvDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
    }

    private void initView(){
        mBtnCancel = findViewById(R.id.btn_cancel);
        mEtSearch = findViewById(R.id.et_search);
        mTvDelete = findViewById(R.id.tv_deleteAll);
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

            }
        });

        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    Bundle bundle = new Bundle();
                    String keyword = mEtSearch.getText().toString().trim();
                    bundle.putString("searchKey", keyword);

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
    }
}