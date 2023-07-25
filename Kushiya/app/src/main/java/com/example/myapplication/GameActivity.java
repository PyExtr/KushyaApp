package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import okhttp3.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;

public class GameActivity extends AppCompatActivity {

    private TextView textViewQuestion, scoreText, currentQuestionNumber, timerTextView;  // TextViews
    private EditText userAnswer;
    private ImageButton submitBtn, homeBtn;  // Buttons
    private String fullAnswer = "", topic;
    private int score = 0, questionCount = 0;
    private List<String> previousQuestions = new ArrayList<>();
    private ProgressBar progressBar;
    public static List<String> historyList = new ArrayList<>();
    private long startTime;
    private boolean timerRunning;
    private GameViewModel gameViewModel;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_screen);

        // timerTextView = findViewById(R.id.textClock);
        // Start the timer when the activity is created
        startTimer();

        // Initialize the GameViewModel
        gameViewModel = new ViewModelProvider((ViewModelStoreOwner) this,
                (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory
                        .getInstance(getApplication())).get(GameViewModel.class);


        gameViewModel.getCurrentQuestion().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String newFullAnswer) {
                // create a toast when new question is generated
                Toast.makeText(GameActivity.this, "New question generated", Toast.LENGTH_SHORT).show();
            }
        });

        gameViewModel.getCurrentAnswer().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String answer) {
                // no function
            }
        });

        gameViewModel.getCurrentScore().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer score) {
                // no function
            }
        });

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

        sharedPreferences = getSharedPreferences("save_topic_game", Context.MODE_PRIVATE);

        // Setup button actions
        submitBtn.setEnabled(true);
        submitBtn.setClickable(true);

        submitBtn.setOnClickListener(view -> submitAnswer());  // Submit button
        homeBtn.setOnClickListener(view -> goHome());  // Home button
        final EditText input = new EditText(this);  // Create topic input
        input.setHint("Enter a topic");

        new AlertDialog.Builder(this)  // Create dialog box for topic input
                .setTitle("Choose a Topic").setMessage("Please enter the topic you want to play.").setView(input).setPositiveButton("Start", (dialog, which) -> {
                    topic = input.getText().toString();
                    saveTopicSP(topic);
                    startGame();
                }).show();
    }

    public void startTimer() {
//        startTime = SystemClock.elapsedRealtime();
//        timerRunning = true;
       // timerTextView.setText((int) startTime);
    }


    ////SP//////
    public void saveTopicSP(String topic) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(topic, "");
        editor.apply();
    }

    public void clearTopicSP() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private EditText createTopicInput() {  // Create topic input
        final EditText input = new EditText(this);
        input.setHint("Enter a topic");
        return input;
    }

    protected void startGame() {  // Start game
        generateQuestion(topic);
    }

    private void generateQuestion(String topic) {  // Generate question
        String newQuestion = "Your generated question"; // Placeholder question

        runOnUiThread(() -> {  // Run on UI thread, or else app will crash
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
                        gameViewModel.setCurrentQuestion(finalQuestion); // Set current question for the observer
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
        if (userAnswer.getText().toString().isEmpty()) {  // If user answer is empty, show toast
            Toast.makeText(this, "Please enter an answer", Toast.LENGTH_SHORT).show();
            return;
        }
        gameViewModel.setCurrentAnswer(userAnswer.getText().toString());  // Set current answer for the observer
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
                            gameViewModel.setCurrentScore(score);  // Set score for the observer
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
        String historyItem = "Question " + questionCount + ": " + previousQuestion + "\nYour answer: " + userAnswerStr + "\nAI's " + aiAnswerStr + "\nYour score: " + score;
        historyList.add(historyItem); // add to history list

        // Write history to file
        writeToFile(historyItem, this);

        AlertDialog dialog = new AlertDialog.Builder(this) // Create alert dialog
                .setTitle(isCorrect ? "Correct!" : "Incorrect").setMessage("Question: " + previousQuestion + "\nYour answer: " + userAnswerStr + "\nAI's " + aiAnswerStr + "\nTotal score: " + (int) (isCorrect ? score - 1 : score) + (String) (isCorrect ? " + 1" : "")).setPositiveButton("OK", null).create();
        dialog.show(); // Show alert dialog
    }

    private void goHome() { // Go home screen
        finish();
    }

    private void endGame() {
        clearTopicSP();
        // Show an alert dialog with the final score and a replay option
        new AlertDialog.Builder(this).setTitle("Game Over").setMessage("Your final score is " + score + ".\nDo you want to play again?").setPositiveButton("Yes", (dialog, which) -> restartGame()).setNegativeButton("No", (dialog, which) -> goHome()).show();
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
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("game_history.txt", Context.MODE_APPEND));
            outputStreamWriter.write(data + "\n"); // Append a newline character to separate entries
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    private String readFromFile(Context context) { // read from file NOT USED FOR NOW
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("game_history.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }


}
