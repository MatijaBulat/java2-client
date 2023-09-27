package hr.algebra.client.models;

import java.util.EnumMap;
import java.util.Map;

public class Player {
    private final String name;
    private Map<ScoreType, Integer> scores = new EnumMap<>(ScoreType.class);

    public Player(String name) {
        this.name = name;

        defaultScore();
    }

    /**
     * Get the players score table
     * @return a map of the players current scores
     */
    public Map<ScoreType, Integer> getScores() {
        return scores;
    }

    /**
     * Get the players name
     * @return name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a score for the player. If the score exists already it will he overridden.
     * @param scoreType type of score to set
     * @param score value of the score to set
     */
    public void setScore(ScoreType scoreType, int score) {
        if (scores.containsKey(scoreType)) {
            scores.replace(scoreType, score);
        } else {
            scores.put(scoreType, score);
        }
    }

    /**
     * Resets the score for a player to defaults (0)
     */
    public void resetScore() {
        scores = new EnumMap<>(ScoreType.class);
        defaultScore();
    }

    /**
     * Set the default score
     */
    private void defaultScore() {
        scores.put(ScoreType.SUM, 0);
        scores.put(ScoreType.BONUS, 0);
        scores.put(ScoreType.TOTAL, 0);
    }
}

