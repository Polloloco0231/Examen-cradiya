package Controlador;

import Modelo.Clases.EstadoPrestamo;
import Modelo.Clases.Prestamo;
import Modelo.Excepciones.DatoInvalidoException;
import Modelo.Excepciones.PersistenciaException;
import Modelo.Excepciones.RegistroNoEncontradoExcepcion;
import Modelo.Persistencia.ClienteDAO;
import Modelo.Persistencia.EmpleadoDAO;
import Modelo.Persistencia.PrestamoDAO;
import Modelo.Persistencia.SincronizadorArchivos;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PrestamoControlador {

    private final PrestamoDAO prestamoDAO;
    private final ClienteDAO clienteDAO;
    private final EmpleadoDAO empleadoDAO;

    public PrestamoControlador() {
        this.prestamoDAO = new PrestamoDAO();
        this.clienteDAO = new ClienteDAO();
        this.empleadoDAO = new EmpleadoDAO();
    }

    public Prestamo crear(int clienteId, int empleadoId, double monto,
                          double interes, int cuotas)
            throws DatoInvalidoException, RegistroNoEncontradoExcepcion, PersistenciaException {

        validarMonto(monto);
        validarInteres(interes);
        validarCuotas(cuotas);
        validarCliente(clienteId);
        validarEmpleado(empleadoId);

        try {
            Prestamo prestamo = new Prestamo(clienteId, empleadoId, monto, interes,
                    cuotas, LocalDate.now());
            prestamoDAO.guardar(prestamo);
            archivos.sincronizarPrestamos();
            return prestamo;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo registrar el préstamo.", e);
        } catch (IOException e) {
            throw new PersistenciaException("El préstamo se guardó en la base de datos, "
                    + "pero no se pudo actualizar prestamos.txt.", e);
        }
    }

    public List<Prestamo> listar() throws PersistenciaException {
        try {
            return prestamoDAO.listar();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar los préstamos.", e);
        }
    }

    public Prestamo buscarPorId(int id)
            throws RegistroNoEncontradoExcepcion, PersistenciaException {
        try {
            Prestamo prestamo = prestamoDAO.buscarPorId(id);
            if (prestamo == null) {
                throw new RegistroNoEncontradoExcepcion("No existe un préstamo con id " + id + ".");
            }
            return prestamo;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar el préstamo.", e);
        }
    }

    public void cambiarEstado(int id, EstadoPrestamo nuevoEstado)
            throws DatoInvalidoException, RegistroNoEncontradoExcepcion, PersistenciaException {

        Prestamo prestamo = buscarPorId(id);

        if (nuevoEstado == EstadoPrestamo.PENDIENTE && prestamo.getSaldoPendiente() <= 0) {
            throw new DatoInvalidoException(
                    "No se puede dejar pendiente un préstamo sin saldo por pagar.");
        }
        if (nuevoEstado == EstadoPrestamo.PAGADO) {
            prestamo.setSaldoPendiente(0);
        }
        prestamo.setEstado(nuevoEstado);

        try {
            prestamoDAO.actualizar(prestamo);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo actualizar el estado del préstamo.", e);
        }
    }

    public void validarMonto(double monto) throws DatoInvalidoException {
        if (monto <= 0) {
            throw new DatoInvalidoException("El monto debe ser mayor que cero.");
        }
    }

    public void validarInteres(double interes) throws DatoInvalidoException {
        if (interes < 0 || interes > 100) {
            throw new DatoInvalidoException("El interés debe estar entre 0 y 100.");
        }
    }

    public void validarCuotas(int cuotas) throws DatoInvalidoException {
        if (cuotas <= 0) {
            throw new DatoInvalidoException("El número de cuotas debe ser mayor que cero.");
        }
    }

    public void validarCliente(int clienteId)
            throws RegistroNoEncontradoExcepcion , PersistenciaException {
        try {
            if (clienteDAO.buscarPorId(clienteId) == null) {
                throw new RegistroNoEncontradoExcepcion ("No existe un cliente con id " + clienteId + ".");
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo verificar el cliente.", e);
        }
    }

    public void validarEmpleado(int empleadoId)
            throws RegistroNoEncontradoExcepcion , PersistenciaException {
        try {
            if (empleadoDAO.buscarPorId(empleadoId) == null) {
                throw new RegistroNoEncontradoExcepcion ("No existe un empleado con id " + empleadoId + ".");
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo verificar el empleado.", e);
        }
    }

    private final SincronizadorArchivos archivos = new SincronizadorArchivos();
}
