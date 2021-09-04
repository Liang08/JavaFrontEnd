package com.java.xuhaotian;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SyllabusAdapter extends BaseAdapter {

    private final List<String> mData;
    private final Context mContext;

    public SyllabusAdapter(List<String> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.syllabus_list_item, parent,false);
            holder = new ViewHolder();
            holder.tv_name = convertView.findViewById(R.id.tv_syllabus_list_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name.setText(Html.fromHtml(mData.get(position), Html.FROM_HTML_MODE_LEGACY));

        return convertView;
    }

    static class ViewHolder{
        private TextView tv_name;
    }
}
