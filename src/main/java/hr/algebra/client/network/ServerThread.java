package hr.algebra.client.network;

import hr.algebra.client.model.Player;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingDeque;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


//ovo mi ne triba *********************************************************************
public class ServerThread extends Thread {
    private final LinkedBlockingDeque<Player> players = new LinkedBlockingDeque<>();
    private static final int PORT = 1989;
    private ServerSocket serverSocket;
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    @Override
    public void run() {
        try {
            try {
                serverSocket = new ServerSocket(PORT);
            } catch (Exception e) {
                //serverSocket = new ServerSocket(1235); //-> ovo maknit jer ne triba bit tu, nije potriba od toga
                e.printStackTrace();
            }

            Socket clientSocket = serverSocket.accept();

            while (true) {
                if (!players.isEmpty()) {
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    out.writeObject(players.getFirst());
                    players.remove(players.getFirst());
                    out.flush();
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred in the server thread:");
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void trigger(Player player) {
        players.add(player);
    }
}

