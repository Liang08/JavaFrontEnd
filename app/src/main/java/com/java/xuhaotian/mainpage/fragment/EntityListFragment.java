package com.java.xuhaotian.mainpage.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.java.xuhaotian.ChannelSelectActivity;
import com.java.xuhaotian.Consts;
import com.java.xuhaotian.R;
import com.java.xuhaotian.SearchActivity;
import com.java.xuhaotian.adapter.EntityListAdapter;
import com.java.xuhaotian.adapter.SubjectListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class EntityListFragment extends Fragment {
    private String[] mTitles;
    private TextView mTvSearch;
    private ListView mLvEntity;
    private RecyclerView mRvSubject;
    private Button mBtnSetting;
    private String subject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entity_list, container, false);
        Log.d("EntityList", "---create Entity List---");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvSearch = view.findViewById(R.id.tv_search);
        mLvEntity = view.findViewById(R.id.lv_entity);
        mRvSubject = view.findViewById(R.id.rv_subject);
        mBtnSetting = view.findViewById(R.id.btn_set);
        setData();
    }

    private void setData(){
        mTitles = new String[]{"语文", "数学", "英语", "物理", "化学", "生物"};
        ArrayList<String> stringList = new ArrayList<>(Arrays.asList(mTitles));
        Collections.addAll(stringList, mTitles);
        Collections.addAll(stringList, mTitles);
        Collections.addAll(stringList, mTitles);
        setListView(stringList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvSubject.setLayoutManager(linearLayoutManager);
        SubjectListAdapter subjectListAdapter = new SubjectListAdapter(getActivity(), Consts.getSubjectList());
        mRvSubject.setAdapter(subjectListAdapter);
        subjectListAdapter.setOnItemClickListener(new SubjectListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Consts.setSubjectNow(Consts.getSubjectList().get(position));
                setData();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        mBtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChannelSelectActivity.class);
                startActivityForResult(intent, 1);
            }
        });


    }

    public void setListView(ArrayList<String> stringArrayList){
        mLvEntity.setAdapter(new EntityListAdapter(getActivity(), stringArrayList));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            Consts.setSubjectNow(Consts.getSubjectList().get(0));
            setData();
        }
    }
}
