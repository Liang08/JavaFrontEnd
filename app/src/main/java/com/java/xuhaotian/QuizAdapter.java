package com.java.xuhaotian;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Contract;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {

    private final List<Question> mData;
    private final Context mContext;
    private final List<String> mSelect;
    private boolean enable;

    public QuizAdapter(List<Question> mData, Context mContext, List<String> mSelect) {
        this.mData = mData;
        this.mContext = mContext;
        this.mSelect = mSelect;
        this.enable = true;
    }

    public void disable() {
        enable = false;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.quiz_question_list_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question question = mData.get(position);

        holder.tv_head.setText("第 " + (position + 1) + " 题，共 " + mData.size() + " 题");
        holder.rdoGroup.setOnCheckedChangeListener(null);
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
        holder.rdoBtn_a.setText(question.getA());
        holder.rdoBtn_b.setText(question.getB());
        holder.rdoBtn_c.setText(question.getC());
        holder.rdoBtn_d.setText(question.getD());

        if (enable) {
            holder.rdoGroup.setTag(position);
            holder.tv_body.setText(Html.fromHtml(question.getQBody(), Html.FROM_HTML_MODE_LEGACY));
            holder.tv_body.setTag(position);

            holder.rdoGroup.setOnCheckedChangeListener((group, checkedId) -> {
                int position1 = Integer.parseInt(group.getTag().toString());
                mSelect.set(position1, getOption(checkedId));
            });
        }
        else {
            String tips;
            if (mSelect.get(position).isEmpty()) tips = "<font color='#FF0000'>未作答 </font><font color='#00FF00'>正确答案：" + question.getQAnswer() + "</font>";
            else if (mSelect.get(position).equals(question.getQAnswer())) tips = "<font color='#00FF00'>回答正确</font>";
            else tips = "<font color='#FF0000'>回答错误 </font><font color='#00FF00'>正确答案：" + question.getQAnswer() + "</font>";
            holder.tv_body.setText(Html.fromHtml(question.getQBody() + tips, Html.FROM_HTML_MODE_LEGACY));
            holder.rdoBtn_a.setEnabled(false);
            holder.rdoBtn_b.setEnabled(false);
            holder.rdoBtn_c.setEnabled(false);
            holder.rdoBtn_d.setEnabled(false);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_head, tv_body;
        private final RadioButton rdoBtn_a, rdoBtn_b, rdoBtn_c, rdoBtn_d;
        private final RadioGroup rdoGroup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_head = itemView.findViewById(R.id.tv_quiz_question_list_item_head);
            tv_body = itemView.findViewById(R.id.tv_quiz_question_list_item_body);
            rdoGroup = itemView.findViewById(R.id.rdoGroup_quiz_question_list_item);
            rdoBtn_a = itemView.findViewById(R.id.rdoBtn_quiz_question_list_item_a);
            rdoBtn_b = itemView.findViewById(R.id.rdoBtn_quiz_question_list_item_b);
            rdoBtn_c = itemView.findViewById(R.id.rdoBtn_quiz_question_list_item_c);
            rdoBtn_d = itemView.findViewById(R.id.rdoBtn_quiz_question_list_item_d);
        }
    }

    @NonNull
    @Contract(pure = true)
    static String getOption(int id) {
        if (id == R.id.rdoBtn_quiz_question_list_item_a) return "A";
        if (id == R.id.rdoBtn_quiz_question_list_item_b) return "B";
        if (id == R.id.rdoBtn_quiz_question_list_item_c) return "C";
        if (id == R.id.rdoBtn_quiz_question_list_item_d) return "D";
        return "";
    }
}
