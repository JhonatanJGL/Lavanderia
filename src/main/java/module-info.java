module com.example.lavanderia {

    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires mysql.connector.j;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.lavanderia.app to javafx.fxml;
    opens com.example.lavanderia.controller to javafx.fxml;
    opens com.example.lavanderia.model to javafx.base;

    exports com.example.lavanderia.app;
    exports com.example.lavanderia.controller;
    exports com.example.lavanderia.model;
}
