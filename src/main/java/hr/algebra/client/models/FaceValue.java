package hr.algebra.client.models;

public enum FaceValue {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6);

    private int value;

    FaceValue(int value) {
        this.value = value;
    }

    /**
     * @return the value of a face value
     */
    public int getValue() {
        return value;
    }
}