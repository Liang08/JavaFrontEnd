package com.java.xuhaotian.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.java.xuhaotian.R;

import java.util.List;

public class AddListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public AddListAdapter(int layoutResId, List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
        baseViewHolder.setText(R.id.tv_title, s);
    }
}
