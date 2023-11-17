package hr.algebra.client.network;

import hr.algebra.client.GameController;
import hr.algebra.client.model.Player;
import hr.algebra.client.utils.JndiHelper;

import javax.naming.NamingException;
import java.io.IOException;
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
        System.out.println("sendPlayerState fnc");
        try {
            objectOutputStream.writeObject(player);
            objectOutputStream.flush();
            System.out.println("player sent: " + player);
        } catch (IOException e) {
            Logger.getLogger(ClientGameHandler.class.getName()).log(Level.SEVERE, "IOException", e);
            closeConnection();
        }
    }

    public void listenForPlayerUpdatesAndProcess() {
        System.out.println("listenForPlayerUpdatesAndProcess fnc");
       new Thread(new Runnable() {
           @Override
           public void run() {
               System.out.println("listenForPlayerUpdatesAndProcess run fnc");
               while(clientSocket.isConnected()) {
                   System.out.println("while loop");
                   try {
                       Player player = (Player) objectInputStream.readObject();
                       System.out.println("player received: " + player);
                      /* Platform.runLater(() -> {
                           System.out.println("platform run later, update ui");
                           //controller.setPlayer(player);
                       });*/
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