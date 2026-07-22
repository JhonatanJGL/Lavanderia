package com.example.lavanderia.dao;

import com.example.lavanderia.db.Conexion;
import com.example.lavanderia.model.OrdenLavado;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Tabla esperada:
 * CREATE TABLE ordenes_lavado (
 *   id_orden INT AUTO_INCREMENT PRIMARY KEY,
 *   id_cliente INT NOT NULL,
 *   id_servicio INT NOT NULL,
 *   id_usuario INT NOT NULL,
 *   fecha DATE NOT NULL,
 *   cantidad INT NOT NULL,
 *   total DOUBLE NOT NULL,
 *   estado VARCHAR(30) NOT NULL,
 *   FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
 *   FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio),
 *   FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
 * );
 */
public class OrdenLavadoDAO implements ICRUD<OrdenLavado> {

    @Override
    public void insertar(OrdenLavado orden) {
        String sql = "INSERT INTO ordenes_lavado (id_cliente, id_servicio, id_usuario, fecha, cantidad, total, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, orden.getIdCliente());
            ps.setInt(2, orden.getIdServicio());
            ps.setInt(3, orden.getIdUsuario());
            ps.setDate(4, Date.valueOf(orden.getFecha()));
            ps.setInt(5, orden.getCantidad());
            ps.setDouble(6, orden.getTotal());
            ps.setString(7, orden.getEstado());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    orden.setIdOrden(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar orden: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(OrdenLavado orden) {
        String sql = "UPDATE ordenes_lavado SET id_cliente = ?, id_servicio = ?, id_usuario = ?, " +
                "fecha = ?, cantidad = ?, total = ?, estado = ? WHERE id_orden = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orden.getIdCliente());
            ps.setInt(2, orden.getIdServicio());
            ps.setInt(3, orden.getIdUsuario());
            ps.setDate(4, Date.valueOf(orden.getFecha()));
            ps.setInt(5, orden.getCantidad());
            ps.setDouble(6, orden.getTotal());
            ps.setString(7, orden.getEstado());
            ps.setInt(8, orden.getIdOrden());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar orden: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM ordenes_lavado WHERE id_orden = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar orden: " + e.getMessage());
        }
    }

    @Override
    public OrdenLavado buscar(int id) {
        String sql = "SELECT * FROM ordenes_lavado WHERE id_orden = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar orden: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<OrdenLavado> listarTodos() {
        List<OrdenLavado> lista = new ArrayList<>();
        String sql = "SELECT * FROM ordenes_lavado ORDER BY id_orden DESC";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar ordenes: " + e.getMessage());
        }
        return lista;
    }

    private OrdenLavado mapear(ResultSet rs) throws SQLException {
        return new OrdenLavado(
                rs.getInt("id_orden"),
                rs.getInt("id_cliente"),
                rs.getInt("id_servicio"),
                rs.getInt("id_usuario"),
                rs.getDate("fecha").toLocalDate(),
                rs.getInt("cantidad"),
                rs.getDouble("total"),
                rs.getString("estado")
        );
    }
}
