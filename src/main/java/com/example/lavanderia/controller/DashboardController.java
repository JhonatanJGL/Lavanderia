package com.example.lavanderia.controller;

<<<<<<< HEAD
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
=======
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
>>>>>>> 871deaad62a31c18124c8d8607235abbb12c644c
        }
    }

    @FXML
<<<<<<< HEAD
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
=======
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
>>>>>>> 871deaad62a31c18124c8d8607235abbb12c644c
        }
    }
}
