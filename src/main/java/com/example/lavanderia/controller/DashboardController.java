package com.example.lavanderia.controller;

import com.example.lavanderia.app.Main;
import com.example.lavanderia.model.Usuario;
import com.example.lavanderia.util.Alertas;
import com.example.lavanderia.util.Sesion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

public class DashboardController {
    @FXML private BorderPane rootDashboard;
    @FXML private StackPane contenidoPrincipal;
    @FXML private Label lblNombreUsuario;
    @FXML private Label lblRol;

    @FXML private Button btnServicios;
    @FXML private Button btnClientes;
    @FXML private Button btnOrdenes;
    @FXML private Button btnUsuarios;
    @FXML private Button btnReportes;
    @FXML private Button btnConfiguracion;

    private Usuario usuario;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        Sesion.iniciar(usuario);
        lblNombreUsuario.setText(usuario.getNombre());
        lblRol.setText(usuario.getRol());
        aplicarPermisos();
        abrirModulo("inicio.fxml");
    }

    private void aplicarPermisos() {
        boolean admin = Usuario.ROL_ADMIN.equals(usuario.getRol());
        boolean cajero = Usuario.ROL_CAJERO.equals(usuario.getRol());
        boolean reportes = Usuario.ROL_REPORTES.equals(usuario.getRol());

        mostrar(btnServicios, admin || cajero);
        mostrar(btnClientes, admin || cajero);
        mostrar(btnOrdenes, admin || cajero);
        mostrar(btnUsuarios, admin);
        mostrar(btnReportes, admin || reportes);
        mostrar(btnConfiguracion, admin);

        rootDashboard.getStyleClass().removeAll(
                "rol-admin", "rol-cajero", "rol-reportes");
        rootDashboard.getStyleClass().add(admin
                ? "rol-admin" : cajero ? "rol-cajero" : "rol-reportes");
    }

    private void mostrar(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    @FXML private void irInicio() { abrirModulo("inicio.fxml"); }
    @FXML private void irServicios() { abrirModulo("servicios.fxml"); }
    @FXML private void irClientes() { abrirModulo("clientes.fxml"); }
    @FXML private void irOrdenes() { abrirModulo("ordenes.fxml"); }
    @FXML private void irUsuarios() { abrirModulo("usuarios.fxml"); }
    @FXML private void irReportes() { abrirModulo("reportes.fxml"); }
    @FXML private void irConfiguracion() { abrirModulo("configuracion.fxml"); }

    private void abrirModulo(String archivoFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/lavanderia/view/" + archivoFxml));
            contenidoPrincipal.getChildren().setAll((Node) loader.load());
        } catch (IOException e) {
            Alertas.error("Error de navegación",
                    "No se pudo abrir " + archivoFxml + ": " + e.getMessage());
        }
    }

    @FXML
    private void cerrarSesion() {
        if (!Alertas.confirmar("Cerrar sesión",
                "¿Desea cerrar la sesión actual?")) return;

        Sesion.cerrar();
        try {
            Stage stage = (Stage) rootDashboard.getScene().getWindow();
            Main.mostrarLogin(stage);
        } catch (IOException e) {
            Alertas.error("Error", "No se pudo volver al login: "
                    + e.getMessage());
        }
    }
}
