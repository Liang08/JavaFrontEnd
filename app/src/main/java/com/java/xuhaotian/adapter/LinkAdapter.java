package com.java.xuhaotian.adapter;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.java.xuhaotian.Consts;
import com.java.xuhaotian.R;

import java.util.ArrayList;
import java.util.HashMap;

public class LinkAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, String>> mList;
    private Context mContext;
    private LayoutInflater layoutInflater;

    public final class Component{
        public ImageView ivImage;
        public TextView tvTitle;
        public TextView tvCategory;
    }

    public LinkAdapter(Context context, ArrayList<HashMap<String, String>> list){
        this.mList = list;
        this.mContext = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Component component = null;
        if (convertView == null){
            component = new Component();
            convertView=layoutInflater.inflate(R.layout.listview_search_result, null);
            component.ivImage = convertView.findViewById(R.id.iv_image);
            component.tvTitle = convertView.findViewById(R.id.tv_title);
            component.tvCategory = convertView.findViewById(R.id.tv_category);
            convertView.setTag(component);
        }else{
            component = (Component) convertView.getTag();
        }
        component.ivImage.setImageResource(Consts.getSubjectIconResId(mList.get(position).get("course")));
        component.tvTitle.setText(mList.get(position).get("name"));
        component.tvCategory.setText(mList.get(position).get("entityType"));
        return convertView;
    }
}
