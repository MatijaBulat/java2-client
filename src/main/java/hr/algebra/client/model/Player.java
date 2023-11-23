package hr.algebra.client.model;

import java.io.*;
import java.util.EnumMap;
import java.util.Map;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Map<ScoreType, Integer> scores = new EnumMap<>(ScoreType.class);

    public Player() {
        defaultScore();
    }

    public Player(String name) {
        this.name = name;
        defaultScore();
    }

    public Map<ScoreType, Integer> getScores() {
        return scores;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {this.name = name;}

    public void setScore(ScoreType scoreType, int score) {
        if (scores.containsKey(scoreType)) {
            scores.replace(scoreType, score);
        } else {
            scores.put(scoreType, score);
        }
    }

    public void resetScore() {
        scores = new EnumMap<>(ScoreType.class);
        defaultScore();
    }

    private void defaultScore() {
        scores.put(ScoreType.SUM, 0);
        scores.put(ScoreType.BONUS, 0);
        scores.put(ScoreType.TOTAL, 0);
    }

    @Override
    public String toString() {
        return getName() + ": " +  getScores().toString();
    }
}

