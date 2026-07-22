package com.example.lavanderia.app;

import com.example.lavanderia.db.Conexion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        mostrarLogin(stage);
    }

    public static void mostrarLogin(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(
                "/com/example/lavanderia/view/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 930, 600);
        stage.setTitle("AquaClean - Sistema de Lavandería");
        stage.setScene(scene);
        stage.setMinWidth(850);
        stage.setMinHeight(560);
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void stop() {
        Conexion.getInstancia().cerrarConexion();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
