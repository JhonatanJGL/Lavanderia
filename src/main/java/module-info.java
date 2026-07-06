module com.example.lavanderia {

    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires mysql.connector.j;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.lavanderia to javafx.fxml;
    exports com.example.lavanderia;
}