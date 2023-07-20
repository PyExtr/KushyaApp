package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<HistoryItem> historyList;

    public HistoryAdapter(List<HistoryItem> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem historyItem = historyList.get(position);
        holder.questionView.setText(historyItem.getQuestion());
        holder.answerView.setText(historyItem.getAnswer());
        holder.scoreView.setText(String.valueOf(historyItem.getScore()));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    // Your HistoryViewHolder class should have TextViews for question, answer, and score
    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView questionView;
        private final TextView answerView;
        private final TextView scoreView;

        public HistoryViewHolder(View view) {
            super(view);
            questionView = view.findViewById(R.id.question_text_view);
            answerView = view.findViewById(R.id.answer_text_view);
            scoreView = view.findViewById(R.id.score_text_view);
        }
    }
}
