package com.java.xuhaotian.mainpage.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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
import com.java.xuhaotian.HttpRequest;
import com.java.xuhaotian.R;
import com.java.xuhaotian.SearchActivity;
import com.java.xuhaotian.adapter.EntityListAdapter;
import com.java.xuhaotian.adapter.SubjectListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class EntityListFragment extends Fragment {
    private String[] mTitles;
    private TextView mTvSearch;
    private ListView mLvEntity;
    private RecyclerView mRvSubject;
    private Button mBtnSetting;
    private String subject;
    private String error_message;

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
        ArrayList<String> stringList = new ArrayList<>();
        error_message = null;
        Log.d("test", "start");
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("token", Consts.getToken());
            params.put("subject", Consts.getSubjectName(Consts.getSubjectNow()));
            HttpRequest.MyResponse response = new HttpRequest().getRequest(Consts.backendURL + "getHotInstance", params);
            if (response.code() == 200) {
                JSONArray jsonArray = new JSONArray(response.string());
                for (int i = 0; i < jsonArray.length(); i++) {
                    Object obj = jsonArray.get(i);
                    stringList.add(String.valueOf(obj));
                    Log.d("test", String.valueOf(obj));
                }

            }
            else if (response.code() == 401) {
                Log.d("test", "401");
                JSONObject obj = new JSONObject(response.string());
                error_message = obj.getString("message") + "";
            }
            else {
                Log.d("test", String.valueOf(response.code()));
                error_message = "请求失败(" + response.code() + ")";
            }
        } catch (Exception e) {
            Log.d("test", "fail");
            e.printStackTrace();
        }

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
