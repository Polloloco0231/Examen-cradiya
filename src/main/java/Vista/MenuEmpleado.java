package Vista;

import Controlador.EmpleadoControlador;
import Modelo.Clases.Empleado;
import Modelo.Excepciones.DatoInvalidoException;
import Modelo.Excepciones.PersistenciaException;

import java.util.List;

public class MenuEmpleado {

    private final EmpleadoControlador controlador = new EmpleadoControlador();
    private static final String[] ROLES = {"Asesor", "Cobrador", "Cajero", "Analista", "Administrador"};

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n--- EMPLEADOS ---");
            System.out.println("1. Registrar empleado");
            System.out.println("2. Listar empleados");
            System.out.println("0. Volver");
            opcion = Entrada.leerEntero("Opción: ");

            switch (opcion) {
                case 1 -> registrar();
                case 2 -> listar();
                case 0 -> { }
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void registrar() {
        System.out.println("\n-- Registrar empleado --");
        String nombre = Entrada.leerTexto("Nombre: ", controlador::validarNombre);
        String documento = Entrada.leerTexto("Documento: ", controlador::validarDocumento);
        String correo = Entrada.leerTexto("Correo: ", controlador::validarCorreo);
        String rol = pedirRol();
        double salario = Entrada.leerDecimal("Salario: ", controlador::validarSalario);

        try {
            Empleado e = controlador.registrar(nombre, documento, correo, rol, salario);
            System.out.println("Empleado registrado con id " + e.getId() + ".");
        } catch (DatoInvalidoException | PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void listar() {
        try {
            List<Empleado> lista = controlador.listar();
            if (lista.isEmpty()) {
                System.out.println("No hay empleados registrados.");
                return;
            }
            System.out.printf("%n%-5s %-22s %-14s %-12s %15s%n",
                    "ID", "NOMBRE", "DOCUMENTO", "ROL", "SALARIO");
            lista.forEach(e -> System.out.printf("%-5d %-22s %-14s %-12s %,15.2f%n",
                    e.getId(), e.getNombre(), e.getDocumento(), e.getRol(), e.getSalario()));
        } catch (PersistenciaException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private String pedirRol() {
        System.out.println("Rol:");
        for (int i = 0; i < ROLES.length; i++) {
            System.out.println("  " + (i + 1) + ". " + ROLES[i]);
        }
        int opcion;
        do {
            opcion = Entrada.leerEntero("Elija el rol: ");
        } while (opcion < 1 || opcion > ROLES.length);
        return ROLES[opcion - 1];
    }
}
