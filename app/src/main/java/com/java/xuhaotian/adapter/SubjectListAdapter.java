package com.java.xuhaotian.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.xuhaotian.R;

import java.util.ArrayList;

public class SubjectListAdapter extends RecyclerView.Adapter<SubjectListAdapter.SubjectListHolder> {

    private Context context;
    private ArrayList<String> list;

    public SubjectListAdapter(Context context, ArrayList<String> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SubjectListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubjectListHolder(LayoutInflater.from(context).inflate(R.layout.recyclerview_subject, parent, false));
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull SubjectListHolder holder, int position) {
        holder.tvSubject.setText(list.get(position));
        if(mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SubjectListHolder extends RecyclerView.ViewHolder {
        private TextView tvSubject;

        public SubjectListHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tv_subject);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    private SubjectListAdapter.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(SubjectListAdapter.OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
