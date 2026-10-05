package Vista;

import Controlador.PagoControlador;
import Modelo.Clases.Pago;
import Modelo.Clases.Prestamo;
import Modelo.Excepciones.PagoInvalidoException;
import Modelo.Excepciones.PersistenciaException;
import Modelo.Excepciones.RegistroNoEncontradoExcepcion;

import java.util.List;

public class MenuPago {

    private final PagoControlador controlador = new PagoControlador();

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n--- PAGOS ---");
            System.out.println("1. Registrar pago (abono)");
            System.out.println("2. Ver histórico de pagos de un préstamo");
            System.out.println("3. Consultar saldo de un préstamo");
            System.out.println("0. Volver");
            opcion = Entrada.leerEntero("Opción: ");

            switch (opcion) {
                case 1 -> registrar();
                case 2 -> historial();
                case 3 -> consultarSaldo();
                case 0 -> { }
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void registrar() {
        System.out.println("\n-- Registrar pago (escriba 0 en el id para cancelar) --");

        Prestamo prestamo = pedirPrestamo();
        if (prestamo == null) {
            System.out.println("Operación cancelada.");
            return;
        }

        System.out.printf("Saldo pendiente: $%,.2f   Cuota mensual: $%,.2f%n",
                prestamo.getSaldoPendiente(), prestamo.getCuotaMensual());
        double monto = pedirMonto(prestamo);

        try {
            Pago pago = controlador.registrarPago(prestamo.getId(), monto);
            double saldo = controlador.consultarSaldo(prestamo.getId());
            System.out.println("Pago registrado con id " + pago.getId() + ".");
            System.out.printf("Saldo restante: $%,.2f%n", saldo);
            if (saldo == 0) {
                System.out.println("¡Préstamo pagado en su totalidad!");
            }
        } catch (PagoInvalidoException | RegistroNoEncontradoExcepcion | PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    // Repite hasta que el préstamo exista y esté pendiente. Devuelve null si cancela.
    private Prestamo pedirPrestamo() {
        while (true) {
            int id = Entrada.leerEntero("Id del préstamo: ");
            if (id == 0) {
                return null;
            }
            try {
                return controlador.buscarPrestamoParaPago(id);
            } catch (RegistroNoEncontradoExcepcion | PagoInvalidoException ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (PersistenciaException ex) {
                System.out.println("Error: " + ex.getMessage());
                return null;
            }
        }
    }

    private double pedirMonto(Prestamo prestamo) {
        while (true) {
            double monto = Entrada.leerDecimal("Monto del abono: ");
            try {
                controlador.validarAbono(prestamo, monto);
                return monto;
            } catch (PagoInvalidoException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void historial() {
        int id = Entrada.leerEntero("Id del préstamo: ");
        try {
            List<Pago> pagos = controlador.historial(id);
            if (pagos.isEmpty()) {
                System.out.println("Este préstamo aún no tiene pagos.");
                return;
            }
            System.out.printf("%n%-8s %-12s %15s%n", "ID PAGO", "FECHA", "MONTO");
            pagos.forEach(p -> System.out.printf("%-8d %-12s %,15.2f%n",
                    p.getId(), p.getFechaPago(), p.getMonto()));
            double total = pagos.stream().mapToDouble(Pago::getMonto).sum();
            System.out.printf("Total pagado: $%,.2f en %d pago(s)%n", total, pagos.size());
        } catch (RegistroNoEncontradoExcepcion | PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void consultarSaldo() {
        int id = Entrada.leerEntero("Id del préstamo: ");
        try {
            double saldo = controlador.consultarSaldo(id);
            System.out.printf("Saldo pendiente del préstamo %d: $%,.2f%n", id, saldo);
        } catch (RegistroNoEncontradoExcepcion | PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
