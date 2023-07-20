package com.example.myapplication;

public class HistoryItem {
    private String question;
    private String answer;
    private int score;

    public HistoryItem(String question, String answer, int score) {
        this.question = question;
        this.answer = answer;
        this.score = score;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
