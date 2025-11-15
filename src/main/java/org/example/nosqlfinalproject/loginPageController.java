package org.example.nosqlfinalproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class loginPageController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Button loginButton;

    public void loginButtonToHomePage(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent home = FXMLLoader.load(getClass().getResource("homePage.fxml"));
        Scene scene = ((Node) actionEvent.getSource()).getScene(); // same scene, same size
        scene.setRoot(home);
    }
}
