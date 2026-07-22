package com.example.lavanderia.controller;

import com.example.lavanderia.dao.ClienteDAO;
import com.example.lavanderia.model.Cliente;
import com.example.lavanderia.util.Alertas;
import com.example.lavanderia.util.Validaciones;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class ClienteController {
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, Number> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colDireccion;
    @FXML private Button btnGuardar;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private int idSeleccionado;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getId()));
        colNombre.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombre()));
        colCorreo.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCorreo()));
        colTelefono.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getTelefono()));
        colDireccion.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getDireccion()));

        tablaClientes.getSelectionModel().selectedItemProperty()
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
                || Validaciones.textoVacio(txtDireccion.getText())) {
            Alertas.advertencia("Campos incompletos",
                    "Complete todos los campos del cliente.");
            return;
        }

        String correo = txtCorreo.getText().trim();
        if (!Validaciones.correoValido(correo)) {
            Alertas.advertencia("Correo inválido",
                    "Ingrese un correo electrónico válido.");
            return;
        }

        try {
            if (clienteDAO.existeCorreo(correo, idSeleccionado)) {
                Alertas.advertencia("Registro duplicado",
                        "El correo ya pertenece a otro cliente.");
                return;
            }

            Cliente cliente = new Cliente(
                    idSeleccionado,
                    txtNombre.getText().trim(),
                    correo,
                    txtTelefono.getText().trim(),
                    txtDireccion.getText().trim()
            );

            boolean correcto = idSeleccionado == 0
                    ? clienteDAO.insertar(cliente)
                    : clienteDAO.actualizar(cliente);

            if (correcto) {
                Alertas.informacion("Operación exitosa",
                        idSeleccionado == 0
                                ? "Cliente guardado."
                                : "Cliente actualizado.");
                limpiar();
                cargarTabla(txtBuscar.getText());
            }
        } catch (SQLException e) {
            Alertas.error("Error de base de datos", e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        Cliente seleccionado =
                tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alertas.advertencia("Sin selección",
                    "Seleccione un cliente en la tabla.");
            return;
        }
        if (!Alertas.confirmar("Eliminar cliente",
                "¿Desea eliminar a " + seleccionado.getNombre() + "?")) return;

        try {
            clienteDAO.eliminar(seleccionado.getId());
            limpiar();
            cargarTabla(txtBuscar.getText());
            Alertas.informacion("Cliente eliminado",
                    "El registro fue desactivado correctamente.");
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
        txtDireccion.clear();
        tablaClientes.getSelectionModel().clearSelection();
        btnGuardar.setText("Guardar cliente");
    }

    private void cargarSeleccion(Cliente cliente) {
        if (cliente == null) return;
        idSeleccionado = cliente.getId();
        txtNombre.setText(cliente.getNombre());
        txtCorreo.setText(cliente.getCorreo());
        txtTelefono.setText(cliente.getTelefono());
        txtDireccion.setText(cliente.getDireccion());
        btnGuardar.setText("Actualizar cliente");
    }

    private void cargarTabla(String filtro) {
        try {
            String texto = filtro == null
                    ? "" : filtro.trim().toLowerCase();
            var datos = clienteDAO.listar().stream()
                    .filter(c -> texto.isBlank()
                            || c.getNombre().toLowerCase().contains(texto)
                            || c.getCorreo().toLowerCase().contains(texto)
                            || c.getTelefono().toLowerCase().contains(texto))
                    .toList();
            tablaClientes.setItems(FXCollections.observableArrayList(datos));
        } catch (SQLException e) {
            Alertas.error("Error",
                    "No se pudieron cargar los clientes: " + e.getMessage());
        }
    }
}
