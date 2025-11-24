package org.example.nosqlfinalproject;

import javafx.event.ActionEvent;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import java.io.IOException;

public class accountCreationController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button createAccountSubmitButton;

    private Neo4jUserRepository userRepository;

    public void setRepository(Neo4jUserRepository repository) {
        this.userRepository = repository;
    }

    @FXML
    private void initialize() {
        BooleanBinding requiredFieldsEmpty = firstNameField.textProperty().isEmpty()
                .or(lastNameField.textProperty().isEmpty())
                .or(emailField.textProperty().isEmpty())
                .or(usernameField.textProperty().isEmpty())
                .or(passwordField.textProperty().isEmpty());
        createAccountSubmitButton.disableProperty().bind(requiredFieldsEmpty);
    }

    // Placeholder handler for create account action
    @FXML
    private void handleCreateAccount(ActionEvent event) {
        if (userRepository == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Repository not available. Please return to login and try again.");
            alert.showAndWait();
            return;
        }

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("Please fill out all fields before creating an account.");
            alert.showAndWait();
            return;
        }

        try {
            if (userRepository.usernameExists(username)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Username Taken");
                alert.setHeaderText(null);
                alert.setContentText("Please choose a different username.");
                alert.showAndWait();
                return;
            }

            int newUserId = userRepository.getNextUserId();
            User newUser = new User(newUserId, firstName, lastName, "", "", "", "", "", "");
            newUser.setEmail(email);
            newUser.setUsername(username);
            newUser.setPassword(Neo4jUserRepository.hashPassword(password));

            userRepository.createUser(newUser);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Account Created");
            alert.setHeaderText(null);
            alert.setContentText("Account created successfully! You can now log in.");
            alert.showAndWait();

            goBackToLogin(event);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Account Creation Failed");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while creating the account.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void goBackToLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loginPage.fxml"));
        Parent login = loader.load();
        loginPageController controller = loader.getController();
        if (userRepository != null) {
            controller.setRepository(userRepository);
        }
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(login);
    }
}

