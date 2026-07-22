package com.example.lavanderia.model;

public class Usuario extends Persona {
    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_CAJERO = "CAJERO";
    public static final String ROL_REPORTES = "REPORTES";

    private String usuario;
    private String password;
    private String rol;

    public Usuario() {}

    public Usuario(int id, String nombre, String correo, String telefono,
                   String usuario, String password, String rol) {
        super(id, nombre, correo, telefono);
        this.usuario = usuario;
        this.password = password;
        this.rol = rol;
    }

    @Override
    public String mostrarTipo() {
        return "Usuario del sistema con rol " + rol;
    }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
