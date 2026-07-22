package com.example.lavanderia.controller;

import com.example.lavanderia.model.Usuario;
import com.example.lavanderia.util.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Label lblBienvenida;

    @FXML
    public void initialize() {
        Usuario actual = Sesion.getUsuarioActual();
        if (actual != null) {
            lblBienvenida.setText("Bienvenido, " + actual.getNombre());
        }
    }

    @FXML
    private void onOrdenesClick(ActionEvent event) {
        cambiarPantalla(event, "/com/example/lavanderia/orden-view.fxml", "Lavandería - Órdenes de Lavado");
    }

    @FXML
    private void onCerrarSesionClick(ActionEvent event) {
        Sesion.cerrarSesion();
        cambiarPantalla(event, "/com/example/lavanderia/login-view.fxml", "Lavandería - Login");
    }

    private void cambiarPantalla(ActionEvent event, String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
        } catch (IOException e) {
            System.out.println("Error al cambiar de pantalla: " + e.getMessage());
        }
    }
}
