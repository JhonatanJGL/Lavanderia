package com.example.lavanderia.controller;

import com.example.lavanderia.dao.OrdenLavadoDAO;
import com.example.lavanderia.model.OrdenLavado;
import com.example.lavanderia.util.Alertas;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public class ReporteController {
    @FXML private Label lblTotalOrdenes;
    @FXML private Label lblIngresos;
    @FXML private Label lblPendientes;
    @FXML private Label lblEntregadas;
    @FXML private PieChart graficoEstados;

    @FXML private TableView<OrdenLavado> tablaReporte;
    @FXML private TableColumn<OrdenLavado, Number> colId;
    @FXML private TableColumn<OrdenLavado, String> colCliente;
    @FXML private TableColumn<OrdenLavado, String> colServicio;
    @FXML private TableColumn<OrdenLavado, String> colFecha;
    @FXML private TableColumn<OrdenLavado, Number> colTotal;
    @FXML private TableColumn<OrdenLavado, String> colEstado;

    private final OrdenLavadoDAO ordenDAO = new OrdenLavadoDAO();
    private List<OrdenLavado> datosActuales = List.of();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getIdOrden()));
        colCliente.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getClienteNombre()));
        colServicio.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getServicioNombre()));
        colFecha.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getFecha().toString()));
        colTotal.setCellValueFactory(d ->
                new SimpleDoubleProperty(d.getValue().getTotal()));
        colEstado.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEstado()));
        cargarReporte();
    }

    @FXML
    private void actualizar() {
        cargarReporte();
    }

    @FXML
    private void exportarCSV() {
        if (datosActuales.isEmpty()) {
            Alertas.advertencia("Sin datos",
                    "No existen registros para exportar.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar reporte de órdenes");
        chooser.setInitialFileName("reporte_ordenes.csv");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo CSV", "*.csv"));

        File archivo = chooser.showSaveDialog(
                tablaReporte.getScene().getWindow());
        if (archivo == null) return;

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(archivo, StandardCharsets.UTF_8))) {
            writer.write(
                    "ID,Fecha,Cliente,Servicio,Cantidad,Total,Estado,Atendido por");
            writer.newLine();

            for (OrdenLavado o : datosActuales) {
                writer.write(String.format(
                        "%d,%s,\"%s\",\"%s\",%d,%.2f,%s,\"%s\"",
                        o.getIdOrden(),
                        o.getFecha(),
                        o.getClienteNombre(),
                        o.getServicioNombre(),
                        o.getCantidad(),
                        o.getTotal(),
                        o.getEstado(),
                        o.getUsuarioNombre()));
                writer.newLine();
            }

            Alertas.informacion("Reporte exportado",
                    "El archivo se guardó correctamente en:\n"
                            + archivo.getAbsolutePath());
        } catch (IOException e) {
            Alertas.error("Error de exportación", e.getMessage());
        }
    }

    private void cargarReporte() {
        try {
            lblTotalOrdenes.setText(String.valueOf(
                    ordenDAO.contarOrdenes()));
            lblIngresos.setText(String.format(
                    "$ %.2f", ordenDAO.totalIngresos()));
            lblPendientes.setText(String.valueOf(
                    ordenDAO.contarPorEstado("PENDIENTE")));
            lblEntregadas.setText(String.valueOf(
                    ordenDAO.contarPorEstado("ENTREGADO")));

            graficoEstados.setData(FXCollections.observableArrayList(
                    ordenDAO.contarEstados().entrySet().stream()
                            .map(e -> new PieChart.Data(
                                    e.getKey(), e.getValue()))
                            .toList()
            ));

            datosActuales = ordenDAO.listar();
            tablaReporte.setItems(
                    FXCollections.observableArrayList(datosActuales));
        } catch (SQLException e) {
            Alertas.error("Error de reporte", e.getMessage());
        }
    }
}
