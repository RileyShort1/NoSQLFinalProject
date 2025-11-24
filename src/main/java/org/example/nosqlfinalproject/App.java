package org.example.nosqlfinalproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class App extends Application {
    public final double APP_W = 1200;
    public final double APP_H = 800;

    private Neo4jClient client;
    private Neo4jUserRepository repository;

    @Override
    public void start(Stage stage) throws IOException {
        // Initialize Neo4j connection
        try {
            client = new Neo4jClient("neo4j://127.0.0.1:7687", "neo4j", "12345678");
            repository = new Neo4jUserRepository(client.getDriver(), "users");
        } catch (Exception e) {
            e.printStackTrace();
            // Continue anyway - the app will show errors when trying to use the repository
        }

        // Load login page and pass repository to controller
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("loginPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), APP_W, APP_H);
        
        // Get the controller and set the repository
        loginPageController loginController = fxmlLoader.getController();
        if (repository != null) {
            loginController.setRepository(repository);
        }
        
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        // Close Neo4j connection when application shuts down
        if (client != null) {
            client.close();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}