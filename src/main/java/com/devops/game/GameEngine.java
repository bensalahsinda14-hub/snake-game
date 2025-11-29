package com.devops.game;

public class GameEngine {
    
    private static final String VERSION = "1.0.0";
    private int highScore;

    public GameEngine() {
        this.highScore = 0;
    }

    public String getVersion() {
        return VERSION;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int score) {
        if (score > this.highScore) {
            this.highScore = score;
        }
    }

    public boolean isValidScore(int score) {
        return score >= 0;
    }

    public int calculateLevel(int score) {
        return score < 0 ? 0 : (score / 10) + 1;
    }
}
