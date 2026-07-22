-- Base de datos usada por Conexion.java: examen_poo
CREATE DATABASE IF NOT EXISTS examen_poo;
USE examen_poo;

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS servicios (
    id_servicio INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DOUBLE NOT NULL,
    stock INT NOT NULL,
    descripcion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS ordenes_lavado (
    id_orden INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_servicio INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha DATE NOT NULL,
    cantidad INT NOT NULL,
    total DOUBLE NOT NULL,
    estado VARCHAR(30) NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
    FOREIGN KEY (id_servicio) REFERENCES servicios(id_servicio),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- Datos de prueba
INSERT INTO usuarios (nombre) VALUES ('admin');
INSERT INTO clientes (nombre) VALUES ('Juan Pérez'), ('María Gómez');
INSERT INTO servicios (nombre, precio, stock, descripcion) VALUES
    ('Lavado normal', 3.50, 100, 'Lavado y secado estándar'),
    ('Lavado en seco', 6.00, 100, 'Para prendas delicadas');
