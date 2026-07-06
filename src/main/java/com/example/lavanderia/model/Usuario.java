package com.example.lavanderia.model;

public class Usuario extends Persona {

    public Usuario() {
    }

    public Usuario(int id, String nombre) {
        super(id, nombre);
    }

    @Override
    public String mostrarTipo() {
        return "Usuario";
    }
}