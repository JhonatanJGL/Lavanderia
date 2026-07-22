package com.example.lavanderia.controller;

import com.example.lavanderia.dao.ClienteDAO;
import com.example.lavanderia.dao.OrdenLavadoDAO;
import com.example.lavanderia.dao.ServicioDAO;
import com.example.lavanderia.model.Cliente;
import com.example.lavanderia.model.OrdenLavado;
import com.example.lavanderia.model.Servicio;
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
}
