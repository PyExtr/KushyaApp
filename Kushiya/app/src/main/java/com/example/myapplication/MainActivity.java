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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        //Create the request body
        RequestBody body = RequestBody.create(JSON,
                "{\"topic\":\"disney\",\"previous_questions\":[\"\", \"\"]}");

        Request request = new Request.Builder()  //Build the request
                .url("http://192.168.0.101:5000/generate_question")
                .post(body)
                .build();

        //Log the request for debugging
        Log.d("Request", request.toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Log the e.error for debugging
                        Log.d("ErrorD", e.toString());
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    // Do something with the response.
                    final String jsonData = response.body().string();

                    // Parse JSON and extract the question.
                    String question = "";
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        question = jsonObject.getString("question");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final String finalQuestion = question;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast the response for debugging
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            TextView textView = findViewById(R.id.text);
                            //set the question to the textview
                            textView.setText(finalQuestion);
                            //Log the response for debugging
                            Log.d("Response", jsonData.toString());
                        }
                    });
                }
            }
        });

        }
}
