package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label serverScore,clientScore;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SaveReference.addReference("anchorPane",anchorPane);
        SaveReference.addReference("serverScore",serverScore);
        SaveReference.addReference("clientScore",clientScore);
    }

}
