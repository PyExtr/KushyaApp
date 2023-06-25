package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_screen);

        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON,
                "{\"topic\":\"disney\",\"previous_questions\":[\"\", \"\"]}");

        Request request = new Request.Builder()
                .url("http://192.168.0.101:5000/generate_question")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Log.d("ErrorD", e.toString()));
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    final String jsonData = response.body().string();

                    String question = "";
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        question = jsonObject.getString("question");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final String finalQuestion = question;
                    runOnUiThread(() -> {
                        Toast.makeText(GameActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        TextView textView = findViewById(R.id.textView);
                        textView.setText(finalQuestion);
                        Log.d("Response", jsonData);
                    });
                }
            }
        });
    }
}
