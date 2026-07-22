package com.example.lavanderia.controller;

import com.example.lavanderia.dao.ClienteDAO;
import com.example.lavanderia.dao.OrdenLavadoDAO;
import com.example.lavanderia.dao.ServicioDAO;
import com.example.lavanderia.dao.UsuarioDAO;
import com.example.lavanderia.util.Sesion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.SQLException;

public class InicioController {
    @FXML private Label lblBienvenida;
    @FXML private Label lblTotalOrdenes;
    @FXML private Label lblTotalClientes;
    @FXML private Label lblTotalServicios;
    @FXML private Label lblTotalUsuarios;

    @FXML
    public void initialize() {
        if (Sesion.getUsuarioActual() != null) {
            lblBienvenida.setText("¡Bienvenido, "
                    + Sesion.getUsuarioActual().getNombre() + "!");
        }
        try {
            lblTotalOrdenes.setText(String.valueOf(
                    new OrdenLavadoDAO().contarOrdenes()));
            lblTotalClientes.setText(String.valueOf(
                    new ClienteDAO().contarActivos()));
            lblTotalServicios.setText(String.valueOf(
                    new ServicioDAO().contarActivos()));
            lblTotalUsuarios.setText(String.valueOf(
                    new UsuarioDAO().contarActivos()));
        } catch (SQLException e) {
            lblTotalOrdenes.setText("-");
            lblTotalClientes.setText("-");
            lblTotalServicios.setText("-");
            lblTotalUsuarios.setText("-");
        }
    }
}
