package Vista;

import Controlador.*;
import Modelo.Clases.*;
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

            System.out.println("\n=== 9. PRUEBA PRÉSTAMO ===");
            PrestamoControlador preCtrl = new PrestamoControlador();
            Prestamo nuevo = preCtrl.crear(cli.getId(), emp.getId(), 500000, 5, 3);
            System.out.println("Creado: " + nuevo);
            System.out.println("Esperado -> total: 525000.0, cuota: 175000.0");

            preCtrl.cambiarEstado(nuevo.getId(), EstadoPrestamo.PAGADO);
            System.out.println("Estado en BD: " + preCtrl.buscarPorId(nuevo.getId()).getEstado()
                    + " (esperado PAGADO)");

            try {
                preCtrl.crear(99999, emp.getId(), 500000, 5, 3);
            } catch (Exception e) {
                System.out.println("OK, error esperado: " + e.getMessage());
            }
            try {
                preCtrl.crear(cli.getId(), emp.getId(), -100, 5, 3);
            } catch (Exception e) {
                System.out.println("OK, error esperado: " + e.getMessage());
            }

            System.out.println("\n=== 10. PRUEBA PAGOS ===");
            PagoControlador pagoCtrl = new PagoControlador();
            Prestamo p10 = preCtrl.crear(cli.getId(), emp.getId(), 600000, 0, 3);

            pagoCtrl.registrarPago(p10.getId(), 200000);
            System.out.println("Saldo tras 1er pago: " + pagoCtrl.consultarSaldo(p10.getId())
                    + " (esperado 400000.0)");

            try {
                pagoCtrl.registrarPago(p10.getId(), 500000);
            } catch (Exception e) {
                System.out.println("OK, error esperado: " + e.getMessage());
            }
            try {
                pagoCtrl.registrarPago(p10.getId(), -50);
            } catch (Exception e) {
                System.out.println("OK, error esperado: " + e.getMessage());
            }

            pagoCtrl.registrarPago(p10.getId(), 400000);
            System.out.println("Saldo final: " + pagoCtrl.consultarSaldo(p10.getId())
                    + " (esperado 0.0)");
            System.out.println("Estado: " + preCtrl.buscarPorId(p10.getId()).getEstado()
                    + " (esperado PAGADO)");

            try {
                pagoCtrl.registrarPago(p10.getId(), 1000);
            } catch (Exception e) {
                System.out.println("OK, error esperado: " + e.getMessage());
            }

            System.out.println("Histórico (esperado 2 pagos):");
            pagoCtrl.historial(p10.getId()).forEach(System.out::println);


            System.out.println("\n=== 11. PRUEBA REPORTES ===");

// Préstamo vencido: inicia hace 6 meses y dura 2 cuotas, así que venció hace 4 meses
            Prestamo vencido = new Prestamo(cli.getId(), emp.getId(), 300000, 10, 2,
                    LocalDate.now().minusMonths(6));
            prestamoDAO.guardar(vencido);

            ReporteControlador repCtrl = new ReporteControlador();

            System.out.println("Activos: " + repCtrl.prestamosActivos().size());
            System.out.println("Vencidos: " + repCtrl.prestamosVencidos().size());
            System.out.println("¿Aparece el vencido recién creado?: "
                    + repCtrl.prestamosVencidos().stream()
                    .anyMatch(p -> p.getId() == vencido.getId()) + " (esperado true)");

            System.out.println("Clientes morosos:");
            repCtrl.clientesMorosos().forEach(cm -> System.out.println("  " + c.obtenerResumen()));
            System.out.println("¿" + cli.getNombre() + " es moroso?: "
                    + repCtrl.clientesMorosos().stream()
                    .anyMatch(cm -> c.getId() == cli.getId()) + " (esperado true)");

            System.out.println("Total prestado: " + repCtrl.totalPrestado());
            System.out.println("Total recaudado: " + repCtrl.totalRecaudado());
            System.out.println("Préstamos por empleado: " + repCtrl.prestamosPorEmpleado());
            repCtrl.clienteMayorDeuda().ifPresent(cs ->
                    System.out.println("Mayor deuda: " + cs.obtenerResumen()));

            System.out.println("\nTodas las pruebas terminaron.");

        } catch (Exception e) {
            System.out.println("\nFALLÓ una prueba: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
