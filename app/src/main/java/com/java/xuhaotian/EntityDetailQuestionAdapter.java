package com.java.xuhaotian;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

public class EntityDetailQuestionAdapter extends BaseAdapter {

    private final List<Question> mData;
    private final Context mContext;
    private final List<String> mSelect;
    private View.OnLongClickListener mShareListener;

    public EntityDetailQuestionAdapter(@NonNull List<Question> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
        this.mSelect = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            mSelect.add("");
        }
    }

    public void setShareListener(View.OnLongClickListener mShareListener) {
        this.mShareListener = mShareListener;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.entity_detail_question_list_item, parent,false);
            holder = new ViewHolder();
            holder.tv_body = convertView.findViewById(R.id.tv_entity_detail_question_list_item_body);
            holder.rdoGroup = convertView.findViewById(R.id.rdoGroup_entity_detail_question_list_item);
            holder.rdoBtn_a = convertView.findViewById(R.id.rdoBtn_entity_detail_question_list_item_a);
            holder.rdoBtn_b = convertView.findViewById(R.id.rdoBtn_entity_detail_question_list_item_b);
            holder.rdoBtn_c = convertView.findViewById(R.id.rdoBtn_entity_detail_question_list_item_c);
            holder.rdoBtn_d = convertView.findViewById(R.id.rdoBtn_entity_detail_question_list_item_d);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.rdoGroup.setOnCheckedChangeListener(null);

        String tips;
        if (mSelect.get(position).isEmpty()) tips = "";
        else if (mSelect.get(position).equals(mData.get(position).getQAnswer())) tips = "<font color='#00FF00'>回答正确</font>";
        else tips = "<font color='#FF0000'>回答错误</font>";
        switch (mSelect.get(position)) {
            case "A":
                holder.rdoBtn_a.setChecked(true);
                break;
            case "B":
                holder.rdoBtn_b.setChecked(true);
                break;
            case "C":
                holder.rdoBtn_c.setChecked(true);
                break;
            case "D":
                holder.rdoBtn_d.setChecked(true);
                break;
            default:
                holder.rdoGroup.clearCheck();
        }
        holder.tv_body.setText(Html.fromHtml(mData.get(position).getQBody() + tips, Html.FROM_HTML_MODE_LEGACY));
        holder.tv_body.setOnLongClickListener(mShareListener);
        holder.tv_body.setTag(position);
        holder.rdoBtn_a.setText(mData.get(position).getA());
        holder.rdoBtn_b.setText(mData.get(position).getB());
        holder.rdoBtn_c.setText(mData.get(position).getC());
        holder.rdoBtn_d.setText(mData.get(position).getD());
        holder.rdoGroup.setTag(position);

        holder.rdoGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int position1 = Integer.parseInt(group.getTag().toString());
            mSelect.set(position1, getOption(checkedId));
            this.notifyDataSetChanged();
        });

        return convertView;
    }

    static class ViewHolder{
        private TextView tv_body;
        private RadioButton rdoBtn_a, rdoBtn_b, rdoBtn_c, rdoBtn_d;
        private RadioGroup rdoGroup;
    }

    @NonNull
    @Contract(pure = true)
    static String getOption(int id) {
        if (id == R.id.rdoBtn_entity_detail_question_list_item_a) return "A";
        if (id == R.id.rdoBtn_entity_detail_question_list_item_b) return "B";
        if (id == R.id.rdoBtn_entity_detail_question_list_item_c) return "C";
        if (id == R.id.rdoBtn_entity_detail_question_list_item_d) return "D";
        return "";
    }
}
