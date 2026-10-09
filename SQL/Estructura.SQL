DROP DATABASE IF EXISTS crediya_db;
CREATE DATABASE crediya_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE crediya_db;

CREATE TABLE empleados (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(80) NOT NULL,
  documento VARCHAR(30) NOT NULL UNIQUE,
  rol VARCHAR(30) NOT NULL,
  correo VARCHAR(80),
  salario DECIMAL(10,2) NOT NULL
);

CREATE TABLE clientes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(80) NOT NULL,
  documento VARCHAR(30) NOT NULL UNIQUE,
  correo VARCHAR(80),
  telefono VARCHAR(20)
);

CREATE TABLE prestamos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  cliente_id INT NOT NULL,
  empleado_id INT NOT NULL,
  monto DECIMAL(12,2) NOT NULL,
  interes DECIMAL(5,2) NOT NULL,
  cuotas INT NOT NULL,
  monto_total DECIMAL(12,2) NOT NULL,
  cuota_mensual DECIMAL(12,2) NOT NULL,
  saldo_pendiente DECIMAL(12,2) NOT NULL,
  fecha_inicio DATE NOT NULL,
  fecha_vencimiento DATE NOT NULL,
  estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
  FOREIGN KEY (cliente_id) REFERENCES clientes(id),
  FOREIGN KEY (empleado_id) REFERENCES empleados(id)
);

CREATE TABLE pagos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  prestamo_id INT NOT NULL,
  fecha_pago DATE NOT NULL,
  monto DECIMAL(10,2) NOT NULL,
  FOREIGN KEY (prestamo_id) REFERENCES prestamos(id)
);