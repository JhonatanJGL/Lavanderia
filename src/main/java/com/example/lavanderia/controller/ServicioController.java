package com.example.lavanderia.controller;

import com.example.lavanderia.dao.ServicioDAO;
import com.example.lavanderia.model.Servicio;
import com.example.lavanderia.util.Alertas;
import com.example.lavanderia.util.Validaciones;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;

public class ServicioController {
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Servicio> tablaServicios;
    @FXML private TableColumn<Servicio, Number> colId;
    @FXML private TableColumn<Servicio, String> colNombre;
    @FXML private TableColumn<Servicio, Number> colPrecio;
    @FXML private TableColumn<Servicio, Number> colStock;
    @FXML private TableColumn<Servicio, String> colDescripcion;
    @FXML private Button btnGuardar;

    private final ServicioDAO servicioDAO = new ServicioDAO();
    private int idSeleccionado;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getIdServicio()));
        colNombre.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombre()));
        colPrecio.setCellValueFactory(d ->
                new SimpleDoubleProperty(d.getValue().getPrecio()));
        colStock.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getStock()));
        colDescripcion.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getDescripcion()));

        colPrecio.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : String.format("$ %.2f", item.doubleValue()));
            }
        });

        tablaServicios.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, actual) -> cargarSeleccion(actual));
        txtBuscar.textProperty()
                .addListener((obs, anterior, actual) -> cargarTabla(actual));
        cargarTabla("");
    }

    @FXML
    private void guardar() {
        if (Validaciones.textoVacio(txtNombre.getText())
                || Validaciones.textoVacio(txtPrecio.getText())
                || Validaciones.textoVacio(txtStock.getText())
                || Validaciones.textoVacio(txtDescripcion.getText())) {
            Alertas.advertencia("Campos incompletos",
                    "Complete todos los campos del servicio.");
            return;
        }

        try {
            double precio = Validaciones.decimalPositivo(txtPrecio.getText());
            int stock = Validaciones.enteroPositivo(txtStock.getText());
            String nombre = txtNombre.getText().trim();

            if (servicioDAO.existeNombre(nombre, idSeleccionado)) {
                Alertas.advertencia("Registro duplicado",
                        "Ya existe un servicio con ese nombre.");
                return;
            }

            Servicio servicio = new Servicio(idSeleccionado, nombre, precio,
                    stock, txtDescripcion.getText().trim());

            boolean correcto = idSeleccionado == 0
                    ? servicioDAO.insertar(servicio)
                    : servicioDAO.actualizar(servicio);

            if (correcto) {
                Alertas.informacion("Operación exitosa",
                        idSeleccionado == 0
                                ? "Servicio guardado."
                                : "Servicio actualizado.");
                limpiar();
                cargarTabla(txtBuscar.getText());
            }
        } catch (NumberFormatException e) {
            Alertas.error("Dato inválido",
                    "Precio y stock deben ser números mayores a cero.");
        } catch (SQLException e) {
            Alertas.error("Error de base de datos", e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        Servicio seleccionado =
                tablaServicios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alertas.advertencia("Sin selección",
                    "Seleccione un servicio en la tabla.");
            return;
        }
        if (!Alertas.confirmar("Eliminar servicio",
                "¿Desea eliminar el servicio "
                        + seleccionado.getNombre() + "?")) return;

        try {
            servicioDAO.eliminar(seleccionado.getIdServicio());
            limpiar();
            cargarTabla(txtBuscar.getText());
            Alertas.informacion("Servicio eliminado",
                    "El registro fue desactivado correctamente.");
        } catch (SQLException e) {
            Alertas.error("No se pudo eliminar", e.getMessage());
        }
    }

    @FXML
    private void limpiar() {
        idSeleccionado = 0;
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtDescripcion.clear();
        tablaServicios.getSelectionModel().clearSelection();
        btnGuardar.setText("Guardar servicio");
    }

    private void cargarSeleccion(Servicio servicio) {
        if (servicio == null) return;
        idSeleccionado = servicio.getIdServicio();
        txtNombre.setText(servicio.getNombre());
        txtPrecio.setText(String.valueOf(servicio.getPrecio()));
        txtStock.setText(String.valueOf(servicio.getStock()));
        txtDescripcion.setText(servicio.getDescripcion());
        btnGuardar.setText("Actualizar servicio");
    }

    private void cargarTabla(String filtro) {
        try {
            String texto = filtro == null
                    ? "" : filtro.trim().toLowerCase();
            var datos = servicioDAO.listar().stream()
                    .filter(s -> texto.isBlank()
                            || s.getNombre().toLowerCase().contains(texto)
                            || s.getDescripcion().toLowerCase().contains(texto))
                    .toList();
            tablaServicios.setItems(
                    FXCollections.observableArrayList(datos));
        } catch (SQLException e) {
            Alertas.error("Error",
                    "No se pudieron cargar los servicios: " + e.getMessage());
        }
    }
}
