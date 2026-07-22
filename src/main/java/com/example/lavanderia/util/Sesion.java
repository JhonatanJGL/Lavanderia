package com.example.lavanderia.util;

import com.example.lavanderia.model.Usuario;

public final class Sesion {
    private static Usuario usuarioActual;

    private Sesion() {}

    public static Usuario getUsuarioActual() { return usuarioActual; }
    public static void iniciar(Usuario usuario) { usuarioActual = usuario; }
    public static void cerrar() { usuarioActual = null; }
}
