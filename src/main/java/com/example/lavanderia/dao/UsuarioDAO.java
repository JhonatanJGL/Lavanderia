package com.example.lavanderia.dao;

import com.example.lavanderia.db.Conexion;
import com.example.lavanderia.model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO implements ICRUD<Usuario> {

    public Usuario autenticar(String nombreUsuario, String password) throws SQLException {
        String sql = """
                SELECT u.idUsuario, u.nombre, u.correo, u.telefono,
                       u.usuario, u.password, r.nombreRol
                FROM Usuarios u
                INNER JOIN Roles r ON r.idRol = u.idRol
                WHERE u.usuario = ? AND u.password = ? AND u.activo = 1
                """;
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public boolean insertar(Usuario usuario) throws SQLException {
        String sql = """
                INSERT INTO Usuarios(nombre, correo, telefono, usuario, password, idRol, activo)
                VALUES (?, ?, ?, ?, ?,
                        (SELECT idRol FROM Roles WHERE nombreRol = ?), 1)
                """;
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            cargarParametros(ps, usuario, false);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Usuario> listar() throws SQLException {
        String sql = """
                SELECT u.idUsuario, u.nombre, u.correo, u.telefono,
                       u.usuario, u.password, r.nombreRol
                FROM Usuarios u
                INNER JOIN Roles r ON r.idRol = u.idRol
                WHERE u.activo = 1
                ORDER BY u.nombre
                """;
        List<Usuario> usuarios = new ArrayList<>();
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) usuarios.add(mapear(rs));
        }
        return usuarios;
    }

    @Override
    public Usuario buscar(int id) throws SQLException {
        String sql = """
                SELECT u.idUsuario, u.nombre, u.correo, u.telefono,
                       u.usuario, u.password, r.nombreRol
                FROM Usuarios u
                INNER JOIN Roles r ON r.idRol = u.idRol
                WHERE u.idUsuario = ? AND u.activo = 1
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
    public boolean actualizar(Usuario usuario) throws SQLException {
        String sql = """
                UPDATE Usuarios
                SET nombre = ?, correo = ?, telefono = ?, usuario = ?, password = ?,
                    idRol = (SELECT idRol FROM Roles WHERE nombreRol = ?)
                WHERE idUsuario = ? AND activo = 1
                """;
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            cargarParametros(ps, usuario, true);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(
                "UPDATE Usuarios SET activo = 0 WHERE idUsuario = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existeUsuarioOCorreo(String usuario, String correo, int idExcluir)
            throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM Usuarios
                WHERE activo = 1
                  AND (LOWER(usuario) = LOWER(?) OR LOWER(correo) = LOWER(?))
                  AND idUsuario <> ?
                """;
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, correo);
            ps.setInt(3, idExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public int contarActivos() throws SQLException {
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT COUNT(*) FROM Usuarios WHERE activo = 1");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void cargarParametros(PreparedStatement ps, Usuario usuario, boolean incluirId)
            throws SQLException {
        ps.setString(1, usuario.getNombre());
        ps.setString(2, usuario.getCorreo());
        ps.setString(3, usuario.getTelefono());
        ps.setString(4, usuario.getUsuario());
        ps.setString(5, usuario.getPassword());
        ps.setString(6, usuario.getRol());
        if (incluirId) ps.setInt(7, usuario.getId());
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("idUsuario"),
                rs.getString("nombre"),
                rs.getString("correo"),
                rs.getString("telefono"),
                rs.getString("usuario"),
                rs.getString("password"),
                rs.getString("nombreRol")
        );
    }
}
