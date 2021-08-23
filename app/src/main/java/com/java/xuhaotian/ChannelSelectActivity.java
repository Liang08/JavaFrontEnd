package com.java.xuhaotian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.java.xuhaotian.adapter.AddListAdapter;

import java.util.ArrayList;

public class ChannelSelectActivity extends AppCompatActivity {

    private TextView mTvFinish;
    private RecyclerView mRvMyChannel;
    private RecyclerView mRvOtherChannel;
    private ArrayList<String> mDataOne;
    private ArrayList<String> mDataTwo;

    boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_select);
        initViews();
        initEvent();
    }

    public void initViews(){
        mTvFinish = findViewById(R.id.tv_finish);
        mRvMyChannel = findViewById(R.id.rv_my_channel);
        mRvOtherChannel = findViewById(R.id.rv_add_channel);
    }

    public void initEvent(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(this, 3);
        mDataOne = Consts.getSubjectList();
        mDataTwo = new ArrayList<>();
        for (String s : Consts.getTotal()) {
            if (!mDataOne.contains(s)){
                mDataTwo.add(s);
            }
        }

        mTvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        AddListAdapter addListAdapter = new AddListAdapter(R.layout.recyclerview_add_list, mDataTwo);
        AddListAdapter myListAdapter = new AddListAdapter(R.layout.recyclerview_add_list, mDataOne);

        mRvOtherChannel.setLayoutManager(gridLayoutManager);
        mRvOtherChannel.setAdapter(addListAdapter);
        mRvOtherChannel.setItemAnimator(new DefaultItemAnimator());

        mRvMyChannel.setLayoutManager(gridLayoutManager2);
        mRvMyChannel.setAdapter(myListAdapter);
        mRvMyChannel.setItemAnimator(new DefaultItemAnimator());

        addListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                String title = mDataTwo.get(position);
                Log.d("channel", "add chanel:" + title);

                mDataOne.add(title);
                myListAdapter.notifyItemChanged(mDataOne.size() - 1);
                mDataTwo.remove(position);
                addListAdapter.notifyItemRemoved(position);

                Log.d("channel", "channels:" + mDataOne.toString());
            }
        });


        myListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                String title = mDataOne.get(position);
                Log.d("channel", "remove chanel:" + title);

                mDataTwo.add(0, title);
                addListAdapter.notifyItemInserted(0);
                mDataOne.remove(position);
                myListAdapter.notifyItemRemoved(position);

                Log.d("channel", "channels:" + mDataOne.toString());
            }
        });
    }
}