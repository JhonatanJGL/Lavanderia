package com.example.lavanderia.dao;

import com.example.lavanderia.db.Conexion;
import com.example.lavanderia.model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements ICRUD<Cliente> {

    @Override
    public boolean insertar(Cliente cliente) throws SQLException {
        String sql = """
                INSERT INTO Clientes(nombre, correo, telefono, direccion, activo)
                VALUES (?, ?, ?, ?, 1)
                """;
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            cargarParametros(ps, cliente, false);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Cliente> listar() throws SQLException {
        String sql = """
                SELECT idCliente, nombre, correo, telefono, direccion
                FROM Clientes
                WHERE activo = true
                ORDER BY nombre
                """;
        List<Cliente> clientes = new ArrayList<>();
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) clientes.add(mapear(rs));
        }
        return clientes;
    }

    @Override
    public Cliente buscar(int id) throws SQLException {
        String sql = """
                SELECT idCliente, nombre, correo, telefono, direccion
                FROM Clientes
                WHERE idCliente = ? AND activo = true
                """;
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public boolean actualizar(Cliente cliente) throws SQLException {
        String sql = """
                UPDATE Clientes
                SET nombre = ?, correo = ?, telefono = ?, direccion = ?
                WHERE idCliente = ? AND activo = true
                """;
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            cargarParametros(ps, cliente, true);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(
                "UPDATE Clientes SET activo = false WHERE idCliente = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existeCorreo(String correo, int idExcluir) throws SQLException {
        String sql = """
                SELECT COUNT(*) FROM Clientes
                WHERE activo = true AND LOWER(correo) = LOWER(?) AND idCliente <> ?
                """;
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setInt(2, idExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public int contarActivos() throws SQLException {
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT COUNT(*) FROM Clientes WHERE activo = true");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void cargarParametros(PreparedStatement ps, Cliente cliente, boolean incluirId)
            throws SQLException {
        ps.setString(1, cliente.getNombre());
        ps.setString(2, cliente.getCorreo());
        ps.setString(3, cliente.getTelefono());
        ps.setString(4, cliente.getDireccion());
        if (incluirId) ps.setInt(5, cliente.getId());
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("idCliente"),
                rs.getString("nombre"),
                rs.getString("correo"),
                rs.getString("telefono"),
                rs.getString("direccion")
        );
    }
}
