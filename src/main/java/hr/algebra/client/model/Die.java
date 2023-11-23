package hr.algebra.client.model;

import java.util.Random;

public class Die {
    private static int MAXIMUM_FACE_NUMBER = FaceValue.values().length;
    private FaceValue faceValue;
    private boolean selected = false;

    public FaceValue getFaceValue() {
        return faceValue;
    }

    private void setFaceValue(FaceValue faceValue) {
        this.faceValue = faceValue;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void roll() {
        int number = new Random().nextInt(MAXIMUM_FACE_NUMBER);
        FaceValue faceValue = FaceValue.values()[number];
        setFaceValue(faceValue);
    }
}
