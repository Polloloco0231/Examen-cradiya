package Vista;

import Modelo.Excepciones.DatoInvalidoException;

import java.util.Scanner;

public class Entrada {

    private static final Scanner sc = new Scanner(System.in);

    private Entrada() { }

    public static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }

    public static int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Escriba un número entero.");
            }
        }
    }

    public static double leerDecimal(String mensaje) {
        while (true) {
            try {
                return Double.parseDouble(leerTexto(mensaje).replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Escriba un número (ej. 2500000 o 2500000.50).");
            }
        }
    }

    @FunctionalInterface
    public interface Validador<T> {
        void validar(T valor) throws DatoInvalidoException;
    }

    public static String leerTexto(String mensaje, Validador<String> validador) {
        while (true) {
            String valor = leerTexto(mensaje);
            try {
                validador.validar(valor);
                return valor;
            } catch (DatoInvalidoException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static double leerDecimal(String mensaje, Validador<Double> validador) {
        while (true) {
            double valor = leerDecimal(mensaje);
            try {
                validador.validar(valor);
                return valor;
            } catch (DatoInvalidoException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static int leerEntero(String mensaje, Validador<Integer> validador) {
        while (true) {
            int valor = leerEntero(mensaje);
            try {
                validador.validar(valor);
                return valor;
            } catch (DatoInvalidoException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}