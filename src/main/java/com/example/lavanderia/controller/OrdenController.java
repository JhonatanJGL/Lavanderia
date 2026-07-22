package com.example.lavanderia.controller;

import com.example.lavanderia.dao.ClienteDAO;
import com.example.lavanderia.dao.OrdenLavadoDAO;
import com.example.lavanderia.dao.ServicioDAO;
import com.example.lavanderia.model.Cliente;
import com.example.lavanderia.model.OrdenLavado;
import com.example.lavanderia.model.Servicio;
<<<<<<< HEAD
import com.example.lavanderia.util.Alertas;
import com.example.lavanderia.util.Sesion;
import com.example.lavanderia.util.Validaciones;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.sql.SQLException;
import java.time.LocalDate;

public class OrdenController {
    @FXML private ComboBox<Cliente> comboCliente;
    @FXML private ComboBox<Servicio> comboServicio;
    @FXML private DatePicker fechaOrden;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<String> comboEstado;
    @FXML private TextArea txtObservaciones;
    @FXML private Label lblTotal;
    @FXML private TextField txtBuscar;

    @FXML private TableView<OrdenLavado> tablaOrdenes;
    @FXML private TableColumn<OrdenLavado, Number> colId;
    @FXML private TableColumn<OrdenLavado, String> colCliente;
    @FXML private TableColumn<OrdenLavado, String> colServicio;
    @FXML private TableColumn<OrdenLavado, String> colFecha;
    @FXML private TableColumn<OrdenLavado, Number> colCantidad;
    @FXML private TableColumn<OrdenLavado, Number> colTotal;
    @FXML private TableColumn<OrdenLavado, String> colEstado;
    @FXML private Button btnGuardar;

    private final OrdenLavadoDAO ordenDAO = new OrdenLavadoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ServicioDAO servicioDAO = new ServicioDAO();
    private int idSeleccionado;

