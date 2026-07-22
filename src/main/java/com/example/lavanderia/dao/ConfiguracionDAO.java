package com.example.lavanderia.dao;

import com.example.lavanderia.db.Conexion;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfiguracionDAO {
    public Map<String, String> listar() throws SQLException {
        Map<String, String> configuracion = new LinkedHashMap<>();
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT clave, valor FROM Configuracion ORDER BY clave");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                configuracion.put(rs.getString("clave"), rs.getString("valor"));
            }
        }
        return configuracion;
    }

    public void guardar(String clave, String valor) throws SQLException {
        String sql = """
                INSERT INTO Configuracion(clave, valor)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE valor = VALUES(valor)
                """;
        Connection cn = Conexion.getInstancia().getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, clave);
            ps.setString(2, valor);
            ps.executeUpdate();
        }
    }
}
