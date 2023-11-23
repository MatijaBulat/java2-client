package hr.algebra.client.utils;

import hr.algebra.client.model.FaceValue;
import hr.algebra.client.model.ScoreType;

import java.util.EnumMap;
import java.util.Map;

public class ScoreUtil {
    private final FaceValue[] faceValues;

    public ScoreUtil(FaceValue[] faceValues) {
        this.faceValues = faceValues;
    }

    public Map<ScoreType, Integer> scoreMap() {
        Map<ScoreType, Integer> scores = new EnumMap<>(ScoreType.class);
        scores.put(ScoreType.ONES, ones());
        scores.put(ScoreType.TWOS, twos());
        scores.put(ScoreType.THREES, threes());
        scores.put(ScoreType.FOURS, fours());
        scores.put(ScoreType.FIVES, fives());
        scores.put(ScoreType.SIXES, sixes());
        scores.put(ScoreType.ONE_PAIR, onePair());
        scores.put(ScoreType.TWO_PAIR, twoPair());
        scores.put(ScoreType.THREE_OF_A_KIND, threeOfAKind());
        scores.put(ScoreType.FOUR_OF_A_KIND, fourOfAKind());
        scores.put(ScoreType.SMALL_STRAIGHT, smallStraight());
        scores.put(ScoreType.LARGE_STRAIGHT, largeStraight());
        scores.put(ScoreType.FULL_HOUSE, fullHouse());
        scores.put(ScoreType.CHANCE, chance());
        scores.put(ScoreType.YATZY, yatzy());

        return scores;
    }

    public int ones() {
        return sumOfSingle(FaceValue.ONE);
    }

    public int twos() {
        return sumOfSingle(FaceValue.TWO);
    }

    public int threes() {
        return sumOfSingle(FaceValue.THREE);
    }

    public int fours() {
        return sumOfSingle(FaceValue.FOUR);
    }

    public int fives() {
        return sumOfSingle(FaceValue.FIVE);
    }

    public int sixes() {
        return sumOfSingle(FaceValue.SIX);
    }

    public int onePair() {
        return sumOfAKind(2);
    }

    public int twoPair() {
        Map<FaceValue, Integer> frequencies = frequency();
        int minFrequency = 2;

        FaceValue firstPair = highestRepeated(minFrequency);
        FaceValue secondPair = null;

        for (FaceValue faceValue : frequencies.keySet()) {
            if (faceValue == firstPair) {
                continue;
            }

            if (frequencies.get(faceValue) < minFrequency) {
                continue;
            }

            if (secondPair == null || faceValue.getValue() > secondPair.getValue()) {
                secondPair = faceValue;
            }
        }

        if (firstPair == null || secondPair == null) {
            return 0;
        } else {
            return firstPair.getValue() * minFrequency + secondPair.getValue() * minFrequency;
        }
    }

    public int threeOfAKind() {
        return sumOfAKind(3);
    }

    public int fourOfAKind() {
        return sumOfAKind(4);
    }

    public int smallStraight() {
        Map<FaceValue, Integer> frequencies = frequency();

        for (FaceValue faceValue : frequencies.keySet()) {
            if (faceValue == FaceValue.SIX) {
                return 0;
            } else if (frequencies.get(faceValue) > 1) {
                return 0;
            }
        }
        return 15;
    }

    public int largeStraight() {
        Map<FaceValue, Integer> frequencies = frequency();

        for (FaceValue faceValue : frequencies.keySet()) {
            if (faceValue == FaceValue.ONE) {
                return 0;
            } else if (frequencies.get(faceValue) > 1) {
                return 0;
            }
        }
        return 20;
    }

    public int fullHouse() {
        Map<FaceValue, Integer> frequencies = frequency();
        boolean pair = false;

        for (FaceValue faceValue : frequencies.keySet()) {
            if (frequencies.get(faceValue) == 2) {
                pair = true;
            }
        }
        return pair && frequencies.size() == 2 ? sum() : 0;
    }

    public int chance() {
        return sum();
    }

    public int yatzy() {
        Map<FaceValue, Integer> frequencies = frequency();
        return frequencies.size() == 1 ? 50 : 0;
    }

    private int sum() {
        int sum = 0;
        for (FaceValue faceValue : faceValues) {
            sum += faceValue.getValue();
        }
        return sum;
    }

    private int sumOfAKind(int frequency) {
        FaceValue faceValue = highestRepeated(frequency);
        return faceValue != null ? faceValue.getValue() * frequency : 0;
    }

    private int sumOfSingle(FaceValue faceValue) {
        int sum = 0;
        for (FaceValue face : faceValues) {
            if (face == faceValue) {
                sum += face.getValue();
            }
        }
        return sum;
    }

    private Map<FaceValue, Integer> frequency() {
        Map<FaceValue, Integer> frequencies = new EnumMap<>(FaceValue.class);

        for (FaceValue faceValue : faceValues) {
            Integer frequency = frequencies.get(faceValue);
            frequencies.put(faceValue, frequency != null ? frequency + 1 : 1);
        }
        return frequencies;
    }

    private FaceValue highestRepeated(int minFrequency) {
        Map<FaceValue, Integer> frequencies = frequency();

        FaceValue faceValue = null;

        for (Map.Entry<FaceValue, Integer> entry : frequencies.entrySet()) {
            if (entry.getValue() >= minFrequency) {
                if (faceValue == null || entry.getKey().getValue() > faceValue.getValue()) {
                    faceValue = entry.getKey();
                }
            }
        }
        return faceValue;
    }
}