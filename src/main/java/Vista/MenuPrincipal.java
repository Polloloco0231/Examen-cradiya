package Vista;

import Controlador.ArchivoControlador;
import Modelo.Excepciones.PersistenciaException;

public class MenuPrincipal {

    private final MenuEmpleado menuEmpleado = new MenuEmpleado();
    private final MenuCliente menuCliente = new MenuCliente();
    private final MenuPrestamo menuPrestamo = new MenuPrestamo();
    private final MenuPago menuPago = new MenuPago();
    private final MenuReporte menuReporte = new MenuReporte();
    private final ArchivoControlador archivoControlador = new ArchivoControlador();

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n===== CREDIYA S.A.S. =====");
            System.out.println("1. Empleados");
            System.out.println("2. Clientes");
            System.out.println("3. Préstamos");
            System.out.println("4. Pagos");
            System.out.println("5. Reportes");
            System.out.println("6. Actualizar archivos de texto");
            System.out.println("0. Salir");
            opcion = Entrada.leerEntero("Opción: ");

            switch (opcion) {
                case 1 -> menuEmpleado.mostrar();
                case 2 -> menuCliente.mostrar();
                case 3 -> menuPrestamo.mostrar();
                case 4 -> menuPago.mostrar();
                case 5 -> menuReporte.mostrar();
                case 6 -> actualizarArchivos();
                case 0 -> System.out.println("Hasta pronto.");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void actualizarArchivos() {
        try {
            archivoControlador.sincronizarTodo();
            System.out.println("Archivos actualizados en la carpeta datos/.");
        } catch (PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
