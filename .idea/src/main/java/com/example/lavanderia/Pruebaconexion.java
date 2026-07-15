package com.example.lavanderia;

import com.example.lavanderia.db.Conexion;

public class Pruebaconexion {
    public static void main(String[] args) {
        if (Conexion.conectar()!=null){
            System.out.println("Conectado");

        }else{
            System.out.println("No conectado");
        }
    }
}
