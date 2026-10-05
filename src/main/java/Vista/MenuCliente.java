package Vista;

import Controlador.ClienteControlador;
import Modelo.Clases.Cliente;
import Modelo.Clases.Prestamo;
import Modelo.Excepciones.DatoInvalidoException;
import Modelo.Excepciones.PersistenciaException;
import Modelo.Excepciones.RegistroNoEncontradoExcepcion;

import java.util.List;

public class MenuCliente {

    private final ClienteControlador controlador = new ClienteControlador();

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n--- CLIENTES ---");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Consultar préstamos de un cliente");
            System.out.println("0. Volver");
            opcion = Entrada.leerEntero("Opción: ");

            switch (opcion) {
                case 1 -> registrar();
                case 2 -> listar();
                case 3 -> consultarPrestamos();
                case 0 -> { }
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void registrar() {
        System.out.println("\n-- Registrar cliente --");
        String nombre = Entrada.leerTexto("Nombre: ", controlador::validarNombre);
        String documento = Entrada.leerTexto("Documento: ", controlador::validarDocumento);
        String correo = Entrada.leerTexto("Correo (opcional): ", controlador::validarCorreo);
        String telefono = Entrada.leerTexto("Teléfono (opcional): ", controlador::validarTelefono);

        try {
            Cliente c = controlador.registrar(nombre, documento, correo, telefono);
            System.out.println("Cliente registrado con id " + c.getId() + ".");
        } catch (DatoInvalidoException | PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void listar() {
        try {
            List<Cliente> lista = controlador.listar();
            if (lista.isEmpty()) {
                System.out.println("No hay clientes registrados.");
                return;
            }
            System.out.printf("%n%-5s %-22s %-14s %-26s %-15s%n",
                    "ID", "NOMBRE", "DOCUMENTO", "CORREO", "TELÉFONO");
            lista.forEach(c -> System.out.printf("%-5d %-22s %-14s %-26s %-15s%n",
                    c.getId(), c.getNombre(), c.getDocumento(),
                    c.getCorreo() == null ? "-" : c.getCorreo(),
                    c.getTelefono() == null ? "-" : c.getTelefono()));
        } catch (PersistenciaException ex) {
        System.out.println("Error: " + ex.getMessage());
        }
    }

    private void consultarPrestamos() {
        int id = Entrada.leerEntero("Id del cliente: ");
        try {
            Cliente c = controlador.buscarPorId(id);
            List<Prestamo> prestamos = controlador.consultarPrestamos(id);

            System.out.println("\nCliente: " + c.obtenerResumen());
            if (prestamos.isEmpty()) {
                System.out.println("Este cliente no tiene préstamos.");
                return;
            }
            System.out.printf("%n%-5s %14s %14s %14s %14s %-12s %-10s%n",
                    "ID", "MONTO", "TOTAL", "CUOTA", "SALDO", "VENCE", "ESTADO");
            prestamos.forEach(p -> System.out.printf("%-5d %,14.2f %,14.2f %,14.2f %,14.2f %-12s %-10s%n",
                    p.getId(), p.getMonto(), p.getMontoTotal(), p.getCuotaMensual(),
                    p.getSaldoPendiente(), p.getFechaVencimiento(), p.getEstado()));
        } catch (RegistroNoEncontradoExcepcion | PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
