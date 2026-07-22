package com.example.lavanderia.dao;

import com.example.lavanderia.db.Conexion;
import com.example.lavanderia.model.OrdenLavado;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrdenLavadoDAO implements ICRUD<OrdenLavado> {

    private static final String SELECT_BASE = """
            SELECT o.idOrden, o.idCliente, o.idServicio, o.idUsuario,
                   o.fecha, o.cantidad, o.precioUnitario, o.total,
                   o.estado, o.observaciones,
                   c.nombre AS clienteNombre,
                   s.nombre AS servicioNombre,
                   u.nombre AS usuarioNombre
            FROM OrdenesLavado o
            INNER JOIN Clientes c ON c.idCliente = o.idCliente
            INNER JOIN Servicios s ON s.idServicio = o.idServicio
            INNER JOIN Usuarios u ON u.idUsuario = o.idUsuario
            """;

    @Override
    public boolean insertar(OrdenLavado orden) throws SQLException {
        String insertarOrden = """
                INSERT INTO OrdenesLavado(
                    idCliente, idServicio, idUsuario, fecha, cantidad,
                    precioUnitario, total, estado, observaciones)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        String descontarStock = """
                UPDATE Servicios SET stock = stock - ?
                WHERE idServicio = ? AND activo = 1 AND stock >= ?
                """;

        Connection cn = Conexion.getInstancia().getConnection();
        boolean autoCommitOriginal = cn.getAutoCommit();
        try {
            cn.setAutoCommit(false);

            if (!"CANCELADO".equals(orden.getEstado())) {
                try (PreparedStatement stockPs = cn.prepareStatement(descontarStock)) {
                    stockPs.setInt(1, orden.getCantidad());
                    stockPs.setInt(2, orden.getIdServicio());
                    stockPs.setInt(3, orden.getCantidad());
                    if (stockPs.executeUpdate() == 0) {
                        throw new SQLException("Stock insuficiente para registrar la orden.");
                    }
                }
            }

            try (PreparedStatement ps = cn.prepareStatement(
                    insertarOrden, Statement.RETURN_GENERATED_KEYS)) {
                cargarParametros(ps, orden, false);
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("No se pudo insertar la orden.");
                }
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) orden.setIdOrden(keys.getInt(1));
                }
            }

            cn.commit();
            return true;
        } catch (SQLException e) {
            cn.rollback();
            throw e;
        } finally {
            cn.setAutoCommit(autoCommitOriginal);
        }
    }

    @Override
    public List<OrdenLavado> listar() throws SQLException {
        List<OrdenLavado> ordenes = new ArrayList<>();
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(
                SELECT_BASE + " ORDER BY o.fecha DESC, o.idOrden DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ordenes.add(mapear(rs));
        }
        return ordenes;
    }

    @Override
    public OrdenLavado buscar(int id) throws SQLException {
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(
                SELECT_BASE + " WHERE o.idOrden = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public boolean actualizar(OrdenLavado orden) throws SQLException {
        String consultarAnterior = """
                SELECT idServicio, cantidad, estado FROM OrdenesLavado
                WHERE idOrden = ? FOR UPDATE
                """;
        String devolverStock =
                "UPDATE Servicios SET stock = stock + ? WHERE idServicio = ?";
        String descontarStock = """
                UPDATE Servicios SET stock = stock - ?
                WHERE idServicio = ? AND activo = 1 AND stock >= ?
                """;
        String actualizarOrden = """
                UPDATE OrdenesLavado
                SET idCliente = ?, idServicio = ?, idUsuario = ?, fecha = ?,
                    cantidad = ?, precioUnitario = ?, total = ?, estado = ?, observaciones = ?
                WHERE idOrden = ?
                """;

        Connection cn = Conexion.getInstancia().getConnection();
        boolean autoCommitOriginal = cn.getAutoCommit();
        try {
            cn.setAutoCommit(false);

            int servicioAnterior;
            int cantidadAnterior;
            String estadoAnterior;
            try (PreparedStatement ps = cn.prepareStatement(consultarAnterior)) {
                ps.setInt(1, orden.getIdOrden());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException(
                            "La orden seleccionada ya no existe.");
                    servicioAnterior = rs.getInt("idServicio");
                    cantidadAnterior = rs.getInt("cantidad");
                    estadoAnterior = rs.getString("estado");
                }
            }

            if (!"CANCELADO".equals(estadoAnterior)) {
                try (PreparedStatement ps = cn.prepareStatement(devolverStock)) {
                    ps.setInt(1, cantidadAnterior);
                    ps.setInt(2, servicioAnterior);
                    ps.executeUpdate();
                }
            }

            if (!"CANCELADO".equals(orden.getEstado())) {
                try (PreparedStatement ps = cn.prepareStatement(descontarStock)) {
                    ps.setInt(1, orden.getCantidad());
                    ps.setInt(2, orden.getIdServicio());
                    ps.setInt(3, orden.getCantidad());
                    if (ps.executeUpdate() == 0) {
                        throw new SQLException("Stock insuficiente para actualizar la orden.");
                    }
                }
            }

            try (PreparedStatement ps = cn.prepareStatement(actualizarOrden)) {
                cargarParametros(ps, orden, true);
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("No se pudo actualizar la orden.");
                }
            }

            cn.commit();
            return true;
        } catch (SQLException e) {
            cn.rollback();
            throw e;
        } finally {
            cn.setAutoCommit(autoCommitOriginal);
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        String consultar =
                "SELECT idServicio, cantidad, estado FROM OrdenesLavado WHERE idOrden = ? FOR UPDATE";
        String eliminar = "DELETE FROM OrdenesLavado WHERE idOrden = ?";
        String devolverStock =
                "UPDATE Servicios SET stock = stock + ? WHERE idServicio = ?";

        Connection cn = Conexion.getInstancia().getConnection();
        boolean autoCommitOriginal = cn.getAutoCommit();
        try {
            cn.setAutoCommit(false);

            int idServicio;
            int cantidad;
            String estado;
            try (PreparedStatement ps = cn.prepareStatement(consultar)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException(
                            "La orden seleccionada no existe.");
                    idServicio = rs.getInt("idServicio");
                    cantidad = rs.getInt("cantidad");
                    estado = rs.getString("estado");
                }
            }

            try (PreparedStatement ps = cn.prepareStatement(eliminar)) {
                ps.setInt(1, id);
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("No se pudo eliminar la orden.");
                }
            }

            if (!"CANCELADO".equals(estado)) {
                try (PreparedStatement ps = cn.prepareStatement(devolverStock)) {
                    ps.setInt(1, cantidad);
                    ps.setInt(2, idServicio);
                    ps.executeUpdate();
                }
            }

            cn.commit();
            return true;
        } catch (SQLException e) {
            cn.rollback();
            throw e;
        } finally {
            cn.setAutoCommit(autoCommitOriginal);
        }
    }

    public int contarOrdenes() throws SQLException {
        return consultarEntero("SELECT COUNT(*) FROM OrdenesLavado");
    }

    public int contarPorEstado(String estado) throws SQLException {
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT COUNT(*) FROM OrdenesLavado WHERE estado = ?")) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public double totalIngresos() throws SQLException {
        String sql = """
                SELECT COALESCE(SUM(total), 0)
                FROM OrdenesLavado
                WHERE estado <> 'CANCELADO'
                """;
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    public Map<String, Integer> contarEstados() throws SQLException {
        String sql = """
                SELECT estado, COUNT(*) AS cantidad
                FROM OrdenesLavado
                GROUP BY estado
                ORDER BY estado
                """;
        Map<String, Integer> datos = new LinkedHashMap<>();
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) datos.put(
                    rs.getString("estado"), rs.getInt("cantidad"));
        }
        return datos;
    }

    private int consultarEntero(String sql) throws SQLException {
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void cargarParametros(PreparedStatement ps, OrdenLavado orden,
                                  boolean incluirId) throws SQLException {
        ps.setInt(1, orden.getIdCliente());
        ps.setInt(2, orden.getIdServicio());
        ps.setInt(3, orden.getIdUsuario());
        ps.setDate(4, Date.valueOf(orden.getFecha()));
        ps.setInt(5, orden.getCantidad());
        ps.setDouble(6, orden.getPrecioUnitario());
        ps.setDouble(7, orden.getTotal());
        ps.setString(8, orden.getEstado());
        ps.setString(9, orden.getObservaciones());
        if (incluirId) ps.setInt(10, orden.getIdOrden());
    }

    private OrdenLavado mapear(ResultSet rs) throws SQLException {
        OrdenLavado orden = new OrdenLavado(
                rs.getInt("idOrden"),
                rs.getInt("idCliente"),
                rs.getInt("idServicio"),
                rs.getInt("idUsuario"),
                rs.getDate("fecha").toLocalDate(),
                rs.getInt("cantidad"),
                rs.getDouble("precioUnitario"),
                rs.getDouble("total"),
                rs.getString("estado"),
                rs.getString("observaciones")
        );
        orden.setClienteNombre(rs.getString("clienteNombre"));
        orden.setServicioNombre(rs.getString("servicioNombre"));
        orden.setUsuarioNombre(rs.getString("usuarioNombre"));
        return orden;
    }
}
