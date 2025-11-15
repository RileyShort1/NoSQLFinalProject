module org.example.nosqlfinalproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.example.nosqlfinalproject to javafx.fxml;
    exports org.example.nosqlfinalproject;
}