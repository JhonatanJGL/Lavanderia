-- ==============================================================
-- SISTEMA DE LAVANDERÍA - PROYECTO FINAL POO
-- Adaptado de MySQL a PostgreSQL para Supabase
-- ==============================================================
-- Cambios respecto al script original (Poo_lavanderia.sql):
--   1. Se quita CREATE DATABASE / USE: en Supabase la base ya existe
--      (se llama "postgres"), solo se crean las tablas dentro de ella.
--   2. Se quita SET FOREIGN_KEY_CHECKS: no existe en Postgres; se usa
--      CASCADE en los DROP para poder borrar en cualquier orden.
--   3. AUTO_INCREMENT -> SERIAL (equivalente en Postgres).
--   4. CURRENT_DATE() -> CURRENT_DATE (en Postgres no lleva paréntesis).
-- La lógica, los nombres de tablas/columnas y los datos de ejemplo
-- son EXACTAMENTE los mismos que en el script original.
-- ==============================================================

DROP TABLE IF EXISTS OrdenesLavado CASCADE;
DROP TABLE IF EXISTS Configuracion CASCADE;
DROP TABLE IF EXISTS Servicios CASCADE;
DROP TABLE IF EXISTS Clientes CASCADE;
DROP TABLE IF EXISTS Usuarios CASCADE;
DROP TABLE IF EXISTS Roles CASCADE;

CREATE TABLE Roles (
    idRol SERIAL PRIMARY KEY,
    nombreRol VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE Usuarios (
    idUsuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(20) NOT NULL,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    idRol INT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (idRol) REFERENCES Roles(idRol)
);

CREATE TABLE Clientes (
    idCliente SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(20) NOT NULL,
    direccion VARCHAR(150) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE Servicios (
    idServicio SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    descripcion VARCHAR(250) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_servicio_precio CHECK (precio > 0),
    CONSTRAINT chk_servicio_stock CHECK (stock >= 0)
);

CREATE TABLE OrdenesLavado (
    idOrden SERIAL PRIMARY KEY,
    idCliente INT NOT NULL,
    idServicio INT NOT NULL,
    idUsuario INT NOT NULL,
    fecha DATE NOT NULL,
    cantidad INT NOT NULL,
    precioUnitario DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    observaciones VARCHAR(250),
    CONSTRAINT fk_orden_cliente
        FOREIGN KEY (idCliente) REFERENCES Clientes(idCliente),
    CONSTRAINT fk_orden_servicio
        FOREIGN KEY (idServicio) REFERENCES Servicios(idServicio),
    CONSTRAINT fk_orden_usuario
        FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario),
    CONSTRAINT chk_orden_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_orden_total CHECK (total > 0),
    CONSTRAINT chk_orden_estado CHECK (
        estado IN ('PENDIENTE', 'EN PROCESO', 'LISTO', 'ENTREGADO', 'CANCELADO')
    )
);

CREATE TABLE Configuracion (
    clave VARCHAR(60) PRIMARY KEY,
    valor VARCHAR(250) NOT NULL
);

CREATE INDEX idx_orden_fecha ON OrdenesLavado(fecha);
CREATE INDEX idx_orden_estado ON OrdenesLavado(estado);
CREATE INDEX idx_cliente_nombre ON Clientes(nombre);

INSERT INTO Roles(nombreRol) VALUES
('ADMIN'),
('CAJERO'),
('REPORTES');

-- Contraseñas de demostración: 123456
INSERT INTO Usuarios(nombre, correo, telefono, usuario, password, idRol) VALUES
('Administrador General', 'admin@aquaclean.com', '0991111111', 'admin', '123456',
    (SELECT idRol FROM Roles WHERE nombreRol = 'ADMIN')),
('Carlos Pérez', 'cajero@aquaclean.com', '0992222222', 'cajero', '123456',
    (SELECT idRol FROM Roles WHERE nombreRol = 'CAJERO')),
('Ana López', 'reportes@aquaclean.com', '0993333333', 'reportes', '123456',
    (SELECT idRol FROM Roles WHERE nombreRol = 'REPORTES'));

INSERT INTO Clientes(nombre, correo, telefono, direccion) VALUES
('Juan Pérez', 'juan.perez@gmail.com', '0994567890', 'Quito Norte'),
('María López', 'maria.lopez@gmail.com', '0987654321', 'Quito Sur'),
('Carlos Mendoza', 'carlos.mendoza@gmail.com', '0976543210', 'Centro Histórico');

INSERT INTO Servicios(nombre, precio, stock, descripcion) VALUES
('Lavado normal', 3.50, 100, 'Lavado básico de prendas de uso diario'),
('Lavado y planchado', 5.00, 80, 'Lavado completo con planchado'),
('Lavado en seco', 7.50, 50, 'Servicio para prendas delicadas'),
('Edredones y cobijas', 9.00, 30, 'Lavado de piezas de gran tamaño');

INSERT INTO OrdenesLavado(
    idCliente, idServicio, idUsuario, fecha, cantidad,
    precioUnitario, total, estado, observaciones)
VALUES
(1, 1, 2, CURRENT_DATE, 3, 3.50, 10.50, 'PENDIENTE',
 'Separar prendas blancas'),
(2, 2, 2, CURRENT_DATE, 2, 5.00, 10.00, 'EN PROCESO',
 'Entrega por la tarde'),
(3, 3, 2, CURRENT_DATE, 1, 7.50, 7.50, 'ENTREGADO',
 'Prenda delicada');

INSERT INTO Configuracion(clave, valor) VALUES
('empresa_nombre', 'AquaClean Lavandería'),
('empresa_ruc', '1799999999001'),
('empresa_direccion', 'Quito, Ecuador'),
('empresa_telefono', '0999999999');
