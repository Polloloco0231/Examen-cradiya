package Controlador;

import Modelo.Excepciones.PersistenciaException;
import Modelo.Persistencia.SincronizadorArchivos;

import java.io.IOException;
import java.sql.SQLException;

public class ArchivoControlador {

    private final SincronizadorArchivos archivos = new SincronizadorArchivos();

    public void sincronizarTodo() throws PersistenciaException {
        try {
            archivos.sincronizarTodo();
        } catch (SQLException | IOException e) {
            throw new PersistenciaException("No se pudieron actualizar los archivos de texto.", e);
        }
    }
}