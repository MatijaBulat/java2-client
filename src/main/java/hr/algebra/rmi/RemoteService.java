package hr.algebra.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote {

    String REMOTE_OBJECT_NAME = "hr.algebra.rmi";
    void sendMessage(String message) throws RemoteException;
    String getMessage() throws RemoteException;
}
