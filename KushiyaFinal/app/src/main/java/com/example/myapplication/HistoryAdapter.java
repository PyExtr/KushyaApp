package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// inner class - who tell the recycler view who his adapter
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<HistoryItem> historyList;  // reference to our list

    public HistoryAdapter(List<HistoryItem> historyList) {
        historyList = historyList;
    }

    // מחזירה מופע של המחלקה הפנימית שלי
    // מציג את כמות הפריטים שנכנסים בחלון
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.BindData(position);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    // Your HistoryViewHolder class should have TextViews for question, answer, and score
    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView questionView;
        private final TextView answerView;
        private final TextView scoreView;

        public HistoryViewHolder(View view) {
            super(view);
            questionView = view.findViewById(R.id.question_text_view);
            answerView = view.findViewById(R.id.answer_text_view);
            scoreView = view.findViewById(R.id.score_text_view);
        }

        //Fill data for each item
        public void BindData(int position) {
            HistoryItem historyItem = historyList.get(position);
            questionView.setText(historyItem.getQuestion());
            answerView.setText(historyItem.getAnswer());
            scoreView.setText(String.valueOf(historyItem.getScore()));
        }
    }
}
