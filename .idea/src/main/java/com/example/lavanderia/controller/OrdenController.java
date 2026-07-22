package com.example.lavanderia.controller;

import com.example.lavanderia.dao.ClienteDAO;
import com.example.lavanderia.dao.OrdenLavadoDAO;
import com.example.lavanderia.dao.ServicioDAO;
import com.example.lavanderia.model.Cliente;
import com.example.lavanderia.model.OrdenLavado;
import com.example.lavanderia.model.Servicio;
import com.example.lavanderia.util.Sesion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class OrdenController {

    @FXML
    private Button btnVolver;

    @FXML
    private TableView<OrdenLavado> tablaOrdenes;
    @FXML
    private TableColumn<OrdenLavado, Number> colId;
    @FXML
    private TableColumn<OrdenLavado, String> colCliente;
    @FXML
    private TableColumn<OrdenLavado, String> colServicio;
    @FXML
    private TableColumn<OrdenLavado, String> colFecha;
    @FXML
    private TableColumn<OrdenLavado, Number> colCantidad;
    @FXML
    private TableColumn<OrdenLavado, String> colTotal;
    @FXML
    private TableColumn<OrdenLavado, String> colEstado;

    @FXML
    private ComboBox<Cliente> cbCliente;
    @FXML
    private ComboBox<Servicio> cbServicio;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private TextField txtCantidad;
    @FXML
    private ComboBox<String> cbEstado;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblMensaje;

    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnEliminar;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ServicioDAO servicioDAO = new ServicioDAO();
    private final OrdenLavadoDAO ordenDAO = new OrdenLavadoDAO();

    private final ObservableList<OrdenLavado> datosOrdenes = FXCollections.observableArrayList();
    private final Map<Integer, String> mapaClientes = new HashMap<>();
    private final Map<Integer, String> mapaServicios = new HashMap<>();

    private OrdenLavado ordenSeleccionada;

    @FXML
    public void initialize() {
        configurarTabla();
        configurarCombos();
        cargarClientesYServicios();
        cargarOrdenes();
        limpiarFormulario();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getIdOrden()));
        colCliente.setCellValueFactory(data ->
                new SimpleStringProperty(mapaClientes.getOrDefault(data.getValue().getIdCliente(), "—")));
        colServicio.setCellValueFactory(data ->
                new SimpleStringProperty(mapaServicios.getOrDefault(data.getValue().getIdServicio(), "—")));
        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getFecha())));
        colCantidad.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCantidad()));
        colTotal.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("$%.2f", data.getValue().getTotal())));
        colEstado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstado()));

        tablaOrdenes.setItems(datosOrdenes);
        tablaOrdenes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                cargarEnFormulario(newSel);
            }
        });
    }

    private void configurarCombos() {
        // Muestra el nombre del cliente en vez de Cliente@hashcode
        cbCliente.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente c) {
                return c == null ? "" : c.getNombre();
            }

            @Override
            public Cliente fromString(String s) {
                return null;
            }
        });

        cbServicio.setConverter(new StringConverter<Servicio>() {
            @Override
            public String toString(Servicio s) {
                return s == null ? "" : s.getNombre() + " ($" + s.getPrecio() + ")";
            }

            @Override
            public Servicio fromString(String s) {
                return null;
            }
        });

        cbEstado.setItems(FXCollections.observableArrayList("Pendiente", "En proceso", "Listo", "Entregado"));

        // Recalcula el total automáticamente cuando cambian servicio o cantidad
        cbServicio.valueProperty().addListener((obs, oldVal, newVal) -> recalcularTotal());
        txtCantidad.textProperty().addListener((obs, oldVal, newVal) -> recalcularTotal());
    }

    private void cargarClientesYServicios() {
        ObservableList<Cliente> clientes = FXCollections.observableArrayList(clienteDAO.listarTodos());
        ObservableList<Servicio> servicios = FXCollections.observableArrayList(servicioDAO.listarTodos());

        cbCliente.setItems(clientes);
        cbServicio.setItems(servicios);

        mapaClientes.clear();
        for (Cliente c : clientes) {
            mapaClientes.put(c.getId(), c.getNombre());
        }

        mapaServicios.clear();
        for (Servicio s : servicios) {
            mapaServicios.put(s.getIdServicio(), s.getNombre());
        }
    }

    private void cargarOrdenes() {
        datosOrdenes.setAll(ordenDAO.listarTodos());
    }

    private void recalcularTotal() {
        Servicio servicio = cbServicio.getValue();
        String cantidadTexto = txtCantidad.getText();

        if (servicio == null || cantidadTexto == null || cantidadTexto.isBlank()) {
            lblTotal.setText("$0.00");
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadTexto.trim());
            double total = cantidad * servicio.getPrecio();
            lblTotal.setText(String.format("$%.2f", total));
        } catch (NumberFormatException e) {
            lblTotal.setText("$0.00");
        }
    }

    private void cargarEnFormulario(OrdenLavado orden) {
        ordenSeleccionada = orden;

        cbCliente.getItems().stream()
                .filter(c -> c.getId() == orden.getIdCliente())
                .findFirst().ifPresent(cbCliente::setValue);

        cbServicio.getItems().stream()
                .filter(s -> s.getIdServicio() == orden.getIdServicio())
                .findFirst().ifPresent(cbServicio::setValue);

        dpFecha.setValue(orden.getFecha());
        txtCantidad.setText(String.valueOf(orden.getCantidad()));
        cbEstado.setValue(orden.getEstado());
        lblTotal.setText(String.format("$%.2f", orden.getTotal()));
    }

    private void limpiarFormulario() {
        ordenSeleccionada = null;
        cbCliente.setValue(null);
        cbServicio.setValue(null);
        dpFecha.setValue(LocalDate.now());
        txtCantidad.clear();
        cbEstado.setValue("Pendiente");
        lblTotal.setText("$0.00");
        lblMensaje.getStyleClass().setAll("error-label");
        lblMensaje.setText("");
        tablaOrdenes.getSelectionModel().clearSelection();
    }

    /**
     * Valida el formulario y devuelve una orden con los datos ingresados,
     * o null si hay campos inválidos (y muestra el mensaje de error).
     */
    private OrdenLavado leerFormulario() {
        Cliente cliente = cbCliente.getValue();
        Servicio servicio = cbServicio.getValue();
        LocalDate fecha = dpFecha.getValue();
        String estado = cbEstado.getValue();

        if (cliente == null || servicio == null || fecha == null || estado == null) {
            lblMensaje.setText("Completa cliente, servicio, fecha y estado.");
            return null;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                lblMensaje.setText("La cantidad debe ser mayor a 0.");
                return null;
            }
        } catch (NumberFormatException e) {
            lblMensaje.setText("La cantidad debe ser un número entero.");
            return null;
        }

        if (Sesion.getUsuarioActual() == null) {
            lblMensaje.setText("No hay sesión activa.");
            return null;
        }

        double total = cantidad * servicio.getPrecio();

        OrdenLavado orden = new OrdenLavado();
        orden.setIdCliente(cliente.getId());
        orden.setIdServicio(servicio.getIdServicio());
        orden.setIdUsuario(Sesion.getUsuarioActual().getId());
        orden.setFecha(fecha);
        orden.setCantidad(cantidad);
        orden.setTotal(total);
        orden.setEstado(estado);
        return orden;
    }

    @FXML
    private void onNuevoClick(ActionEvent event) {
        limpiarFormulario();
    }

    @FXML
    private void onGuardarClick(ActionEvent event) {
        OrdenLavado orden = leerFormulario();
        if (orden == null) {
            return;
        }

        ordenDAO.insertar(orden);
        lblMensaje.getStyleClass().setAll("success-label");
        lblMensaje.setText("Orden creada correctamente.");
        cargarOrdenes();
        limpiarFormulario();
    }

    @FXML
    private void onActualizarClick(ActionEvent event) {
        if (ordenSeleccionada == null) {
            lblMensaje.getStyleClass().setAll("error-label");
            lblMensaje.setText("Selecciona una orden de la tabla para actualizar.");
            return;
        }

        OrdenLavado orden = leerFormulario();
        if (orden == null) {
            return;
        }

        orden.setIdOrden(ordenSeleccionada.getIdOrden());
        ordenDAO.actualizar(orden);
        lblMensaje.getStyleClass().setAll("success-label");
        lblMensaje.setText("Orden actualizada correctamente.");
        cargarOrdenes();
        limpiarFormulario();
    }

    @FXML
    private void onEliminarClick(ActionEvent event) {
        if (ordenSeleccionada == null) {
            lblMensaje.getStyleClass().setAll("error-label");
            lblMensaje.setText("Selecciona una orden de la tabla para eliminar.");
            return;
        }

        ordenDAO.eliminar(ordenSeleccionada.getIdOrden());
        lblMensaje.getStyleClass().setAll("success-label");
        lblMensaje.setText("Orden eliminada correctamente.");
        cargarOrdenes();
        limpiarFormulario();
    }

    @FXML
    private void onVolverClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/lavanderia/dashboard-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Lavandería - Dashboard");
        } catch (IOException e) {
            lblMensaje.setText("Error al volver al dashboard: " + e.getMessage());
        }
    }
}
