package com.example.myapplication;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class GameViewModel extends AndroidViewModel {

    private MutableLiveData<Integer> score;
    private MutableLiveData<String> currentQuestion;
    private MutableLiveData<String> fullAnswer;
    private MutableLiveData<Integer> questionCount;
    private MutableLiveData<List<String>> previousQuestions;

    public GameViewModel(@NonNull Application application) {
        super(application);
        score = new MutableLiveData<>();
        currentQuestion = new MutableLiveData<>();
        fullAnswer = new MutableLiveData<>();
        questionCount = new MutableLiveData<>();
        previousQuestions = new MutableLiveData<>();
    }

    public LiveData<Integer> getScore() {
        return score;
    }

    public LiveData<String> getCurrentQuestion() {
        return currentQuestion;
    }

    public LiveData<String> getFullAnswer() {
        return fullAnswer;
    }

    public LiveData<Integer> getQuestionCount() {
        return questionCount;
    }

    public LiveData<List<String>> getPreviousQuestions() {
        return previousQuestions;
    }

    public void setScore(int newScore) {
        score.setValue(newScore);
    }

    public void setCurrentQuestion(String question) {
        currentQuestion.setValue(question);
    }

    public void setFullAnswer(String answer) {
        fullAnswer.setValue(answer);
    }

    public void setQuestionCount(int count) {
        questionCount.setValue(count);
    }

    public void setPreviousQuestions(List<String> questions) {
        previousQuestions.setValue(questions);
    }
}


