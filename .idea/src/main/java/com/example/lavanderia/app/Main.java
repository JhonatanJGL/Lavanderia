package com.example.lavanderia.app;

import com.example.lavanderia.db.Conexion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Crea la base de datos y las tablas si no existen (no hace falta ejecutar SQL a mano)
        Conexion.inicializarBaseDatos();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/lavanderia/login-view.fxml"));
        Parent root = loader.load();

        stage.setTitle("Lavandería - Login");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
