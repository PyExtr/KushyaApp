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
    private MutableLiveData<String> currentQuestion;
    private MutableLiveData<String> currentAnswer;
    private MutableLiveData<Integer> currentScore;

    public GameViewModel(@NonNull Application application) {
        super(application);
        currentQuestion = new MutableLiveData<>();
        currentAnswer = new MutableLiveData<>();
        currentScore = new MutableLiveData<>();
    }

    public LiveData<String> getCurrentQuestion() {
        return currentQuestion;
    }

    public LiveData<String> getCurrentAnswer() {
        return currentAnswer;
    }

    public LiveData<Integer> getCurrentScore() {
        return currentScore;
    }

    public void setCurrentQuestion(String question) {
        currentQuestion.postValue(question);
    }

    public void setCurrentAnswer(String answer) {
        currentAnswer.postValue(answer);
    }

    public void setCurrentScore(Integer score) {
        currentScore.postValue(score);
    }
}