    @FXML
    public void initialize() {
        comboEstado.setItems(FXCollections.observableArrayList(
                "PENDIENTE", "EN PROCESO", "LISTO",
                "ENTREGADO", "CANCELADO"));
        comboEstado.setValue("PENDIENTE");
        fechaOrden.setValue(LocalDate.now());

        colId.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getIdOrden()));
        colCliente.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getClienteNombre()));
        colServicio.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getServicioNombre()));
        colFecha.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getFecha().toString()));
        colCantidad.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getCantidad()));
        colTotal.setCellValueFactory(d ->
                new SimpleDoubleProperty(d.getValue().getTotal()));
        colEstado.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEstado()));

        colTotal.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : String.format("$ %.2f", item.doubleValue()));
            }
        });

        tablaOrdenes.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, actual) -> cargarSeleccion(actual));
        comboServicio.valueProperty()
                .addListener((obs, anterior, actual) -> calcularTotal());
        txtCantidad.textProperty()
                .addListener((obs, anterior, actual) -> calcularTotal());
        txtBuscar.textProperty()
                .addListener((obs, anterior, actual) -> cargarOrdenes(actual));

        recargarCombos();
        cargarOrdenes("");
    }

    @FXML
    private void guardar() {
        Cliente cliente = comboCliente.getValue();
        Servicio servicio = comboServicio.getValue();

        if (cliente == null || servicio == null
                || fechaOrden.getValue() == null
                || comboEstado.getValue() == null
                || Validaciones.textoVacio(txtCantidad.getText())) {
            Alertas.advertencia("Campos incompletos",
                    "Seleccione cliente, servicio, fecha, estado y cantidad.");
            return;
        }

        if (Sesion.getUsuarioActual() == null) {
            Alertas.error("Sesión inválida",
                    "Cierre la aplicación e inicie sesión nuevamente.");
            return;
        }

        try {
            int cantidad = Validaciones.enteroPositivo(txtCantidad.getText());
            double total = cantidad * servicio.getPrecio();

            OrdenLavado orden = new OrdenLavado(
                    idSeleccionado,
                    cliente.getId(),
                    servicio.getIdServicio(),
                    Sesion.getUsuarioActual().getId(),
                    fechaOrden.getValue(),
                    cantidad,
                    servicio.getPrecio(),
                    total,
                    comboEstado.getValue(),
                    txtObservaciones.getText() == null
                            ? "" : txtObservaciones.getText().trim()
            );

            boolean correcto = idSeleccionado == 0
                    ? ordenDAO.insertar(orden)
                    : ordenDAO.actualizar(orden);

            if (correcto) {
                Alertas.informacion("Operación exitosa",
                        idSeleccionado == 0
                                ? "Orden registrada."
                                : "Orden actualizada.");
                limpiar();
                recargarCombos();
                cargarOrdenes(txtBuscar.getText());
            }
        } catch (NumberFormatException e) {
            Alertas.error("Cantidad inválida",
                    "La cantidad debe ser un entero mayor a cero.");
        } catch (SQLException e) {
            Alertas.error("No se pudo guardar la orden", e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        OrdenLavado orden =
                tablaOrdenes.getSelectionModel().getSelectedItem();
        if (orden == null) {
            Alertas.advertencia("Sin selección",
                    "Seleccione una orden en la tabla.");
            return;
        }

        if (!Alertas.confirmar("Eliminar orden",
                "¿Desea eliminar la orden #" + orden.getIdOrden()
                        + "?\nEl stock utilizado será devuelto.")) return;

        try {
            ordenDAO.eliminar(orden.getIdOrden());
            limpiar();
            recargarCombos();
            cargarOrdenes(txtBuscar.getText());
            Alertas.informacion("Orden eliminada",
                    "La orden fue eliminada correctamente.");
        } catch (SQLException e) {
            Alertas.error("No se pudo eliminar", e.getMessage());
        }
    }

    @FXML
    private void imprimirComprobante() {
        OrdenLavado orden =
                tablaOrdenes.getSelectionModel().getSelectedItem();
        if (orden == null) {
            Alertas.advertencia("Sin selección",
                    "Seleccione una orden para imprimir.");
            return;
        }

        VBox comprobante = new VBox(8);
        comprobante.setPadding(new Insets(28));
        Label titulo = new Label("AquaClean Lavandería");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        comprobante.getChildren().addAll(
                titulo,
                new Label("COMPROBANTE DE ORDEN #" + orden.getIdOrden()),
                new Separator(),
                new Label("Cliente: " + orden.getClienteNombre()),
                new Label("Servicio: " + orden.getServicioNombre()),
                new Label("Fecha: " + orden.getFecha()),
                new Label("Cantidad: " + orden.getCantidad()),
                new Label(String.format(
                        "Precio unitario: $ %.2f", orden.getPrecioUnitario())),
                new Label(String.format("TOTAL: $ %.2f", orden.getTotal())),
                new Label("Estado: " + orden.getEstado()),
                new Label("Atendido por: " + orden.getUsuarioNombre()),
                new Separator(),
                new Label("Gracias por preferirnos")
        );

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            Alertas.error("Impresión no disponible",
                    "No se encontró un servicio de impresión.");
            return;
        }

        if (job.showPrintDialog(tablaOrdenes.getScene().getWindow())) {
            boolean impreso = job.printPage(comprobante);
            if (impreso) {
                job.endJob();
                Alertas.informacion("Comprobante",
                        "Comprobante enviado a la impresora.");
            }
        }
    }

    @FXML
    private void limpiar() {
        idSeleccionado = 0;
        comboCliente.getSelectionModel().clearSelection();
        comboServicio.getSelectionModel().clearSelection();
        fechaOrden.setValue(LocalDate.now());
        txtCantidad.clear();
        comboEstado.setValue("PENDIENTE");
        txtObservaciones.clear();
        lblTotal.setText("$ 0.00");
        tablaOrdenes.getSelectionModel().clearSelection();
        btnGuardar.setText("Registrar orden");
    }

    private void calcularTotal() {
        Servicio servicio = comboServicio.getValue();
        if (servicio == null
                || Validaciones.textoVacio(txtCantidad.getText())) {
            lblTotal.setText("$ 0.00");
            return;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            double total = cantidad > 0
                    ? cantidad * servicio.getPrecio() : 0;
            lblTotal.setText(String.format("$ %.2f", total));
        } catch (NumberFormatException e) {
            lblTotal.setText("$ 0.00");
        }
    }

    private void cargarSeleccion(OrdenLavado orden) {
        if (orden == null) return;
        idSeleccionado = orden.getIdOrden();
        seleccionarCliente(orden.getIdCliente());
        seleccionarServicio(orden.getIdServicio());
        fechaOrden.setValue(orden.getFecha());
        txtCantidad.setText(String.valueOf(orden.getCantidad()));
        comboEstado.setValue(orden.getEstado());
        txtObservaciones.setText(orden.getObservaciones());
        lblTotal.setText(String.format("$ %.2f", orden.getTotal()));
        btnGuardar.setText("Actualizar orden");
    }

    private void recargarCombos() {
        try {
            comboCliente.setItems(FXCollections.observableArrayList(
                    clienteDAO.listar()));
            comboServicio.setItems(FXCollections.observableArrayList(
                    servicioDAO.listar()));
        } catch (SQLException e) {
            Alertas.error("Error",
                    "No se pudieron cargar clientes y servicios: "
                            + e.getMessage());
        }
    }

    private void cargarOrdenes(String filtro) {
        try {
            String texto = filtro == null
                    ? "" : filtro.trim().toLowerCase();
            var datos = ordenDAO.listar().stream()
                    .filter(o -> texto.isBlank()
                            || o.getClienteNombre().toLowerCase().contains(texto)
                            || o.getServicioNombre().toLowerCase().contains(texto)
                            || o.getEstado().toLowerCase().contains(texto)
                            || String.valueOf(o.getIdOrden()).contains(texto))
                    .toList();
            tablaOrdenes.setItems(FXCollections.observableArrayList(datos));
        } catch (SQLException e) {
            Alertas.error("Error",
                    "No se pudieron cargar las órdenes: " + e.getMessage());
        }
    }

    private void seleccionarCliente(int idCliente) {
        comboCliente.getItems().stream()
                .filter(c -> c.getId() == idCliente)
                .findFirst()
                .ifPresent(comboCliente::setValue);
    }

    private void seleccionarServicio(int idServicio) {
        comboServicio.getItems().stream()
                .filter(s -> s.getIdServicio() == idServicio)
                .findFirst()
                .ifPresent(comboServicio::setValue);
    }
=======
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
        lblMensaje.setStyle("-fx-text-fill: #2e7d32;");
        lblMensaje.setText("Orden creada correctamente.");
        cargarOrdenes();
        limpiarFormulario();
    }

    @FXML
    private void onActualizarClick(ActionEvent event) {
        if (ordenSeleccionada == null) {
            lblMensaje.setStyle("-fx-text-fill: #c62828;");
            lblMensaje.setText("Selecciona una orden de la tabla para actualizar.");
            return;
        }

        OrdenLavado orden = leerFormulario();
        if (orden == null) {
            return;
        }

        orden.setIdOrden(ordenSeleccionada.getIdOrden());
        ordenDAO.actualizar(orden);
        lblMensaje.setStyle("-fx-text-fill: #2e7d32;");
        lblMensaje.setText("Orden actualizada correctamente.");
        cargarOrdenes();
        limpiarFormulario();
    }

    @FXML
    private void onEliminarClick(ActionEvent event) {
        if (ordenSeleccionada == null) {
            lblMensaje.setStyle("-fx-text-fill: #c62828;");
            lblMensaje.setText("Selecciona una orden de la tabla para eliminar.");
            return;
        }

        ordenDAO.eliminar(ordenSeleccionada.getIdOrden());
        lblMensaje.setStyle("-fx-text-fill: #2e7d32;");
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
>>>>>>> 871deaad62a31c18124c8d8607235abbb12c644c
}
