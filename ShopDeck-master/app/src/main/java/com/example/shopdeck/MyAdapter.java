package com.example.shopdeck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopdeck.Model.FeedbackM;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    ArrayList<FeedbackM> mList;
    Context context;

    public MyAdapter(Context context, ArrayList<FeedbackM> mList){
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.feedbackblock, parent, false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FeedbackM model = mList.get(position);
        holder.phone.setText(model.getUser());
        holder.feedback.setText(model.getFeedback());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{
                        "Remove"
                };

            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView phone, feedback;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            phone = itemView.findViewById(R.id.phonetv);
            feedback = itemView.findViewById(R.id.feedbacktv);
        }
    }
}
