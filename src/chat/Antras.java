package chat;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class Antras implements Initializable{
    @FXML
    private Button addB;

    @FXML
    private AnchorPane ap_main;

    @FXML
    private Button button_send;

    @FXML
    private ScrollPane sp_main;

    @FXML
    private TextField tf_message;

    @FXML
    private VBox vbox_messages;
    @FXML
    private ChoiceBox cb;
    private Server server;
    public static ArrayList<String> clients = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            server = new Server(new ServerSocket(1235));
            Controller.myWriter = new FileWriter("everything.txt", true);
            Controller.myWriter.append("Prijungtas serveris\n");
            Controller.myWriter.close();
            Thread thread = new Thread(server);
            thread.start();
            Client client = new Client(new Socket("localhost", 1235), "SERVER");
            client.listenForMessage(vbox_messages);

        } catch (IOException e) {
            e.printStackTrace();
            server.closeServerSocket();
            System.out.println("Error creating a server.");
        }
        vbox_messages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                sp_main.setVvalue((Double) newValue);
            }
        });
        button_send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String messageToSend = tf_message.getText();
                if(!messageToSend.isEmpty()){
                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    hBox.setPadding(new Insets(5, 5, 5, 10));
                    Text text = new Text(messageToSend);
                    TextFlow textFlow = new TextFlow(text);
                    textFlow.setStyle("-fx-color: rgb(239,242,255)" + ";-fx-background-color: rgb(15,125,242)" + ";-fx-background-radius: 20px");
                    textFlow.setPadding(new Insets(5, 10, 5, 10));
                    text.setFill(Color.color(0.934, 0.945, 0.966));
                    hBox.getChildren().add(textFlow);
                    vbox_messages.getChildren().add(hBox);
                    try {
                        Controller.myWriter = new FileWriter("everything.txt", true);
                        Controller.myWriter.append("Serverio išsiųta žinutė: " + messageToSend + "\n");
                        Controller.myWriter.close();
                        ClientHandler.clientHandlers.get(0).broadcastMessage(messageToSend, (String) cb.getValue());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    tf_message.clear();
                }
            }
        });
        ClientHandler.clientHandlers.get(0).setCb(cb);
        cb.getItems().add("All");
    }
    public static void addLabel(String msgFromServer, VBox vBox){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 5, 10));
        Text text = new Text(msgFromServer);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(233,233,235)" + ";-fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hBox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBox.getChildren().add(hBox);
            }
        });
    }
}
