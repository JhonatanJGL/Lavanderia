package com.example.lavanderia.util;

import com.example.lavanderia.model.Usuario;

/**
 * Guarda en memoria al usuario que inició sesión,
 * para que otras pantallas (Dashboard, Orden) sepan quién es.
 */
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
}
