package hr.algebra.client.model;

import java.io.*;

public enum ScoreType implements Serializable {
    ONES("Ones"),
    TWOS("Twos"),
    THREES("Threes"),
    FOURS("Fours"),
    FIVES("Fives"),
    SIXES("Sixes"),
    SUM("Sum 63"),
    BONUS("Bonus"),
    ONE_PAIR("1 pair"),
    TWO_PAIR("2 pair"),
    THREE_OF_A_KIND("3 of a kind"),
    FOUR_OF_A_KIND("4 of a kind"),
    SMALL_STRAIGHT("Small straight"),
    LARGE_STRAIGHT("Large straight"),
    FULL_HOUSE("Full house"),
    CHANCE("Chance"),
    YATZY("Yatzy"),
    TOTAL("Total");

    private String value;

    ScoreType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private static ScoreType[] scoreTypes = values();

    public ScoreType next() {
        return scoreTypes[(this.ordinal() + 1) % scoreTypes.length];
    }
}