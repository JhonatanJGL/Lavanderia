package com.example.lavanderia.controller;

import com.example.lavanderia.dao.UsuarioDAO;
import com.example.lavanderia.model.Usuario;
import com.example.lavanderia.util.Alertas;
import com.example.lavanderia.util.Sesion;
import com.example.lavanderia.util.Validaciones;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblEstado;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
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
        }
    }
}
