package com.example.lavanderia.dao;

import com.example.lavanderia.db.Conexion;
import com.example.lavanderia.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tabla esperada:
 * CREATE TABLE usuarios (
 *   id_usuario INT AUTO_INCREMENT PRIMARY KEY,
 *   nombre VARCHAR(100) NOT NULL UNIQUE
 * );
 *
 * Nota: el modelo Usuario (heredado de Persona) solo tiene id y nombre,
 * por eso el login de este proyecto valida únicamente por nombre de usuario.
 * Si tu profesor pide contraseña, se debe agregar el campo "contrasena"
 * tanto en la tabla como en el modelo Usuario.
 */
public class UsuarioDAO implements ICRUD<Usuario> {

    @Override
    public void insertar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre) VALUES (?)";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ? WHERE id_usuario = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setInt(2, usuario.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
        }
    }

    @Override
    public Usuario buscar(int id) {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getInt("id_usuario"), rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }

    /**
     * Usado por el LoginController para validar el ingreso por nombre de usuario.
     */
    public Usuario buscarPorNombre(String nombre) {
        String sql = "SELECT * FROM usuarios WHERE nombre = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getInt("id_usuario"), rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario por nombre: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Usuario(rs.getInt("id_usuario"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }
}
