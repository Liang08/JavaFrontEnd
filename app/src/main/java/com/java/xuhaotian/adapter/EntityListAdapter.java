package com.java.xuhaotian.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.java.xuhaotian.R;

import java.util.ArrayList;

public class EntityListAdapter extends BaseAdapter {
    private ArrayList<String> mList;
    private Context mContext;
    private LayoutInflater layoutInflater;

    public EntityListAdapter(Context context, ArrayList<String> list){
        mList = list;
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public final class Component{
        public ImageView ivImage;
        public TextView tvTitle;
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
            convertView=layoutInflater.inflate(R.layout.listview_entity, null);
            component.ivImage = convertView.findViewById(R.id.iv_image);
            component.tvTitle = convertView.findViewById(R.id.tv_title);
            convertView.setTag(component);
        }else{
            component = (Component) convertView.getTag();
        }
        component.ivImage.setImageResource(R.drawable.chemistry);
        component.tvTitle.setText(mList.get(position));
        return convertView;
    }
}
