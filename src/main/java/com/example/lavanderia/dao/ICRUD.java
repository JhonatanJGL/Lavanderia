package com.example.lavanderia.dao;

public interface ICRUD<T> {

    void insertar(T objeto);

    void actualizar(T objeto);

    void eliminar(int id);

    T buscar(int id);
}