package com.example.myapplication;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.myapplication.R;

public class HistoryDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        View view = inflater.inflate(R.layout.dialog_history, null);

        TextView textView = view.findViewById(R.id.history_text_view);

        StringBuilder stringBuilder = new StringBuilder();
        for (String historyItem : GameActivity.historyList) {
            stringBuilder.append(historyItem);
            stringBuilder.append("\n\n");
        }

        textView.setText(stringBuilder.toString());

        builder.setView(view)
                .setTitle("Game History")
                .setPositiveButton("OK", (dialog, id) -> {
                    // User clicked OK button
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

