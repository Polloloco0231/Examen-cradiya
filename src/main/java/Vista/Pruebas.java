package Vista;

import Controlador.ClienteControlador;
import Controlador.EmpleadoControlador;
import Modelo.Clases.Cliente;
import Modelo.Clases.Empleado;
import Modelo.Clases.Pago;
import Modelo.Clases.Prestamo;
import Modelo.Excepciones.DatoInvalidoException;
import Modelo.Excepciones.PersistenciaException;
import Modelo.Excepciones.RegistroNoEncontradoExcepcion;
import Modelo.Persistencia.ClienteDAO;
import Modelo.Persistencia.ConexionBD;
import Modelo.Persistencia.PagoDAO;
import Modelo.Persistencia.PrestamoDAO;

import java.sql.Connection;
import java.time.LocalDate;

public class Pruebas {
    static void main() throws PersistenciaException, RegistroNoEncontradoExcepcion, DatoInvalidoException {
        // ---------- 1. CONEXIÓN ----------

        System.out.println("=== 1. Probando conexión ===");
        try (Connection con = ConexionBD.getConnection()) {
            System.out.println("Conexión exitosa a: " + con.getCatalog());
        } catch (Exception e) {
            System.out.println("FALLÓ la conexión: " + e.getMessage());
        }

        try {
            // ---------- 2. EMPLEADO (controlador) ----------
            System.out.println("\n=== 2. Empleado con controlador ===");
            EmpleadoControlador empCtrl = new EmpleadoControlador();
            Empleado emp = empCtrl.registrar("Ana Pérez", "E-" + System.currentTimeMillis(),
                    "ana@crediya.com", "Asesor", 2500000);
            System.out.println("Registrado: " + emp);
            System.out.println("Resumen: " + emp.obtenerResumen());

            // ---------- 3. CLIENTE (DAO directo) ----------
            System.out.println("\n=== 3. Cliente con DAO ===");
            ClienteDAO clienteDAO = new ClienteDAO();
            Cliente cli = new Cliente("Carlos Gómez", "C-" + System.currentTimeMillis(),
                    "carlos@mail.com", "3001234567");
            clienteDAO.guardar(cli);
            System.out.println("Registrado: " + cli);

            // ---------- 4. PRÉSTAMO ----------
            System.out.println("\n=== 4. Préstamo ===");
            PrestamoDAO prestamoDAO = new PrestamoDAO();
            Prestamo pre = new Prestamo(cli.getId(), emp.getId(), 1000000, 10, 5, LocalDate.now());
            prestamoDAO.guardar(pre);
            System.out.println("Registrado: " + pre);
            System.out.println("Esperado -> total: 1100000.0, cuota: 220000.0");

            // ---------- 5. PAGO ----------
            System.out.println("\n=== 5. Pago ===");
            PagoDAO pagoDAO = new PagoDAO();
            Pago pago = new Pago(pre.getId(), LocalDate.now(), 220000);
            pagoDAO.guardar(pago);
            pre.registrarAbono(220000);
            prestamoDAO.actualizar(pre);
            System.out.println("Pago registrado: " + pago);
            System.out.println("Saldo actual: " + pre.getSaldoPendiente() + " (esperado 880000.0)");

            // ---------- 6. CONSULTAS ----------
            System.out.println("\n=== 6. Consultas ===");
            System.out.println("Empleados en BD: " + empCtrl.listar().size());
            System.out.println("Clientes en BD: " + clienteDAO.listar().size());
            System.out.println("Préstamos del cliente " + cli.getId() + ": "
                    + prestamoDAO.listarPorCliente(cli.getId()).size());
            System.out.println("Pagos del préstamo " + pre.getId() + ":");
            pagoDAO.listarPorPrestamo(pre.getId()).forEach(System.out::println);

            // Releer desde la BD para confirmar que el saldo se guardó
            Prestamo releido = prestamoDAO.buscarPorId(pre.getId());
            System.out.println("Saldo leído de la BD: " + releido.getSaldoPendiente());
            System.out.println("¿Vencido?: " + releido.estaVencido() + " (esperado false)");

            // ---------- 7. VALIDACIONES ----------
            System.out.println("\n=== 7. Validaciones (deben mostrar error) ===");
            try {
                empCtrl.registrar("", "123", "x@x.com", "Asesor", 1000);
            } catch (Exception e) {
                System.out.println("OK, error esperado: " + e.getMessage());
            }
            try {
                empCtrl.registrar("Luis", "456", "x@x.com", "Asesor", -5);
            } catch (Exception e) {
                System.out.println("OK, error esperado: " + e.getMessage());
            }

            // ---------- 8. PRUEBA CLIENTE ----------
            System.out.println("\n=== 8. PRUEBA CLIENTE ===");

            ClienteControlador cliCtrl = new ClienteControlador();
            Cliente c = cliCtrl.registrar("Laura Ruiz", "L-" + System.currentTimeMillis(),
                    "laura@mail.com", "3109876543");
            System.out.println("Cliente: " + c.obtenerResumen());
            System.out.println("Préstamos de Laura: " + cliCtrl.consultarPrestamos(c.getId()).size());
            try {
                cliCtrl.buscarPorId(99999);
            } catch (Exception e) {
                System.out.println("OK, error esperado: " + e.getMessage());
            }


            System.out.println("\nTodas las pruebas terminaron.");

        } catch (Exception e) {
            System.out.println("\nFALLÓ una prueba: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
