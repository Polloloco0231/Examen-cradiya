package Modelo.Excepciones;

public class RegistroNoEncontradoExcepcion extends RuntimeException {
    public RegistroNoEncontradoExcepcion(String message) {
        super(message);
    }
}
