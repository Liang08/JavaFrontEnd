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

import com.java.xuhaotian.QuizActivity;
import com.java.xuhaotian.R;

public class ReviewFragment extends Fragment {
    private TextView mTvTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        Log.d("EntityList", "---create Review---");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvTitle = view.findViewById(R.id.tv_title);
        Button mBtnRecommendQuiz = view.findViewById(R.id.btn_review_recommend_quiz);

        mBtnRecommendQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QuizActivity.class);
            intent.putExtra("name", "");
            startActivity(intent);
        });
    }

}
