module org.example.nosqlfinalproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.neo4j.driver;


    opens org.example.nosqlfinalproject to javafx.fxml;
    exports org.example.nosqlfinalproject;
}