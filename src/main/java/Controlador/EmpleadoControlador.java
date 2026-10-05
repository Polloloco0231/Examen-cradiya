package Controlador;

import Modelo.Clases.Empleado;
import Modelo.Excepciones.DatoInvalidoException;
import Modelo.Excepciones.PagoInvalidoException;
import Modelo.Excepciones.PersistenciaException;
import Modelo.Persistencia.EmpleadoDAO;
import Modelo.Persistencia.SincronizadorArchivos;

import java.io.IOException;
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
            validarNombre(nombre);
        }
        if (documento == null || documento.isBlank()) {
            validarDocumento(documento);
        }
        if (correo != null && !correo.isBlank() && !correo.contains("@")) {
            validarCorreo(correo);
        }
        if (rol == null || rol.isBlank()) {
            validarRol(rol);
        }
        if (salario <= 0) {
            validarSalario(salario);
        }

        String correoFinal = (correo == null || correo.isBlank()) ? null : correo.trim();

        Empleado empleado = new Empleado(nombre.trim(), documento.trim(), correoFinal, rol.trim(), salario);

        try {
            empleadoDAO.guardar(empleado);
            archivos.sincronizarEmpleados();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo registrar el empleado. " + "Verifique que el documento no esté repetido.", e);
        } catch (IOException e) {
            throw new PersistenciaException("El empleado se guardó en la base de datos, " + "pero no se pudo actualizar empleados.txt.", e);
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

    public void validarNombre(String nombre) throws DatoInvalidoException {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre es obligatorio.");
        }
    }

    public void validarDocumento(String documento) throws DatoInvalidoException {
        if (documento == null || documento.isBlank()) {
            throw new DatoInvalidoException("El documento es obligatorio.");
        }
    }

    public void validarCorreo(String correo) throws DatoInvalidoException {
        if (correo != null && !correo.isBlank() && !correo.contains("@")) {
            throw new DatoInvalidoException("El correo no es válido.");
        }
    }

    public void validarRol(String rol) throws DatoInvalidoException {
        if (rol == null || rol.isBlank()) {
            throw new DatoInvalidoException("El rol es obligatorio.");
        }
    }

    public void validarSalario(double salario) throws DatoInvalidoException {
        if (salario <= 0) {
            throw new DatoInvalidoException("El salario debe ser mayor que cero.");
        }
    }

    private final SincronizadorArchivos archivos = new SincronizadorArchivos();
}
