package Controlador;

import Modelo.Clases.Empleado;
import Modelo.Excepciones.DatoInvalidoException;
import Modelo.Excepciones.PagoInvalidoException;
import Modelo.Excepciones.PersistenciaException;
import Modelo.Persistencia.EmpleadoDAO;

import java.sql.SQLException;
import java.util.List;

public class EmpleadoControlador {

    private final EmpleadoDAO empleadoDAO;
    public EmpleadoControlador() {
        this.empleadoDAO = new EmpleadoDAO();
    }

    public Empleado registrar(String nombre, String documento, String correo, String rol, double salario)
            throws DatoInvalidoException, PersistenciaException {

        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre es obligatorio.");
        }
        if (documento == null || documento.isBlank()) {
            throw new DatoInvalidoException("El documento es obligatorio.");
        }
        if (correo != null && !correo.isBlank() && !correo.contains("@")) {
            throw new DatoInvalidoException("El correo no es valido.");
        }
        if (rol == null || rol.isBlank()) {
            throw new DatoInvalidoException("El rol es obligatorio.");
        }
        if (salario <= 0) {
            throw new DatoInvalidoException("El salario debe ser mayor a cero.");
        }

        Empleado empleado = new Empleado(nombre.trim(), documento.trim(), correo, rol.trim(), salario);

        try {
            empleadoDAO.guardar(empleado);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo registrar el empleado" + " Verifique que el documento no esté repetido", e);
        }
        return empleado;
    }

    public List<Empleado> listar() throws PersistenciaException{
        try {
            return empleadoDAO.listar();
        } catch (SQLException e){
            throw new PersistenciaException("No se pudo consultar los empleados", e);
        }
    }

}
