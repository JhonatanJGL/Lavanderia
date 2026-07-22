module com.example.lavanderia {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;

    exports com.example.lavanderia.app;
    exports com.example.lavanderia.model;

    opens com.example.lavanderia.controller to javafx.fxml;
    opens com.example.lavanderia.model to javafx.base;
}
