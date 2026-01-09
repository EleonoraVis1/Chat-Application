package chat;

import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{
    private ServerSocket serverSocket;
    public Server(ServerSocket serverSocket) throws IOException {
        this.serverSocket = serverSocket;
    }
    public void closeServerSocket(){
        try {
            if(serverSocket != null)
                serverSocket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
                System.out.println("A new client has connected");
            }
        } catch (IOException e){
            e.printStackTrace();
            closeServerSocket();
        }
    }
}
