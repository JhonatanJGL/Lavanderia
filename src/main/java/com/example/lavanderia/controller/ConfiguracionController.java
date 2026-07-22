package com.example.lavanderia.controller;

import com.example.lavanderia.dao.ConfiguracionDAO;
import com.example.lavanderia.util.Alertas;
import com.example.lavanderia.util.Validaciones;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.sql.SQLException;
import java.util.Map;

public class ConfiguracionController {
    @FXML private TextField txtEmpresa;
    @FXML private TextField txtRuc;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtTelefono;

    private final ConfiguracionDAO configuracionDAO =
            new ConfiguracionDAO();

    @FXML
    public void initialize() {
        cargar();
    }

    @FXML
    private void guardar() {
        if (Validaciones.textoVacio(txtEmpresa.getText())
                || Validaciones.textoVacio(txtRuc.getText())
                || Validaciones.textoVacio(txtDireccion.getText())
                || Validaciones.textoVacio(txtTelefono.getText())) {
            Alertas.advertencia("Campos incompletos",
                    "Complete todos los parámetros de la empresa.");
            return;
        }

        try {
            configuracionDAO.guardar(
                    "empresa_nombre", txtEmpresa.getText().trim());
            configuracionDAO.guardar(
                    "empresa_ruc", txtRuc.getText().trim());
            configuracionDAO.guardar(
                    "empresa_direccion", txtDireccion.getText().trim());
            configuracionDAO.guardar(
                    "empresa_telefono", txtTelefono.getText().trim());

            Alertas.informacion("Configuración guardada",
                    "Los datos generales fueron actualizados.");
        } catch (SQLException e) {
            Alertas.error("Error", e.getMessage());
        }
    }

    private void cargar() {
        try {
            Map<String, String> datos = configuracionDAO.listar();
            txtEmpresa.setText(datos.getOrDefault(
                    "empresa_nombre", "AquaClean Lavandería"));
            txtRuc.setText(datos.getOrDefault(
                    "empresa_ruc", "1799999999001"));
            txtDireccion.setText(datos.getOrDefault(
                    "empresa_direccion", "Quito, Ecuador"));
            txtTelefono.setText(datos.getOrDefault(
                    "empresa_telefono", "0999999999"));
        } catch (SQLException e) {
            Alertas.error("Error",
                    "No se pudo cargar la configuración: " + e.getMessage());
        }
    }
}
