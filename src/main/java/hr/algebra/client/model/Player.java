package hr.algebra.client.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.EnumMap;
import java.util.Map;

public class Player implements Externalizable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Map<ScoreType, Integer> scores = new EnumMap<>(ScoreType.class);

    public Player() {

    }

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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(scores);
        out.writeUTF(name);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        scores = (Map<ScoreType, Integer>) in.readObject();
        name = in.readUTF();
    }
}

