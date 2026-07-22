package com.example.lavanderia.util;

import com.example.lavanderia.model.Usuario;

<<<<<<< HEAD
public final class Sesion {
    private static Usuario usuarioActual;

    private Sesion() {}

    public static Usuario getUsuarioActual() { return usuarioActual; }
    public static void iniciar(Usuario usuario) { usuarioActual = usuario; }
    public static void cerrar() { usuarioActual = null; }
=======
public class Sesion {

    private static Usuario usuarioActual;

    private Sesion() {
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
>>>>>>> 871deaad62a31c18124c8d8607235abbb12c644c
}
