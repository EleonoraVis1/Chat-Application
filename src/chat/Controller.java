package chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button sukurtiB;
    public static FileWriter myWriter;

    @FXML
    void sukurtiClicked(ActionEvent event) throws IOException {
        Stage stage1 = new Stage();
        stage1.setTitle("Server");
        FXMLLoader fxmlLoader1 = new FXMLLoader(Antras.class.getResource("antras.fxml"));
        Scene scene1 = null;
        try {
            scene1 = new Scene(fxmlLoader1.load(), 580, 400);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage1.setScene(scene1);
        stage1.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            myWriter = new FileWriter("everything.txt", true);
            myWriter.append("Sukurtas kambarys\n");
            myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

