package com.java.xuhaotian.mainpage.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.java.xuhaotian.Consts;
import com.java.xuhaotian.HttpRequest;
import com.java.xuhaotian.R;
import com.java.xuhaotian.user.FavouriteActivity;
import com.java.xuhaotian.user.HistoryActivity;
import com.java.xuhaotian.user.ModifyPasswordActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class UserPageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_page, container, false);
        Log.d("EntityList", "---create User---");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView mTvUserName = view.findViewById(R.id.tv_username);
        Button mBtnModifyPassword = view.findViewById(R.id.btn_modify_password);
        Button mBtnLogout = view.findViewById(R.id.btn_logout);
        Button mBtnHistory = view.findViewById(R.id.btn_history);
        Button mBtnFavourite = view.findViewById(R.id.btn_favourite);

        mTvUserName.setText(Consts.getUserName());

        mBtnModifyPassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ModifyPasswordActivity.class);
            startActivity(intent);
        });

        mBtnLogout.setOnClickListener(v -> {
            JSONObject params = new JSONObject();
            try {
                params.put("token", Consts.getToken());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            new HttpRequest().putRequest(Consts.backendURL + "logout", params);
            Consts.setToken("");
            Objects.requireNonNull(getActivity()).finish();
        });

        mBtnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HistoryActivity.class);
            startActivity(intent);
        });

        mBtnFavourite.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FavouriteActivity.class);
            startActivity(intent);
        });
    }

}
