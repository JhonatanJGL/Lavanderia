package com.example.lavanderia.app;

import com.example.lavanderia.db.Conexion;

public class PruebaConexion {
    public static void main(String[] args) {
        System.out.println(Conexion.getInstancia().probarConexion()
                ? "Conexión exitosa con Poo_lavanderia."
                : "No se pudo conectar con la base de datos.");
    }
}
