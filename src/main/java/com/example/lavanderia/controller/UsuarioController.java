package com.example.lavanderia.controller;

import com.example.lavanderia.dao.UsuarioDAO;
import com.example.lavanderia.model.Usuario;
import com.example.lavanderia.util.Alertas;
import com.example.lavanderia.util.Sesion;
import com.example.lavanderia.util.Validaciones;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;

public class UsuarioController {
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> comboRol;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Number> colId;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colUsuario;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private Button btnGuardar;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private int idSeleccionado;

    @FXML
    public void initialize() {
        comboRol.setItems(FXCollections.observableArrayList(
                Usuario.ROL_ADMIN, Usuario.ROL_CAJERO, Usuario.ROL_REPORTES));

        colId.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getId()));
        colNombre.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombre()));
        colCorreo.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCorreo()));
        colUsuario.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getUsuario()));
        colRol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getRol()));

        tablaUsuarios.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, actual) -> cargarSeleccion(actual));
        txtBuscar.textProperty()
                .addListener((obs, anterior, actual) -> cargarTabla(actual));
        cargarTabla("");
    }

    @FXML
    private void guardar() {
        if (Validaciones.textoVacio(txtNombre.getText())
                || Validaciones.textoVacio(txtCorreo.getText())
                || Validaciones.textoVacio(txtTelefono.getText())
                || Validaciones.textoVacio(txtUsuario.getText())
                || Validaciones.textoVacio(txtPassword.getText())
                || comboRol.getValue() == null) {
            Alertas.advertencia("Campos incompletos",
                    "Complete todos los datos del usuario.");
            return;
        }

        if (!Validaciones.correoValido(txtCorreo.getText().trim())) {
            Alertas.advertencia("Correo inválido",
                    "Ingrese un correo electrónico válido.");
            return;
        }

        if (txtPassword.getText().length() < 6) {
            Alertas.advertencia("Contraseña insegura",
                    "La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        try {
            if (usuarioDAO.existeUsuarioOCorreo(
                    txtUsuario.getText().trim(),
                    txtCorreo.getText().trim(),
                    idSeleccionado)) {
                Alertas.advertencia("Registro duplicado",
                        "El usuario o el correo ya están registrados.");
                return;
            }

            Usuario usuario = new Usuario(
                    idSeleccionado,
                    txtNombre.getText().trim(),
                    txtCorreo.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtUsuario.getText().trim(),
                    txtPassword.getText(),
                    comboRol.getValue()
            );

            boolean correcto = idSeleccionado == 0
                    ? usuarioDAO.insertar(usuario)
                    : usuarioDAO.actualizar(usuario);

            if (correcto) {
                Alertas.informacion("Operación exitosa",
                        idSeleccionado == 0
                                ? "Usuario creado."
                                : "Usuario actualizado.");
                limpiar();
                cargarTabla(txtBuscar.getText());
            }
        } catch (SQLException e) {
            Alertas.error("Error de base de datos", e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        Usuario seleccionado =
                tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alertas.advertencia("Sin selección",
                    "Seleccione un usuario en la tabla.");
            return;
        }

        if (Sesion.getUsuarioActual() != null
                && seleccionado.getId() == Sesion.getUsuarioActual().getId()) {
            Alertas.advertencia("Operación no permitida",
                    "No puede eliminar el usuario con el que inició sesión.");
            return;
        }

        if (!Alertas.confirmar("Eliminar usuario",
                "¿Desea desactivar a " + seleccionado.getUsuario() + "?")) return;

        try {
            usuarioDAO.eliminar(seleccionado.getId());
            limpiar();
            cargarTabla(txtBuscar.getText());
            Alertas.informacion("Usuario eliminado",
                    "El usuario fue desactivado.");
        } catch (SQLException e) {
            Alertas.error("No se pudo eliminar", e.getMessage());
        }
    }

    @FXML
    private void limpiar() {
        idSeleccionado = 0;
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtUsuario.clear();
        txtPassword.clear();
        comboRol.getSelectionModel().clearSelection();
        tablaUsuarios.getSelectionModel().clearSelection();
        btnGuardar.setText("Guardar usuario");
    }

    private void cargarSeleccion(Usuario usuario) {
        if (usuario == null) return;
        idSeleccionado = usuario.getId();
        txtNombre.setText(usuario.getNombre());
        txtCorreo.setText(usuario.getCorreo());
        txtTelefono.setText(usuario.getTelefono());
        txtUsuario.setText(usuario.getUsuario());
        txtPassword.setText(usuario.getPassword());
        comboRol.setValue(usuario.getRol());
        btnGuardar.setText("Actualizar usuario");
    }

    private void cargarTabla(String filtro) {
        try {
            String texto = filtro == null
                    ? "" : filtro.trim().toLowerCase();
            var datos = usuarioDAO.listar().stream()
                    .filter(u -> texto.isBlank()
                            || u.getNombre().toLowerCase().contains(texto)
                            || u.getUsuario().toLowerCase().contains(texto)
                            || u.getRol().toLowerCase().contains(texto))
                    .toList();
            tablaUsuarios.setItems(FXCollections.observableArrayList(datos));
        } catch (SQLException e) {
            Alertas.error("Error",
                    "No se pudieron cargar los usuarios: " + e.getMessage());
        }
    }
}
