package com.example.lavanderia.model;

public class Cliente extends Persona {
    private String direccion;

    public Cliente() {}

    public Cliente(int id, String nombre, String correo,
                   String telefono, String direccion) {
        super(id, nombre, correo, telefono);
        this.direccion = direccion;
    }

    @Override
    public String mostrarTipo() {
        return "Cliente de lavandería";
    }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    @Override
    public String toString() {
        return getNombre() + " - " + getTelefono();
    }
}
