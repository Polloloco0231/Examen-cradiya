package Vista;

import Controlador.PrestamoControlador;
import Controlador.ReporteControlador;
import Modelo.Clases.EstadoPrestamo;
import Modelo.Clases.Prestamo;
import Modelo.Excepciones.DatoInvalidoException;
import Modelo.Excepciones.PersistenciaException;
import Modelo.Excepciones.RegistroNoEncontradoExcepcion;

import java.util.List;

public class MenuPrestamo {

    private final PrestamoControlador controlador = new PrestamoControlador();
    private final ReporteControlador reporteControlador = new ReporteControlador();

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n--- PRÉSTAMOS ---");
            System.out.println("1. Crear préstamo");
            System.out.println("2. Listar préstamos");
            System.out.println("3. Consultar un préstamo");
            System.out.println("4. Cambiar estado");
            System.out.println("5. Buscar por estado");
            System.out.println("0. Volver");
            opcion = Entrada.leerEntero("Opción: ");

            switch (opcion) {
                case 1 -> crear();
                case 2 -> listar();
                case 3 -> consultar();
                case 4 -> cambiarEstado();
                case 5 -> buscarPorEstado();
                case 0 -> { }
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void crear() {
        System.out.println("\n-- Crear préstamo (escriba 0 en un id para cancelar) --");

        int clienteId = pedirCliente();
        if (clienteId == 0) {
            System.out.println("Operación cancelada.");
            return;
        }
        int empleadoId = pedirEmpleado();
        if (empleadoId == 0) {
            System.out.println("Operación cancelada.");
            return;
        }

        double monto = Entrada.leerDecimal("Monto: ", controlador::validarMonto);
        double interes = Entrada.leerDecimal("Interés total en % (ej. 10): ", controlador::validarInteres);
        int cuotas = Entrada.leerEntero("Número de cuotas (meses): ", controlador::validarCuotas);

        try {
            Prestamo p = controlador.crear(clienteId, empleadoId, monto, interes, cuotas);
            System.out.println("Préstamo creado con id " + p.getId() + ".");
            mostrarDetalle(p);
        } catch (DatoInvalidoException | RegistroNoEncontradoExcepcion | PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    // Repite hasta que el cliente exista. Devuelve 0 si el usuario cancela.
    private int pedirCliente() {
        while (true) {
            int id = Entrada.leerEntero("Id del cliente: ");
            if (id == 0) {
                return 0;
            }
            try {
                controlador.validarCliente(id);
                return id;
            } catch (RegistroNoEncontradoExcepcion ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (PersistenciaException ex) {
                System.out.println("Error: " + ex.getMessage());
                return 0;
            }
        }
    }

    private int pedirEmpleado() {
        while (true) {
            int id = Entrada.leerEntero("Id del empleado: ");
            if (id == 0) {
                return 0;
            }
            try {
                controlador.validarEmpleado(id);
                return id;
            } catch (RegistroNoEncontradoExcepcion ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (PersistenciaException ex) {
                System.out.println("Error: " + ex.getMessage());
                return 0;
            }
        }
    }

    private void listar() {
        try {
            List<Prestamo> lista = controlador.listar();
            if (lista.isEmpty()) {
                System.out.println("No hay préstamos registrados.");
                return;
            }
            System.out.printf("%n%-5s %-8s %-9s %14s %14s %14s %14s %-12s %-10s%n",
                    "ID", "CLIENTE", "EMPLEADO", "MONTO", "TOTAL", "CUOTA", "SALDO", "VENCE", "ESTADO");
            lista.forEach(p -> System.out.printf("%-5d %-8d %-9d %,14.2f %,14.2f %,14.2f %,14.2f %-12s %-10s%n",
                    p.getId(), p.getClienteId(), p.getEmpleadoId(), p.getMonto(),
                    p.getMontoTotal(), p.getCuotaMensual(), p.getSaldoPendiente(),
                    p.getFechaVencimiento(), p.getEstado()));
        } catch (PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void consultar() {
        int id = Entrada.leerEntero("Id del préstamo: ");
        try {
            mostrarDetalle(controlador.buscarPorId(id));
        } catch (RegistroNoEncontradoExcepcion | PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void cambiarEstado() {
        int id = Entrada.leerEntero("Id del préstamo: ");
        System.out.println("1. ACTIVO");
        System.out.println("2. PAGADO");
        System.out.println("3. MORA");
        int opcion;
        do {
            opcion = Entrada.leerEntero("Nuevo estado: ");
        } while (opcion < 1 || opcion > 2);
        EstadoPrestamo nuevo = (opcion == 1) ? EstadoPrestamo.ACTIVO : EstadoPrestamo.PAGADO;

        try {
            controlador.cambiarEstado(id, nuevo);
            System.out.println("Estado actualizado a " + nuevo + ".");
        } catch (DatoInvalidoException | RegistroNoEncontradoExcepcion | PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void buscarPorEstado() {
        try {
            List<Prestamo> lista = controlador.listar();
            if (lista.isEmpty()) {
                System.out.println("No hay préstamos registrados.");
                return;
            }

            System.out.println("1. ACTIVO");
            System.out.println("2. PAGADO");
            System.out.println("3. MORA");

            int id = Entrada.leerEntero("Seleccione el tipo de prestamos que quiere consultar: ");
            int opcion;

            System.out.printf("%n%-5s %-10s%n",
                    "ID","ESTADO");
            lista.forEach(p-> System.out.printf("%-5d %-10s%n",
                    p.getId(), p.getEstado()));

        } catch (PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void mostrarDetalle(Prestamo p) {
        System.out.printf("%n  Préstamo #%d  (cliente %d, empleado %d)%n",
                p.getId(), p.getClienteId(), p.getEmpleadoId());
        System.out.printf("  Monto prestado:  $%,.2f%n", p.getMonto());
        System.out.printf("  Interés:         %.2f%%%n", p.getInteres());
        System.out.printf("  Total a pagar:   $%,.2f%n", p.getMontoTotal());
        System.out.printf("  Cuota mensual:   $%,.2f  (%d cuotas)%n", p.getCuotaMensual(), p.getCuotas());
        System.out.printf("  Saldo pendiente: $%,.2f%n", p.getSaldoPendiente());
        System.out.printf("  Inicio: %s   Vence: %s%n", p.getFechaInicio(), p.getFechaVencimiento());
        System.out.printf("  Estado: %s%n", p.getEstado());
    }
}
