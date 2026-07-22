package com.example.lavanderia.dao;

import com.example.lavanderia.db.Conexion;
import com.example.lavanderia.model.Servicio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO implements ICRUD<Servicio> {

    @Override
    public boolean insertar(Servicio servicio) throws SQLException {
        String sql = """
                INSERT INTO Servicios(nombre, precio, stock, descripcion, activo)
                VALUES (?, ?, ?, ?, 1)
                """;
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            cargarParametros(ps, servicio, false);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Servicio> listar() throws SQLException {
        String sql = """
                SELECT idServicio, nombre, precio, stock, descripcion
                FROM Servicios
                WHERE activo = true
                ORDER BY nombre
                """;
        List<Servicio> servicios = new ArrayList<>();
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) servicios.add(mapear(rs));
        }
        return servicios;
    }

    @Override
    public Servicio buscar(int id) throws SQLException {
        String sql = """
                SELECT idServicio, nombre, precio, stock, descripcion
                FROM Servicios
                WHERE idServicio = ? AND activo = true
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
    public boolean actualizar(Servicio servicio) throws SQLException {
        String sql = """
                UPDATE Servicios
                SET nombre = ?, precio = ?, stock = ?, descripcion = ?
                WHERE idServicio = ? AND activo = true
                """;
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            cargarParametros(ps, servicio, true);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(
                "UPDATE Servicios SET activo = false WHERE idServicio = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existeNombre(String nombre, int idExcluir) throws SQLException {
        String sql = """
                SELECT COUNT(*) FROM Servicios
                WHERE activo = true AND LOWER(nombre) = LOWER(?) AND idServicio <> ?
                """;
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public int contarActivos() throws SQLException {
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT COUNT(*) FROM Servicios WHERE activo = true");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void cargarParametros(PreparedStatement ps, Servicio servicio, boolean incluirId)
            throws SQLException {
        ps.setString(1, servicio.getNombre());
        ps.setDouble(2, servicio.getPrecio());
        ps.setInt(3, servicio.getStock());
        ps.setString(4, servicio.getDescripcion());
        if (incluirId) ps.setInt(5, servicio.getIdServicio());
    }

    private Servicio mapear(ResultSet rs) throws SQLException {
        return new Servicio(
                rs.getInt("idServicio"),
                rs.getString("nombre"),
                rs.getDouble("precio"),
                rs.getInt("stock"),
                rs.getString("descripcion")
        );
    }
}
