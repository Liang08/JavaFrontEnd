package com.java.xuhaotian.mainpage.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.java.xuhaotian.EntityDetailActivity;
import com.java.xuhaotian.HttpRequest;
import com.java.xuhaotian.R;
import com.java.xuhaotian.SearchActivity;
import com.java.xuhaotian.adapter.EntityListAdapter;
import com.java.xuhaotian.adapter.SubjectListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;

public class EntityListFragment extends Fragment {
    private TextView mTvSearch;
    private TextView mTvSubject;
    private ListView mLvEntity;
    private RecyclerView mRvSubject;
    private Button mBtnSetting;
    private String error_message;
    private ArrayList<HashMap<String, String>> stringList;

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
        mTvSubject = view.findViewById(R.id.tv_subject);
        mTvSubject.setText(Consts.getSubjectNow());
        setData();
        setListView(stringList);
        setSubjectList();
    }

    private void setData(){
        stringList = new ArrayList<>();
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
                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", String.valueOf(obj));
                    map.put("visited", "no");
                    stringList.add(map);
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
            error_message = "请求失败";
            e.printStackTrace();
        }
        if (error_message != null){
            Toast.makeText(getContext(), "请求失败", Toast.LENGTH_SHORT).show();
        }
    }

    void setSubjectList(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvSubject.setLayoutManager(linearLayoutManager);
        SubjectListAdapter subjectListAdapter = new SubjectListAdapter(getActivity(), Consts.getSubjectList());
        mRvSubject.setAdapter(subjectListAdapter);
        subjectListAdapter.setOnItemClickListener(new SubjectListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Consts.setSubjectNow(Consts.getSubjectList().get(position));
                mTvSubject.setText(Consts.getSubjectNow());
                setData();
                setListView(stringList);
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

        mLvEntity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), EntityDetailActivity.class);
                intent.putExtra("course", Consts.getSubjectName(Consts.getSubjectNow()));
                intent.putExtra("name", stringList.get(position).get("name"));
                String name = stringList.get(position).get("name");
                HashMap<String, String> map = new HashMap<>();
                map.put("name", name);
                map.put("visited", "yes");
                stringList.set(position, map);
                startActivityForResult(intent, 2);
            }
        });

    }

    public void setListView(ArrayList<HashMap<String, String>> stringArrayList){
        mLvEntity.setAdapter(new EntityListAdapter(getActivity(), stringArrayList));
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            Consts.setSubjectNow(Consts.getSubjectList().get(0));
            setSubjectList();
        }
        setListView(stringList);
    }
}
