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

    /**
     * Gets a map of all the (best) possible score combinations for these face values
     * @return map of score types
     */
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

    /**
     * Calculate the score of the category Ones
     * @return sum of ones
     */
    public int ones() {
        return sumOfSingle(FaceValue.ONE);
    }

    /**
     * Calculate the score of the category Twos
     * @return sum of twos
     */
    public int twos() {
        return sumOfSingle(FaceValue.TWO);
    }

    /**
     * Calculate the score of the category Threes
     * @return sum of threes
     */
    public int threes() {
        return sumOfSingle(FaceValue.THREE);
    }

    /**
     * Calculate the score of the category Fours
     * @return sum of fours
     */
    public int fours() {
        return sumOfSingle(FaceValue.FOUR);
    }

    /**
     * Calculate the score of the category Fives
     * @return sum of fives
     */
    public int fives() {
        return sumOfSingle(FaceValue.FIVE);
    }

    /**
     * Calculate the score of the category Sixes
     * @return sum of sixes
     */
    public int sixes() {
        return sumOfSingle(FaceValue.SIX);
    }

    /**
     * Calculate the score of the best pair
     * @return sum of the pair
     */
    public int onePair() {
        return sumOfAKind(2);
    }

    /**
     * Calculate the score of the best two (different) pairs
     * @return sum of the two pairs
     */
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

    /**
     * Calculate the score of three of a kind
     * @return sum of the three
     */
    public int threeOfAKind() {
        return sumOfAKind(3);
    }

    /**
     * Calculate the score of four of a kind
     * @return sum of the four
     */
    public int fourOfAKind() {
        return sumOfAKind(4);
    }

    /**
     * Check if face values equals a small straight
     * @return 15 if true, else 0
     */
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

    /**
     * Check if face values equals a large straight
     * @return 20 if true, else 0
     */
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

    /**
     * Check for a full house and calculate the sum
     * @return sum of the dice if true, else 0
     */
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

    /**
     * Calculate the sum of all dice
     * @return sum of the dice
     */
    public int chance() {
        return sum();
    }

    /**
     * Check if the face values equals a com.github.hsamoht.yatzy
     * @return 50 if true, else 0
     */
    public int yatzy() {
        Map<FaceValue, Integer> frequencies = frequency();
        return frequencies.size() == 1 ? 50 : 0;
    }

    /**
     * @return sum of face values
     */
    private int sum() {
        int sum = 0;
        for (FaceValue faceValue : faceValues) {
            sum += faceValue.getValue();
        }
        return sum;
    }

    /**
     * Checks if the face values yields a X of a kind combo and returns its value if true
     * @param frequency of the face value
     * @return sum of the X of a kind if true, else null
     */
    private int sumOfAKind(int frequency) {
        FaceValue faceValue = highestRepeated(frequency);
        return faceValue != null ? faceValue.getValue() * frequency : 0;
    }

    /**
     * Sums up all the occurrences of a face value
     * @param faceValue to check
     * @return sum of the face value
     */
    private int sumOfSingle(FaceValue faceValue) {
        int sum = 0;
        for (FaceValue face : faceValues) {
            if (face == faceValue) {
                sum += face.getValue();
            }
        }
        return sum;
    }

    /**
     * Finds the frequency of the different face values
     * @return map of the frequency
     */
    private Map<FaceValue, Integer> frequency() {
        Map<FaceValue, Integer> frequencies = new EnumMap<>(FaceValue.class);

        for (FaceValue faceValue : faceValues) {
            Integer frequency = frequencies.get(faceValue);
            frequencies.put(faceValue, frequency != null ? frequency + 1 : 1);
        }

        return frequencies;
    }

    /**
     * Finds the highest repeated face value
     * @param minFrequency minimum number of repeats to look for
     * @return the highest repeated face value with the given minFrequency
     */
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