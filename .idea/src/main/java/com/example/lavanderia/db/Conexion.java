package com.example.lavanderia.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexion {

    private static final String HOST = "jdbc:mysql://localhost:3306/";
    private static final String BASE_DATOS = "examen_poo";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "12345";

    /**
     * Conexión normal, apuntando ya a la base de datos "examen_poo".
     * La usan todos los DAO para hacer sus consultas.
     */
    public static Connection conectar() {
        try {
            Connection con = DriverManager.getConnection(HOST + BASE_DATOS, USUARIO, PASSWORD);
            return con;
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }

    /**
     * Crea la base de datos y las tablas si todavía no existen.
     * Se llama una sola vez al arrancar la aplicación (ver Main.java),
     * así el estudiante no tiene que ejecutar ningún script SQL a mano.
     */
    public static void inicializarBaseDatos() {
        // 1) Nos conectamos al servidor MySQL SIN indicar base de datos,
        //    porque "examen_poo" puede no existir todavía.
        try (Connection con = DriverManager.getConnection(HOST, USUARIO, PASSWORD);
             Statement st = con.createStatement()) {

            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + BASE_DATOS);

        } catch (SQLException e) {
            System.out.println("No se pudo crear la base de datos: " + e.getMessage());
            return;
        }

        // 2) Ahora sí nos conectamos a "examen_poo" y creamos las tablas.
        try (Connection con = conectar()) {
            if (con == null) {
                return;
            }

            try (Statement st = con.createStatement()) {
                st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS usuarios (
                            id_usuario INT AUTO_INCREMENT PRIMARY KEY,
                            nombre VARCHAR(100) NOT NULL UNIQUE
                        )
                        """);

                st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS clientes (
                            id_cliente INT AUTO_INCREMENT PRIMARY KEY,
                            nombre VARCHAR(100) NOT NULL
                        )
                        """);

                st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS servicios (
                            id_servicio INT AUTO_INCREMENT PRIMARY KEY,
                            nombre VARCHAR(100) NOT NULL,
                            precio DOUBLE NOT NULL,
                            stock INT NOT NULL,
                            descripcion VARCHAR(255)
                        )
                        """);

                st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS ordenes_lavado (
                            id_orden INT AUTO_INCREMENT PRIMARY KEY,
                            id_cliente INT NOT NULL,
                            id_servicio INT NOT NULL,
                            id_usuario INT NOT NULL,
                            fecha DATE NOT NULL,
                            cantidad INT NOT NULL,
                            total DOUBLE NOT NULL,
                            estado VARCHAR(30) NOT NULL,
                            FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
                            FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio),
                            FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
                        )
                        """);

                // 3) Datos de ejemplo, solo si las tablas están vacías,
                //    así puedes iniciar sesión y probar el CRUD de una vez.
                if (contarFilas(st, "usuarios") == 0) {
                    st.executeUpdate("INSERT INTO usuarios (nombre) VALUES ('admin')");
                }
                if (contarFilas(st, "clientes") == 0) {
                    st.executeUpdate("INSERT INTO clientes (nombre) VALUES ('Juan Pérez'), ('María Gómez')");
                }
                if (contarFilas(st, "servicios") == 0) {
                    st.executeUpdate("""
                            INSERT INTO servicios (nombre, precio, stock, descripcion) VALUES
                            ('Lavado normal', 3.50, 100, 'Lavado y secado estándar'),
                            ('Lavado en seco', 6.00, 100, 'Para prendas delicadas')
                            """);
                }
            }

        } catch (SQLException e) {
            System.out.println("No se pudieron crear las tablas: " + e.getMessage());
        }
    }

    private static int contarFilas(Statement st, String tabla) throws SQLException {
        var rs = st.executeQuery("SELECT COUNT(*) FROM " + tabla);
        rs.next();
        int total = rs.getInt(1);
        rs.close();
        return total;
    }
}
