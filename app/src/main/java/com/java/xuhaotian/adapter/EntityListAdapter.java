package com.java.xuhaotian.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
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
import java.util.Random;

public class EntityListAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, String>> mList;
    private Context mContext;
    private LayoutInflater layoutInflater;

    public EntityListAdapter(Context context, ArrayList<HashMap<String, String>> list){
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
            Random r = new Random();
            int ran1 = r.nextInt(2);
            if (ran1 == 1){
                convertView=layoutInflater.inflate(R.layout.listview_entity, null);
            }else{
                convertView=layoutInflater.inflate(R.layout.listview_entity2, null);
            }


            component.ivImage = convertView.findViewById(R.id.iv_image);
            component.tvTitle = convertView.findViewById(R.id.tv_title);
            convertView.setTag(component);
        }else{
            component = (Component) convertView.getTag();
        }

        component.ivImage.setImageResource(Consts.getSubjectIconResId(Consts.getSubjectName(Consts.getSubjectNow())));
        component.tvTitle.setText(mList.get(position).get("name"));
        if (mList.get(position).get("visited") == "yes"){
            component.tvTitle.setTextColor(Color.rgb(168, 168, 168));
        }else{
            component.tvTitle.setTextColor(Color.rgb(0, 0, 0));
        }
        return convertView;
    }
}
