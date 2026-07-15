package com.example.lavanderia.dao;

import com.example.lavanderia.db.Conexion;
import com.example.lavanderia.model.Servicio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tabla esperada:
 * CREATE TABLE servicios (
 *   id_servicio INT AUTO_INCREMENT PRIMARY KEY,
 *   nombre VARCHAR(100) NOT NULL,
 *   precio DOUBLE NOT NULL,
 *   stock INT NOT NULL,
 *   descripcion VARCHAR(255)
 * );
 */
public class ServicioDAO implements ICRUD<Servicio> {

    @Override
    public void insertar(Servicio servicio) {
        String sql = "INSERT INTO servicios (nombre, precio, stock, descripcion) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, servicio.getNombre());
            ps.setDouble(2, servicio.getPrecio());
            ps.setInt(3, servicio.getStock());
            ps.setString(4, servicio.getDescripcion());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al insertar servicio: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Servicio servicio) {
        String sql = "UPDATE servicios SET nombre = ?, precio = ?, stock = ?, descripcion = ? WHERE id_servicio = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, servicio.getNombre());
            ps.setDouble(2, servicio.getPrecio());
            ps.setInt(3, servicio.getStock());
            ps.setString(4, servicio.getDescripcion());
            ps.setInt(5, servicio.getIdServicio());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar servicio: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM servicios WHERE id_servicio = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar servicio: " + e.getMessage());
        }
    }

    @Override
    public Servicio buscar(int id) {
        String sql = "SELECT * FROM servicios WHERE id_servicio = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar servicio: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Servicio> listarTodos() {
        List<Servicio> lista = new ArrayList<>();
        String sql = "SELECT * FROM servicios ORDER BY nombre";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar servicios: " + e.getMessage());
        }
        return lista;
    }

    private Servicio mapear(ResultSet rs) throws SQLException {
        return new Servicio(
                rs.getInt("id_servicio"),
                rs.getString("nombre"),
                rs.getDouble("precio"),
                rs.getInt("stock"),
                rs.getString("descripcion")
        );
    }
}
