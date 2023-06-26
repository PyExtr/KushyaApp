package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;

public class GameActivity extends AppCompatActivity {

    private TextView textViewQuestion, scoreText, currentQuestionNumber;  // TextViews
    private EditText userAnswer;  // EditText
    private ImageButton submitBtn, homeBtn;  // Buttons
    private String fullAnswer = "";
    private int score = 0;
    private int questionCount = 0;
    private String topic;
    private List<String> previousQuestions = new ArrayList<>();
    private ProgressBar progressBar;
    public static List<String> historyList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_screen);


        // Initialize UI elements
        textViewQuestion = findViewById(R.id.textViewQuestion);
        userAnswer = findViewById(R.id.userAnswer);
        submitBtn = findViewById(R.id.submitBtn);
        userAnswer.setText("");
        userAnswer.setHint("Enter your answer here");
        scoreText = findViewById(R.id.scoreText);
        homeBtn = findViewById(R.id.homeBtn);
        currentQuestionNumber = findViewById(R.id.currentQuestionNumber);
        progressBar = findViewById(R.id.progress_bar);


        // Setup button actions
        submitBtn.setEnabled(true);
        submitBtn.setClickable(true);


        final EditText input = createTopicInput();  // Create topic input

        submitBtn.setOnClickListener(view -> submitAnswer());  // Submit button
        homeBtn.setOnClickListener(view -> goHome());  // Home button

        new AlertDialog.Builder(this)  // Create dialog box for topic input
                .setTitle("Choose a Topic")
                .setMessage("Please enter the topic you want to play.")
                .setView(input)
                .setPositiveButton("Start", (dialog, which) -> {
                    topic = input.getText().toString();
                    startGame();
                })
                .show();

    }


    private EditText createTopicInput() {  // Create topic input
        final EditText input = new EditText(this);
        input.setHint("Enter a topic");
        return input;
    }

    private void startGame() {  // Start game
        generateQuestion(topic);
    }

    private void generateQuestion(String topic) {  // Generate question
        runOnUiThread(() -> {
            textViewQuestion.setText("Loading...");
            userAnswer.setText("");
            progressBar.setVisibility(View.VISIBLE);  // Show progress bar
        });

        if (questionCount >= 20) {  // If 20 questions have been asked, end game
            runOnUiThread(this::endGame);
            return;
        }

        OkHttpClient client = new OkHttpClient();  // Create HTTP client
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");  // Create JSON media type

        JSONArray previousQuestionsJson = new JSONArray(previousQuestions);  // Create JSON array of previous questions
        JSONObject jsonObject = new JSONObject();  // Create JSON object
        try {  // Add data to JSON object
            jsonObject.put("topic", topic);
            jsonObject.put("previous_questions", previousQuestionsJson);
        } catch (JSONException e) {  // Catch JSON exception
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());  // Create request body

        Request request = new Request.Builder()  // Create request
                .url("http://192.168.0.101:5000/generate_question")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> progressBar.setVisibility(View.GONE)); // Hide progress bar
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
                        fullAnswer = jsonObject.getString("full_answer");
                        previousQuestions.add(question);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String finalQuestion = question;
                    runOnUiThread(() -> {
                        Toast.makeText(GameActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        textViewQuestion.setText(finalQuestion);
                        questionCount++;
                        currentQuestionNumber.setText(String.valueOf(questionCount));
                        progressBar.setVisibility(View.GONE); // Hide progress bar
                    });
                }
            }
        });
    }


    private void submitAnswer() {  // Submit answer
        String userAnswerStr = userAnswer.getText().toString();  // Get user answer
        String previousQuestion = "";  // Initialize previous question
        // If there are previous questions and the question count is less than the number of previous questions, get previous question
        if (!previousQuestions.isEmpty() && questionCount - 1 < previousQuestions.size()) {
            previousQuestion = previousQuestions.get(questionCount - 1);
        }

        OkHttpClient client = new OkHttpClient();  // Create HTTP client
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");  // Create JSON media type

        JSONObject jsonObject = new JSONObject();  // Create JSON object
        try {  // Add data to JSON object
            jsonObject.put("full_answer", fullAnswer);
            jsonObject.put("user_answer", userAnswerStr);
            jsonObject.put("question", previousQuestion);
        } catch (JSONException e) {  // Catch JSON exception
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());  // Create request body

        Request request = new Request.Builder()  // Create request
                .url("http://192.168.0.101:5000/evaluate_answer")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {  // Send request
            @Override
            public void onFailure(Call call, IOException e) {  // If request fails, print stack trace
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {  // If response is not successful, throw exception
                    throw new IOException("Unexpected code " + response);
                } else {  // If response is successful, get JSON data
                    final String jsonData = response.body().string();

                    int scoreFromServer = 0;
                    try {  // Get score from JSON data
                        JSONObject jsonObject = new JSONObject(jsonData);
                        scoreFromServer = jsonObject.getInt("score");
                    } catch (JSONException e) {  // Catch JSON exception
                        e.printStackTrace();
                    }

                    if (scoreFromServer > 0) {  // If score is greater than 0, increment score and update UI
                        score++;
                        runOnUiThread(() -> {  // Update UI
                            scoreText.setText(String.valueOf(score));
                            currentQuestionNumber.setText(String.valueOf(score + 1));
                            Toast.makeText(GameActivity.this, "Correct Answer!", Toast.LENGTH_SHORT).show();
                            showResultDialog(userAnswerStr, fullAnswer, true);
                        });
                    } else {  // If score is less than or equal to 0, update UI
                        runOnUiThread(() -> {  // Update UI
                            Toast.makeText(GameActivity.this, "Wrong Answer!", Toast.LENGTH_SHORT).show();
                            showResultDialog(userAnswerStr, fullAnswer, false);
                        });
                    }

                    // Generate the next question
                    startGame();
                }
            }
        });
    }


    private void showResultDialog(String userAnswerStr, String aiAnswerStr, boolean isCorrect) { // Show result dialog
        String previousQuestion = "N/A";  // Initialize previous question
        // If there are previous questions and the question count is less than the number of previous questions, get previous question
        if (!previousQuestions.isEmpty() && questionCount - 1 < previousQuestions.size()) {
            previousQuestion = previousQuestions.get(questionCount - 1);
        }
        String historyItem = "Question " + questionCount +": " + previousQuestion +
                "\nYour answer: " + userAnswerStr +
                "\nAI's " + aiAnswerStr +
                "\nYour score: " + score;
        historyList.add(historyItem); // add to history list

        // Write history to file
        writeToFile(historyItem, this);

        AlertDialog dialog = new AlertDialog.Builder(this) // Create alert dialog
                .setTitle(isCorrect ? "Correct!" : "Incorrect")
                .setMessage("Question: " + previousQuestion +
                        "\nYour answer: " + userAnswerStr +
                        "\nAI's " + aiAnswerStr +
                        "\nTotal score: " + (int) (isCorrect ? score - 1 : score) + (String) (isCorrect ? " + 1" : ""))
                .setPositiveButton("OK", null)
                .create();
        dialog.show(); // Show alert dialog
    }




    private void goHome() { // Go home screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private void endGame() {
        // Show an alert dialog with the final score and a replay option
        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Your final score is " + score + ".\nDo you want to play again?")
                .setPositiveButton("Yes", (dialog, which) -> restartGame())
                .setNegativeButton("No", (dialog, which) -> goHome())
                .show();
    }

    private void restartGame() {
        // Reset the score and question count and start a new game
        score = 0;
        questionCount = 0;
        previousQuestions.clear();  // clear previous questions
        scoreText.setText(String.valueOf(score));
        currentQuestionNumber.setText(String.valueOf(questionCount + 1));
        startGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // Create menu
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.game_options_menu, menu); // Inflate menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // Menu options
        if (item.getItemId() == R.id.menu_restart_game) { // Restart game
            restartGame();
            return true;
        } else if (item.getItemId() == R.id.menu_view_history) { // View history
            HistoryDialogFragment historyDialog = new HistoryDialogFragment(); // Create history dialog
            historyDialog.show(getSupportFragmentManager(), "historyDialog"); // Show history dialog
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void writeToFile(String data, Context context) {  // write to file
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("game_history.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    private String readFromFile(Context context) { // read from file NOT USED FOR NOW
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("game_history.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }





}
