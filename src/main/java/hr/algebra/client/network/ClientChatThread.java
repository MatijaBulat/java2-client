package hr.algebra.client.network;

import hr.algebra.client.GameController;
import hr.algebra.client.utils.JndiHelper;
import javafx.application.Platform;

import javax.naming.NamingException;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientChatThread extends Thread {
    private static final String CLIENT_CHAT_PORT_KEY = "client.chat.port";
    private static int CLIENT_CHAT_PORT;
    private Socket chatSocket;
    private final GameController controller;

    public ClientChatThread(GameController controller) {
        this.controller = controller;
        try {
            CLIENT_CHAT_PORT = Integer.parseInt(JndiHelper.getValueFromConfiguration(CLIENT_CHAT_PORT_KEY));

            chatSocket = new Socket("localhost", CLIENT_CHAT_PORT);
        } catch (IOException | NamingException e) {
            Logger.getLogger(ClientChatThread.class.getName()).log(Level.SEVERE, "IOException | NamingException", e);
            closeConnection();
        }
    }

    @Override
    public void run() {
        try (BufferedReader cin = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()))) {
            while (chatSocket.isConnected()) {
                String message = cin.readLine();

                Platform.runLater(() -> {
                    controller.addMessage(message);
                });
            }
        } catch (IOException e) {
            Logger.getLogger(ClientChatThread.class.getName()).log(Level.SEVERE, "IOException", e);
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            if (this.chatSocket != null) this.chatSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientChatThread.class.getName()).log(Level.SEVERE, "IOException", ex);
        }
    }
}