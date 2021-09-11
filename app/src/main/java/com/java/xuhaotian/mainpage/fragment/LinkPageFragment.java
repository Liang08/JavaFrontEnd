package com.java.xuhaotian.mainpage.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.Touch;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.java.xuhaotian.Consts;
import com.java.xuhaotian.EntityDetailActivity;
import com.java.xuhaotian.HttpRequest;
import com.java.xuhaotian.R;
import com.java.xuhaotian.SearchResultActivity;
import com.java.xuhaotian.adapter.LinkAdapter;
import com.java.xuhaotian.adapter.SearchListAdapter;
import com.java.xuhaotian.user.FavouriteActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.java.xuhaotian.Consts.JSON;

public class LinkPageFragment extends Fragment {
    private TextView mTvTitle;
    private EditText mEtLink;
    private Button mBtnSearch;
    private Spinner mSpSubject;
    private String error_message;
    private String subjectSelected;
    private ListView mLvResult;
    private LinkAdapter mAdapter;
    private String[] starArray = {"语文", "数学", "英语", "物理", "化学", "生物", "政治", "地理", "历史"};
    ArrayList<HashMap<String, String>> searchResult = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_link_page, container, false);
        Log.d("EntityList", "---create link page---");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvTitle = view.findViewById(R.id.tv_title);
        mBtnSearch = view.findViewById(R.id.btn_search);
        mEtLink = view.findViewById(R.id.et_link);
        mLvResult = view.findViewById(R.id.lv_list);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchResult.clear();
                String context = mEtLink.getText().toString();
                try {
                    JSONObject json = new JSONObject();
                    json.put("token", Consts.getToken());
                    json.put("context", context);
                    HttpRequest.MyResponse response = new HttpRequest().postRequest(Consts.backendURL + "linkInstance", json);
                    if (response.code() == 200) {
                        JSONArray jsonArray = new JSONArray(response.string());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String entityType = obj.getString("entity_type");
                            String name = obj.getString("entity");
                            String course = obj.getString("entity_course");
                            Log.d("test", course + entityType + ": " + name);
                            HashMap<String, String> map = new HashMap<>();
                            map.put("name", name);
                            map.put("course", course);
                            map.put("entityType", entityType);
                            searchResult.add(map);
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
                    error_message = "请求失败";
                }
                mAdapter = new LinkAdapter(getActivity(), searchResult);
                mLvResult.setAdapter(mAdapter);
                if (error_message != null){
                    Toast.makeText(getContext(), "获取知识链接失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mLvResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), EntityDetailActivity.class);
                intent.putExtra("course", searchResult.get(position).get("course"));
                intent.putExtra("name", searchResult.get(position).get("name"));
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
