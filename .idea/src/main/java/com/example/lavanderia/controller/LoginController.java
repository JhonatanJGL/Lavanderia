package com.example.lavanderia.controller;

import com.example.lavanderia.dao.UsuarioDAO;
import com.example.lavanderia.model.Usuario;
import com.example.lavanderia.util.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private Label lblMensaje;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void onIngresarClick(ActionEvent event) {
        String nombre = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();

        if (nombre.isEmpty()) {
            lblMensaje.setText("Ingresa un nombre de usuario.");
            return;
        }

        Usuario usuario = usuarioDAO.buscarPorNombre(nombre);

        if (usuario == null) {
            lblMensaje.setText("Usuario no encontrado.");
            return;
        }

        Sesion.setUsuarioActual(usuario);
        irADashboard(event);
    }

    private void irADashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/lavanderia/dashboard-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Lavandería - Dashboard");
        } catch (IOException e) {
            lblMensaje.setText("Error al cargar el dashboard: " + e.getMessage());
        }
    }
}
