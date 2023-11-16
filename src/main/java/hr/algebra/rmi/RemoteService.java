package hr.algebra.rmi;



import java.net.Socket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteService extends Remote {
    String REMOTE_OBJECT_NAME = "hr.algebra.rmi";
    void sendMessage(String message) throws RemoteException;
    List<String> getChatMessage() throws RemoteException;
    void addChatClient(Socket clientSocket) throws RemoteException;
    List<Socket> getChatClients()throws RemoteException;
}

