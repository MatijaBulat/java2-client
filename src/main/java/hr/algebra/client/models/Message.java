package hr.algebra.client.models;

import java.io.Serializable;
import java.time.LocalTime;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String message;
    private String playerName;
    private LocalTime time;

    public Message(String message, String playerName, LocalTime time) {
        this.message = message;
        this.playerName = playerName;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getPlayerName() {
        return playerName;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    public LocalTime getTime() {
        return time;
    }
    public void setTime(LocalTime time) {
        this.time = time;
    }
    @Override
    public String toString() {
        return playerName + ": " + message + " -" + time;
    }
}