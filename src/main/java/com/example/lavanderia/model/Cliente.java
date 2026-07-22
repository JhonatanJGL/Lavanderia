package com.example.lavanderia.model;

public class Cliente extends Persona {

    public Cliente() {
    }

    public Cliente(int id, String nombre) {
        super(id, nombre);
    }

    @Override
    public String mostrarTipo() {
        return "Cliente";
    }
}