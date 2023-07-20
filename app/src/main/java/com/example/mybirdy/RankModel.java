package com.example.mybirdy;

public class RankModel {
    private int rank;
    private String name;
    private String highScore;

    public RankModel(int rank, String name, String highScore) {
        this.rank = rank;
        this.name = name;
        this.highScore = highScore;
    }

    public int getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public String getHighScore() {
        return highScore;
    }

    public void setHighScore(String highScore) {
        this.highScore = highScore;
    }
}

