package hr.algebra.client.models;

import java.util.Random;

public class Die {
    private static int MAXIMUM_FACE_NUMBER = FaceValue.values().length;

    private FaceValue faceValue;
    private boolean selected = false;

    /**
     * @return the current face value of the die
     */
    public FaceValue getFaceValue() {
        return faceValue;
    }

    /**
     * Sets the current face value of the die
     * @param faceValue to set the die to
     */
    private void setFaceValue(FaceValue faceValue) {
        this.faceValue = faceValue;
    }

    /**
     * @return the current selected state of the die
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Selects the die. Used to 'keep' a die from being rerolled.
     * @param selected the state of the die
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Simulates throwing the die. Sets a random face value on the die.
     */
    public void roll() {
        int number = new Random().nextInt(MAXIMUM_FACE_NUMBER);
        FaceValue faceValue = FaceValue.values()[number];
        setFaceValue(faceValue);
    }
}
