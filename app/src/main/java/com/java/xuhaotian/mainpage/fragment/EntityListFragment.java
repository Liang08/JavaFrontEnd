package com.java.xuhaotian.mainpage.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.java.xuhaotian.R;
import com.java.xuhaotian.SearchActivity;
import com.java.xuhaotian.adapter.EntityListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class EntityListFragment extends Fragment {
    private String[] mTitles;
    private TextView mTvSearch;
    private ListView mLvEntity;
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
        setData();
    }

    private void setData(){
        mTitles = new String[]{"语文", "数学", "英语", "物理", "化学", "生物"};
        ArrayList<String> stringList = new ArrayList<>(Arrays.asList(mTitles));
        Collections.addAll(stringList, mTitles);
        Collections.addAll(stringList, mTitles);
        Collections.addAll(stringList, mTitles);
        setListView(stringList);
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
    }

    public void setListView(ArrayList<String> stringArrayList){
        mLvEntity.setAdapter(new EntityListAdapter(getActivity(), stringArrayList));
    }
}
