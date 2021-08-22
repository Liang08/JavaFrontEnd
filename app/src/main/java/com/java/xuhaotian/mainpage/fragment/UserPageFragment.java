package com.java.xuhaotian.mainpage.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.java.xuhaotian.Consts;
import com.java.xuhaotian.R;
import com.java.xuhaotian.user.FavouriteActivity;
import com.java.xuhaotian.user.HistoryActivity;
import com.java.xuhaotian.user.LoginActivity;
import com.java.xuhaotian.user.ModifyPasswordActivity;

public class UserPageFragment extends Fragment {
    private Button mBtnModifyPassword, mBtnLogout, mBtnHistory, mBtnFavourite;
    private TextView mTvUserName;

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
        mTvUserName = view.findViewById(R.id.tv_username);
        mBtnModifyPassword = view.findViewById(R.id.btn_modify_password);
        mBtnLogout = view.findViewById(R.id.btn_logout);
        mBtnHistory = view.findViewById(R.id.btn_history);
        mBtnFavourite = view.findViewById(R.id.btn_favourite);

        mTvUserName.setText(Consts.getUserName());

        mBtnModifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ModifyPasswordActivity.class);
                startActivity(intent);
            }
        });

        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mBtnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(intent);
            }
        });

        mBtnFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FavouriteActivity.class);
                startActivity(intent);
            }
        });
    }

}
