package com.java.xuhaotian.adapter;

import android.content.Context;
import android.graphics.Color;
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
import java.util.List;

public class SearchListAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, String>> mList;
    private Context mContext;
    private LayoutInflater layoutInflater;

    public final class Component{
        public ImageView ivImage;
        public TextView tvTitle;
        public TextView tvCategory;
    }

    public SearchListAdapter(Context context, ArrayList<HashMap<String, String>> list){
        mList = list;
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
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
        switch (Consts.getSubjectNow()){
            case "语文":
                component.ivImage.setImageResource(R.drawable.chinese);
                break;
            case "数学":
                component.ivImage.setImageResource(R.drawable.math);
                break;
            case "英语":
                component.ivImage.setImageResource(R.drawable.english);
                break;
            case "生物":
                component.ivImage.setImageResource(R.drawable.biology);
                break;
            case "物理":
                component.ivImage.setImageResource(R.drawable.physics);
                break;
            case "化学":
                component.ivImage.setImageResource(R.drawable.chemistry);
                break;
            case "政治":
                component.ivImage.setImageResource(R.drawable.politics);
                break;
            case "历史":
                component.ivImage.setImageResource(R.drawable.history);
                break;
            case "地理":
                component.ivImage.setImageResource(R.drawable.geography);
                break;
            default:
                break;
        }
        component.tvTitle.setText(mList.get(position).get("name"));
        component.tvCategory.setText(mList.get(position).get("entityType"));
        if (mList.get(position).get("visited") == "yes"){
            component.tvTitle.setTextColor(Color.rgb(168, 168, 168));
            component.tvCategory.setTextColor(Color.rgb(168, 168, 168));
        }else{
            component.tvTitle.setTextColor(Color.rgb(0, 0, 0));
            component.tvCategory.setTextColor(Color.rgb(0, 0, 0));
        }
        return convertView;
    }
}
