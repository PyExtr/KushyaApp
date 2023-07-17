package com.example.myapplication;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class GameViewModel extends AndroidViewModel {
    private MutableLiveData<String> currentQuestion = new MutableLiveData<>();
    private MutableLiveData<String> fullAnswer = new MutableLiveData<>();
    private MutableLiveData<Integer> score = new MutableLiveData<>();
    private MutableLiveData<Integer> questionCount = new MutableLiveData<>();
    private List<String> previousQuestions = new ArrayList<>();

    public GameViewModel(Application application) {
        super(application);
        score.setValue(0);   // initialize score
        questionCount.setValue(0);   // initialize question count
    }

    public MutableLiveData<String> getCurrentQuestion() {
        return currentQuestion;
    }

    public MutableLiveData<String> getFullAnswer() {
        return fullAnswer;
    }

    public MutableLiveData<Integer> getScore() {
        return score;
    }

    public MutableLiveData<Integer> getQuestionCount() {
        return questionCount;
    }

    public List<String> getPreviousQuestions() {
        return previousQuestions;
    }

    public void setCurrentQuestion(String question) {
        this.currentQuestion.setValue(question);
    }

    public void setFullAnswer(String fullAnswer) {
        this.fullAnswer.setValue(fullAnswer);
    }

    public void incrementScore() {
        score.setValue(score.getValue() + 1);
    }

    public void incrementQuestionCount() {
        questionCount.setValue(questionCount.getValue() + 1);
    }

    public void addPreviousQuestion(String question) {
        this.previousQuestions.add(question);
    }
}
