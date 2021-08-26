package com.java.xuhaotian;

import android.content.Context;
import android.net.sip.SipSession;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class EntityDetailContentAdapter extends BaseExpandableListAdapter {

    private final List<String> mGroup;
    private final List<List<Pair<String, String>>> mItem;
    private final Context mContext;
    private View.OnClickListener mItemListener = null;

    public EntityDetailContentAdapter(List<String> mGroup, List<List<Pair<String, String>>> mItem, Context mContext) {
        this.mGroup = mGroup;
        this.mItem = mItem;
        this.mContext = mContext;
    }

    public void setItemListener(View.OnClickListener mItemListener) {
        this.mItemListener = mItemListener;
    }

    @Override
    public int getGroupCount() { return mGroup.size(); }

    @Override
    public int getChildrenCount(int groupPosition) { return mItem.get(groupPosition).size(); }

    @Override
    public Object getGroup(int groupPosition) { return null; }

    @Override
    public Object getChild(int groupPosition, int childPosition) { return null; }

    @Override
    public long getGroupId(int groupPosition) { return groupPosition; }

    @Override
    public long getChildId(int groupPosition, int childPosition) { return childPosition; }

    @Override
    public boolean hasStableIds() { return true; }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderGroup groupHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.entity_detail_content_list_group, parent,false);
            groupHolder = new ViewHolderGroup();
            groupHolder.tv_name = convertView.findViewById(R.id.tv_entity_detail_content_list_group_name);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (ViewHolderGroup) convertView.getTag();
        }
        groupHolder.tv_name.setText(mGroup.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderItem itemHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.entity_detail_content_list_item, parent,false);
            itemHolder = new ViewHolderItem();
            itemHolder.tv_name = convertView.findViewById(R.id.tv_entity_detail_content_list_item_name);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ViewHolderItem) convertView.getTag();
        }
        itemHolder.tv_name.setText(mItem.get(groupPosition).get(childPosition).second);
        itemHolder.tv_name.setTag(mItem.get(groupPosition).get(childPosition));
        // itemHolder.tv_name.setOnClickListener(mItemListener);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class ViewHolderGroup{
        private TextView tv_name;
    }

    static class ViewHolderItem{
        private TextView tv_name;
    }
}
