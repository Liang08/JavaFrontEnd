package com.java.xuhaotian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchResultActivity extends AppCompatActivity {
    private ListView mLvResult;
    private ImageButton mIbReturn;
    private TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Intent intent = getIntent();
        String searchKey = intent.getStringExtra("searchKey");

        initView(searchKey);
        initEvent();
    }

    void initView(String keyword){
        mLvResult = findViewById(R.id.lv_result);
        mIbReturn = findViewById(R.id.ib_return);
        mTvTitle = findViewById(R.id.tv_title);
        mTvTitle.setText("搜索\'" + keyword + "\'结果");

    }

    void initEvent(){
        mIbReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }
}