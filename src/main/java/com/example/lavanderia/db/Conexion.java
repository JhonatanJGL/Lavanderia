package com.example.lavanderia.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {
    public static Connection conectar(){
        try{
            String url = "jdbc:mysql://localhost:3306/examen_poo";
            String user = "root";
            String password = "12345";

            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println("Conectado correctamente");
            return con;

        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }}
}
