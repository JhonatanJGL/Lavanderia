package com.example.lavanderia.util;

public final class Validaciones {
    private Validaciones() {}

    public static boolean textoVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    public static boolean correoValido(String correo) {
        return correo != null
                && correo.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    }

    public static int enteroPositivo(String texto) {
        int valor = Integer.parseInt(texto.trim());
        if (valor <= 0) throw new NumberFormatException();
        return valor;
    }

    public static double decimalPositivo(String texto) {
        double valor = Double.parseDouble(texto.trim().replace(',', '.'));
        if (valor <= 0) throw new NumberFormatException();
        return valor;
    }
}
