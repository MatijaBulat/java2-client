package hr.algebra.client.network;

import hr.algebra.client.GameController;
import hr.algebra.client.model.Player;
import hr.algebra.client.model.ScoreType;
import hr.algebra.client.utils.JndiHelper;
import javafx.application.Platform;

import javax.naming.NamingException;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.net.*;


public class ClientGameHandler {
    private static final String CLIENT_GAME_PORT_KEY = "client.game.port";
    private static int CLIENT_GAME_PORT;
    private Socket clientSocket;
    private final GameController controller;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public ClientGameHandler(GameController controller) {
        this.controller = controller;
        try {
            CLIENT_GAME_PORT = Integer.parseInt(JndiHelper.getValueFromConfiguration(CLIENT_GAME_PORT_KEY));

            clientSocket = new Socket("localhost", CLIENT_GAME_PORT);

            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (NamingException | IOException e) {
            Logger.getLogger(ClientGameHandler.class.getName()).log(Level.SEVERE, "NamingException | IOException", e);
            closeConnection();
        }
    }

    public void sendPlayerState(Player player) {
        try {
            objectOutputStream.reset();
            objectOutputStream.writeObject(player);
            objectOutputStream.flush();
        } catch (IOException e) {
            Logger.getLogger(ClientGameHandler.class.getName()).log(Level.SEVERE, "IOException", e);
            closeConnection();
        }
    }

    public void listenForPlayerUpdatesAndProcess() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (clientSocket.isConnected()) {
                    try {
                        Player player = (Player) objectInputStream.readObject();

                        Platform.runLater(() -> {
                            controller.setOpponentPlayer(player);
                        });
                    } catch (IOException | ClassNotFoundException e) {
                        Logger.getLogger(ClientGameHandler.class.getName()).log(Level.SEVERE, "IOException | ClassNotFoundException", e);
                        closeConnection();
                        break;
                    }
                }
            }
        }).start();
    }

    private void closeConnection() {
        try {
            if (this.objectOutputStream != null) this.objectOutputStream.close();
            if (this.objectInputStream != null) this.objectInputStream.close();
            if (this.clientSocket != null) this.clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientGameHandler.class.getName()).log(Level.SEVERE, "IOException", ex);
        }
    }
}