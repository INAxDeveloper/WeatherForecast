package com.inaxdevelopers.weatherforecast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inaxdevelopers.weatherforecast.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    Context context;
    ArrayList<String> allTexts;

    public HistoryAdapter(Context context, ArrayList<String> allTexts) {
        this.allTexts = allTexts;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        holder.historyText.setText(allTexts.get(position));
    }

    @Override
    public int getItemCount() {
        return allTexts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView historyText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            historyText = itemView.findViewById(R.id.historyText);
        }
    }
}
