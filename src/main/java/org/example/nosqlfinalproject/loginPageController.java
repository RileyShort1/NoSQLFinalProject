package org.example.nosqlfinalproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class loginPageController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    
    // Repository should be initialized elsewhere (e.g., in App.java or a service class)
    private Neo4jUserRepository userRepository;

    @FXML
    private TextField nameField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    @FXML
    private Button createAccountButton;
    
    // Setter for repository (should be called before login)
    public void setRepository(Neo4jUserRepository repository) {
        this.userRepository = repository;
    }

    public void loginButtonToHomePage(javafx.event.ActionEvent actionEvent) throws IOException {
        if (userRepository == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Repository not initialized. Please restart the application.");
            alert.showAndWait();
            return;
        }
        
        String nameInput = nameField.getText().trim();
        String usernameInput = usernameField.getText().trim();
        String passwordInput = passwordField.getText();
        
        if (passwordInput == null || passwordInput.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter your password.");
            alert.showAndWait();
            return;
        }
        
        if (usernameInput.isEmpty() && nameInput.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter either a username or your full name.");
            alert.showAndWait();
            return;
        }
        
        String firstName = "";
        String lastName = "";
        if (usernameInput.isEmpty()) {
            String[] nameParts = nameInput.split("\\s+", 2);
            if (nameParts.length < 2) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Login Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter both first and last name (e.g., 'John Doe').");
                alert.showAndWait();
                return;
            }
            firstName = nameParts[0];
            lastName = nameParts[1];
        }
        
        try {
            // Test database connection first
            int userCount = userRepository.testConnection();
            if (userCount < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Database Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to connect to database. Please check your connection settings.");
                alert.showAndWait();
                return;
            }
            
            if (userCount == 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Empty Database");
                alert.setHeaderText(null);
                alert.setContentText("No users found in the database. Please import user data first.");
                alert.showAndWait();
                return;
            }
            
            User foundUser = null;
            
            if (!usernameInput.isEmpty()) {
                foundUser = userRepository.findUserByUsername(usernameInput);
            } else {
                foundUser = userRepository.findUserByExactName(firstName, lastName);
            }
            
            if (foundUser == null) {
                if (!usernameInput.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Login Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Username not found.");
                    alert.showAndWait();
                    return;
                } else {
                    List<User> users = userRepository.findUsersByName(nameInput);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Login Failed");
                    alert.setHeaderText(null);
                    
                    if (users.isEmpty()) {
                        alert.setContentText("User not found: " + firstName + " " + lastName + 
                            "\n\nTotal users in database: " + userCount +
                            "\nPlease check the name and try again.");
                    } else {
                        StringBuilder message = new StringBuilder("Exact match not found: " + firstName + " " + lastName);
                        message.append("\n\nSimilar users found (").append(users.size()).append("):\n");
                        for (int i = 0; i < Math.min(5, users.size()); i++) {
                            User u = users.get(i);
                            message.append("  - ").append(u.getFirstName()).append(" ").append(u.getLastName()).append("\n");
                        }
                        alert.setContentText(message.toString());
                    }
                    alert.showAndWait();
                    return;
                }
            }
            
            String storedPassword = foundUser.getPassword();
            if (storedPassword == null) {
                storedPassword = "";
            }
            String hashedInput = Neo4jUserRepository.hashPassword(passwordInput);
            if (!storedPassword.equals(hashedInput)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Incorrect password.");
                alert.showAndWait();
                return;
            }
            
            // User found - navigate to home page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("homePage.fxml"));
            Parent home = loader.load();
            
            // Get the controller and set the repository with the logged-in user
            homePageController controller = loader.getController();
            controller.setRepository(userRepository, foundUser);
            
            Scene scene = ((Node) actionEvent.getSource()).getScene();
            scene.setRoot(home);
            
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred during login: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void createAccountButtonPressed(javafx.event.ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("accountCreationPage.fxml"));
        Parent accountCreation = loader.load();
        accountCreationController controller = loader.getController();
        if (userRepository != null) {
            controller.setRepository(userRepository);
        }

        Scene scene = ((Node) actionEvent.getSource()).getScene();
        scene.setRoot(accountCreation);
    }
}
