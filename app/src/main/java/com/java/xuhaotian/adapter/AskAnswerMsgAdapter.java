package com.java.xuhaotian.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.xuhaotian.R;
import com.java.xuhaotian.AskAnswerMsg;

import java.util.ArrayList;

public class AskAnswerMsgAdapter extends RecyclerView.Adapter<AskAnswerMsgAdapter.AskAnswerMsgHolder> {

    private ArrayList<AskAnswerMsg> list;

    static class AskAnswerMsgHolder extends RecyclerView.ViewHolder {
        private final TextView tvMsgRight;
        private final TextView tvMsgLeft;
        private LinearLayout MsgRight;
        private LinearLayout MsgLeft;

        public AskAnswerMsgHolder(@NonNull View itemView) {
            super(itemView);
            MsgRight = itemView.findViewById(R.id.message_right_layout);
            MsgLeft = itemView.findViewById(R.id.message_left_layout);
            tvMsgRight = itemView.findViewById(R.id.tv_message_right);
            tvMsgLeft = itemView.findViewById(R.id.tv_message_left);
        }
    }

    public AskAnswerMsgAdapter(ArrayList<AskAnswerMsg> list){
        this.list = list;
    }

    @NonNull
    @Override
    public AskAnswerMsgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_ask_answer_message,parent,false);

        return new AskAnswerMsgHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull AskAnswerMsgHolder holder, int position) {
        AskAnswerMsg msg = list.get(position);

        if(msg.getType() == AskAnswerMsg.TYPE_RECEIVE){
            holder.MsgRight.setVisibility(View.GONE);
            holder.MsgLeft.setVisibility(View.VISIBLE);
            holder.tvMsgLeft.setText(msg.getContent());
        }else if(msg.getType() == AskAnswerMsg.TYPE_SEND){
            holder.MsgLeft.setVisibility(View.GONE);
            holder.MsgRight.setVisibility(View.VISIBLE);
            holder.tvMsgRight.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    private AskAnswerMsgAdapter.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(AskAnswerMsgAdapter.OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
