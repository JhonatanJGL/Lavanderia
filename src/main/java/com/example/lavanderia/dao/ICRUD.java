package com.example.lavanderia.dao;

import java.sql.SQLException;
import java.util.List;

public interface ICRUD<T> {
    boolean insertar(T objeto) throws SQLException;
    List<T> listar() throws SQLException;
    T buscar(int id) throws SQLException;
    boolean actualizar(T objeto) throws SQLException;
    boolean eliminar(int id) throws SQLException;
}
