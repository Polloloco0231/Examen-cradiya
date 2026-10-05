package Vista;

import Controlador.EmpleadoControlador;
import Controlador.ReporteControlador;
import Modelo.Clases.Cliente;
import Modelo.Clases.Empleado;
import Modelo.Clases.Prestamo;
import Modelo.Excepciones.PersistenciaException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MenuReporte {

    private final ReporteControlador controlador = new ReporteControlador();
    private final EmpleadoControlador empleadoControlador = new EmpleadoControlador();

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n--- REPORTES ---");
            System.out.println("1. Préstamos activos");
            System.out.println("2. Préstamos vencidos");
            System.out.println("3. Clientes morosos");
            System.out.println("4. Resumen de cartera (totales)");
            System.out.println("5. Préstamos por empleado");
            System.out.println("6. Cliente con mayor deuda");
            System.out.println("0. Volver");
            opcion = Entrada.leerEntero("Opción: ");

            switch (opcion) {
                case 1 -> activos();
                case 2 -> vencidos();
                case 3 -> morosos();
                case 4 -> resumen();
                case 5 -> porEmpleado();
                case 6 -> mayorDeuda();
                case 0 -> { }
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void activos() {
        try {
            imprimirPrestamos("PRÉSTAMOS ACTIVOS", controlador.prestamosActivos());
        } catch (PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void vencidos() {
        try {
            imprimirPrestamos("PRÉSTAMOS VENCIDOS", controlador.prestamosVencidos());
        } catch (PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void morosos() {
        try {
            List<Cliente> morosos = controlador.clientesMorosos();
            System.out.println("\n== CLIENTES MOROSOS ==");
            if (morosos.isEmpty()) {
                System.out.println("No hay clientes morosos.");
                return;
            }
            System.out.printf("%-5s %-22s %-14s %-15s%n", "ID", "NOMBRE", "DOCUMENTO", "TELÉFONO");
            morosos.forEach(c -> System.out.printf("%-5d %-22s %-14s %-15s%n",
                    c.getId(), c.getNombre(), c.getDocumento(),
                    c.getTelefono() == null ? "-" : c.getTelefono()));
            System.out.println("Total: " + morosos.size() + " cliente(s)");
        } catch (PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void resumen() {
        try {
            double prestado = controlador.totalPrestado();
            double recaudado = controlador.totalRecaudado();
            System.out.println("\n== RESUMEN DE CARTERA ==");
            System.out.printf("Total prestado:   $%,.2f%n", prestado);
            System.out.printf("Total recaudado:  $%,.2f%n", recaudado);
            System.out.printf("Préstamos activos:  %d%n", controlador.prestamosActivos().size());
            System.out.printf("Préstamos vencidos: %d%n", controlador.prestamosVencidos().size());
        } catch (PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void porEmpleado() {
        try {
            Map<Integer, Long> cantidades = controlador.prestamosPorEmpleado();
            System.out.println("\n== PRÉSTAMOS POR EMPLEADO ==");
            if (cantidades.isEmpty()) {
                System.out.println("No hay préstamos registrados.");
                return;
            }
            Map<Integer, String> nombres = empleadoControlador.listar().stream()
                    .collect(Collectors.toMap(Empleado::getId, Empleado::getNombre));

            System.out.printf("%-5s %-22s %10s%n", "ID", "EMPLEADO", "PRÉSTAMOS");
            cantidades.forEach((id, cantidad) -> System.out.printf("%-5d %-22s %10d%n",
                    id, nombres.getOrDefault(id, "(desconocido)"), cantidad));
        } catch (PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void mayorDeuda() {
        try {
            Optional<Cliente> cliente = controlador.clienteMayorDeuda();
            System.out.println("\n== CLIENTE CON MAYOR DEUDA ==");
            if (cliente.isPresent()) {
                System.out.println(cliente.get().obtenerResumen());
            } else {
                System.out.println("No hay clientes con deuda pendiente.");
            }
        } catch (PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void imprimirPrestamos(String titulo, List<Prestamo> lista) {
        System.out.println("\n== " + titulo + " ==");
        if (lista.isEmpty()) {
            System.out.println("No hay registros.");
            return;
        }
        System.out.printf("%-5s %-8s %-9s %14s %14s %-12s%n",
                "ID", "CLIENTE", "EMPLEADO", "MONTO", "SALDO", "VENCE");
        lista.forEach(p -> System.out.printf("%-5d %-8d %-9d %,14.2f %,14.2f %-12s%n",
                p.getId(), p.getClienteId(), p.getEmpleadoId(),
                p.getMonto(), p.getSaldoPendiente(), p.getFechaVencimiento()));
        System.out.println("Total: " + lista.size() + " préstamo(s)");
    }
}