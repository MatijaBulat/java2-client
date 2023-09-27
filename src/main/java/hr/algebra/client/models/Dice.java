package hr.algebra.client.models;

public class Dice {
    private Die[] dice = new Die[5];

    /**
     * Constructor
     */
    public Dice() {
        for (int i = 0; i < dice.length; i++) {
            dice[i] = new Die();
        }
    }

    /**
     * Get one of the dice
     * @param index of the die
     * @return a Die
     */
    public Die getDie(int index) {
        return dice[index];
    }

    /**
     * Get all of the dice
     * @return an array of Die
     */
    public Die[] getDice() {
        return dice;
    }

    /**
     * Roll (or re-roll) all unselected dice
     * This is the main method to use for simulating "throwing" dice
     */
    public void roll() {
        for (Die die : dice) {
            if (!die.isSelected()) {
                die.roll();
            }
        }
    }

    /**
     * Get the current face value of all the dice. If dice is not thrown yet
     * they will be 'null'.
     * @return an array of FaceValues
     */
    public FaceValue[] getFaceValues() {
        FaceValue[] faceValues = new FaceValue[dice.length];
        for (int i = 0; i < faceValues.length; i++) {
            faceValues[i] = dice[i].getFaceValue();
        }
        return faceValues;
    }
}