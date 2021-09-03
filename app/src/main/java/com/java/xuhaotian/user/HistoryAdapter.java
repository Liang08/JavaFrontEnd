package com.java.xuhaotian.user;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.java.xuhaotian.Consts;
import com.java.xuhaotian.R;

import java.util.List;

public class HistoryAdapter extends BaseAdapter {

    private final List<Pair<String, String>> mData;
    private final Context mContext;
    private View.OnClickListener mDetailListener = null;

    public HistoryAdapter(List<Pair<String, String>> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    public void setDetailListener(View.OnClickListener mDetailListener) {
        this.mDetailListener = mDetailListener;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.history_list_item, parent,false);
            holder = new ViewHolder();
            holder.iv_icon = convertView.findViewById(R.id.iv_history_list_item_icon);
            holder.tv_name = convertView.findViewById(R.id.tv_history_list_item_name);
            holder.btn_detail = convertView.findViewById(R.id.btn_history_list_item_detail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.iv_icon.setBackgroundResource(Consts.getSubjectIconResId(mData.get(position).first));
        holder.tv_name.setText(mData.get(position).second);
        holder.btn_detail.setTag(position);

        holder.btn_detail.setOnClickListener(mDetailListener);

        return convertView;
    }

    static class ViewHolder{
        private ImageView iv_icon;
        private TextView tv_name;
        private Button btn_detail;
    }
}
