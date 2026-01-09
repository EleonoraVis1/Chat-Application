package chat;

import javafx.scene.control.ChoiceBox;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    public BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private ChoiceBox cb;
    public static int kiek;

    public ClientHandler(Socket socket){
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            if(kiek > 0)
                clientHandlers.get(0).cb.getItems().add(this.clientUsername);
            if(!this.clientUsername.equals("SERVER"))
                Antras.clients.add(this.clientUsername);
            System.out.println("SERVER: " + clientUsername + " has entered the chat");
            ++kiek;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void setCb(ChoiceBox cb){
        this.cb = cb;
    }
    public String getClientUsername(){
        return clientUsername;
    }
    public void removeClientHandler(){
        clientHandlers.remove(this);
        try {
            Controller.myWriter = new FileWriter("everything.txt", true);
            Controller.myWriter.append("Klientas " + clientUsername + " paliko kambarį\n");
            Controller.myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();
                if(!messageFromClient.startsWith("."))
                    broadcastMessage(messageFromClient, "All");
                else{
                    String msg = messageFromClient.replaceFirst(".", "");
                    broadcastMessage(msg, "SERVER");
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
    public void broadcastMessage(String messageToSend, String who) throws IOException {
        for (ClientHandler clientHandler : clientHandlers){
            try {
                if(who.equals("All")){
                    if (!clientHandler.clientUsername.equals(clientUsername)){
                        clientHandler.bufferedWriter.write(clientUsername + ": " + messageToSend);
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();
                    }
                }
                else{
                    if(clientHandler.clientUsername.equals(who)){
                        clientHandler.bufferedWriter.write(clientUsername + ": " + messageToSend);
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();
                    }
                }

            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
        try {
            if (bufferedReader != null)
                bufferedReader.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
            if (socket != null)
                socket.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
