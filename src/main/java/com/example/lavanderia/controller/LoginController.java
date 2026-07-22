package com.example.lavanderia.controller;

import com.example.lavanderia.dao.UsuarioDAO;
import com.example.lavanderia.model.Usuario;
<<<<<<< HEAD
import com.example.lavanderia.util.Alertas;
import com.example.lavanderia.util.Sesion;
import com.example.lavanderia.util.Validaciones;
=======
import com.example.lavanderia.util.Sesion;
import javafx.event.ActionEvent;
>>>>>>> 871deaad62a31c18124c8d8607235abbb12c644c
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
<<<<<<< HEAD
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblEstado;
=======
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private Label lblMensaje;
>>>>>>> 871deaad62a31c18124c8d8607235abbb12c644c

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
<<<<<<< HEAD
    private void ingresar() {
        String usuario = txtUsuario.getText() == null
                ? "" : txtUsuario.getText().trim();
        String password = txtPassword.getText() == null
                ? "" : txtPassword.getText();

        if (Validaciones.textoVacio(usuario)
                || Validaciones.textoVacio(password)) {
            lblEstado.setText("Complete el usuario y la contraseña.");
            return;
        }

        try {
            Usuario autenticado = usuarioDAO.autenticar(usuario, password);
            if (autenticado == null) {
                lblEstado.setText("Credenciales incorrectas o usuario inactivo.");
                txtPassword.clear();
                return;
            }
            Sesion.iniciar(autenticado);
            abrirDashboard(autenticado);
        } catch (SQLException e) {
            Alertas.error("Error de conexión",
                    "No se pudo validar el usuario. Revise MySQL y Conexion.java.\n\n"
                            + e.getMessage());
        }
    }

    private void abrirDashboard(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/lavanderia/view/dashboard.fxml"));
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setUsuario(usuario);

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root, 1250, 760));
            stage.setTitle("AquaClean - Panel principal");
            stage.setMinWidth(1100);
            stage.setMinHeight(680);
            stage.centerOnScreen();
        } catch (IOException e) {
            Alertas.error("Error", "No se pudo abrir el dashboard: "
                    + e.getMessage());
=======
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
>>>>>>> 871deaad62a31c18124c8d8607235abbb12c644c
        }
    }
}
