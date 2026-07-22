package com.example.lavanderia.model;

import java.time.LocalDate;

public class OrdenLavado {
    private int idOrden;
    private int idCliente;
    private int idServicio;
    private int idUsuario;
    private LocalDate fecha;
    private int cantidad;
    private double precioUnitario;
    private double total;
    private String estado;
    private String observaciones;

    private String clienteNombre;
    private String servicioNombre;
    private String usuarioNombre;

    public OrdenLavado() {}

    public OrdenLavado(int idOrden, int idCliente, int idServicio,
                       int idUsuario, LocalDate fecha, int cantidad,
                       double precioUnitario, double total, String estado,
                       String observaciones) {
        this.idOrden = idOrden;
        this.idCliente = idCliente;
        this.idServicio = idServicio;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.total = total;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public int getIdOrden() { return idOrden; }
    public void setIdOrden(int idOrden) { this.idOrden = idOrden; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getServicioNombre() { return servicioNombre; }
    public void setServicioNombre(String servicioNombre) { this.servicioNombre = servicioNombre; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }
}
