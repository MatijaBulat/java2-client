package hr.algebra.client.models;

public enum ScoreType {
    ONES,
    TWOS,
    THREES,
    FOURS,
    FIVES,
    SIXES,
    SUM,
    BONUS,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    FOUR_OF_A_KIND,
    SMALL_STRAIGHT,
    LARGE_STRAIGHT,
    FULL_HOUSE,
    CHANCE,
    YATZY,
    TOTAL;

    private static ScoreType[] scoreTypes = values();

    /**
     * Gets the next score type (following the list and looping at the end)
     * @return the next ScoreType
     */
    public ScoreType next() {
        return scoreTypes[(this.ordinal() + 1) % scoreTypes.length];
    }
}