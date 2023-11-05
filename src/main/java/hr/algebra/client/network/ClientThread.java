package hr.algebra.client.network;

import hr.algebra.client.GameController;
import hr.algebra.client.models.Player;
import javafx.application.Platform;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.net.*;


public class ClientThread implements Runnable {
    private static final String PROPERTIES_FILE = "socket.properties";
    private static final String CLIENT_PORT = "CLIENT_PORT";
    private static final Properties PROPERTIES = new Properties();
    private Socket clientSocket;
    private final GameController controller;

    public ClientThread(GameController controller) {
     this.controller = controller;
 }

    static {
        try {
            PROPERTIES.load(new FileInputStream(PROPERTIES_FILE));
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            clientSocket = new Socket("localhost", Integer.parseInt(PROPERTIES.getProperty(CLIENT_PORT)));

            while (true) {
                ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

                Player player = (Player) objectInputStream.readObject();

                Platform.runLater(() -> {
                    System.out.println(player + " -> player sa servera");
                    //controller.setPlayer(player);
                });
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("There was a problem connecting to the server.");
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void sendPlayerState(Player player) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOutputStream.writeObject(player);
        } catch (IOException e) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
    }
}