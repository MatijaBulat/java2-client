package hr.algebra.client.model;

public class Dice {
    private Die[] dice = new Die[5];

    public Dice() {
        for (int i = 0; i < dice.length; i++) {
            dice[i] = new Die();
        }
    }

    public Die getDie(int index) {
        return dice[index];
    }

    public Die[] getDice() {
        return dice;
    }

    public void roll() {
        for (Die die : dice) {
            if (!die.isSelected()) {
                die.roll();
            }
        }
    }

    public FaceValue[] getFaceValues() {
        FaceValue[] faceValues = new FaceValue[dice.length];
        for (int i = 0; i < faceValues.length; i++) {
            faceValues[i] = dice[i].getFaceValue();
        }
        return faceValues;
    }
}