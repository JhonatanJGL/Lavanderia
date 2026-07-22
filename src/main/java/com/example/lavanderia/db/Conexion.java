package com.example.lavanderia.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Conexion {
    private static final Conexion INSTANCIA = new Conexion();

    private final String url;
    private final String usuario;
    private final String password;
    private Connection connection;

    private Conexion() {
        this.url = System.getenv().getOrDefault(
                "DB_URL",
                "jdbc:postgresql://aws-0-sa-east-1.pooler.supabase.com:5432/postgres"
        );
        this.usuario = System.getenv().getOrDefault(
                "DB_USER", "postgres.qziiuwdzxftuxgkyywtt");
        this.password = System.getenv().getOrDefault(
                "DB_PASSWORD", "Lavander1a23");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "No se encontró el driver de PostgreSQL. Revise el pom.xml.", e);
        }
    }

    public static Conexion getInstancia() {
        return INSTANCIA;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(2)) {
            connection = DriverManager.getConnection(url, usuario, password);
        }
        return connection;
    }

    public boolean probarConexion() {
        try {
            return getConnection().isValid(2);
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }

    public synchronized void cerrarConexion() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("No se pudo cerrar la conexión: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
