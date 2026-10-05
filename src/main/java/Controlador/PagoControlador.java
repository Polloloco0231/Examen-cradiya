package Controlador;

import Modelo.Clases.EstadoPrestamo;
import Modelo.Clases.Pago;
import Modelo.Clases.Prestamo;
import Modelo.Excepciones.PagoInvalidoException;
import Modelo.Excepciones.PersistenciaException;
import Modelo.Excepciones.RegistroNoEncontradoExcepcion;
import Modelo.Persistencia.PagoDAO;
import Modelo.Persistencia.PrestamoDAO;
import Modelo.Persistencia.SincronizadorArchivos;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PagoControlador {

    private final PagoDAO pagoDAO;
    private final PrestamoDAO prestamoDAO;
    private final SincronizadorArchivos archivos;

    public PagoControlador() {
        this.pagoDAO = new PagoDAO();
        this.prestamoDAO = new PrestamoDAO();
        this.archivos = new SincronizadorArchivos();
    }

    public Prestamo buscarPrestamoParaPago(int prestamoId)
            throws RegistroNoEncontradoExcepcion, PagoInvalidoException, PersistenciaException {
        try {
            Prestamo prestamo = prestamoDAO.buscarPorId(prestamoId);
            if (prestamo == null) {
                throw new RegistroNoEncontradoExcepcion("No existe un préstamo con id " + prestamoId + ".");
            }
            if (prestamo.getEstado() == EstadoPrestamo.PAGADO) {
                throw new PagoInvalidoException("El préstamo ya está pagado.");
            }
            return prestamo;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar el préstamo.", e);
        }
    }

    public void validarAbono(Prestamo prestamo, double monto) throws PagoInvalidoException {
        if (monto <= 0) {
            throw new PagoInvalidoException("El abono debe ser mayor que cero.");
        }
        if (monto > prestamo.getSaldoPendiente()) {
            throw new PagoInvalidoException("El abono ($" + monto
                    + ") supera el saldo pendiente ($" + prestamo.getSaldoPendiente() + ").");
        }
    }

    public Pago registrarPago(int prestamoId, double monto)
            throws PagoInvalidoException, RegistroNoEncontradoExcepcion, PersistenciaException {

        Prestamo prestamo = buscarPrestamoParaPago(prestamoId);
        validarAbono(prestamo, monto);

        try {
            Pago pago = new Pago(prestamoId, LocalDate.now(), monto);
            pagoDAO.guardar(pago);
            prestamo.registrarAbono(monto);
            prestamoDAO.actualizar(prestamo);

            archivos.sincronizarPagos();
            archivos.sincronizarPrestamos();
            return pago;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo registrar el pago.", e);
        } catch (IOException e) {
            throw new PersistenciaException("El pago se guardó en la base de datos, "
                    + "pero no se pudieron actualizar los archivos de texto.", e);
        }
    }

    public List<Pago> historial(int prestamoId)
            throws RegistroNoEncontradoExcepcion, PersistenciaException {
        try {
            if (prestamoDAO.buscarPorId(prestamoId) == null) {
                throw new RegistroNoEncontradoExcepcion("No existe un préstamo con id " + prestamoId + ".");
            }
            return pagoDAO.listarPorPrestamo(prestamoId);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar el histórico de pagos.", e);
        }
    }

    public double consultarSaldo(int prestamoId)
            throws RegistroNoEncontradoExcepcion, PersistenciaException {
        try {
            Prestamo prestamo = prestamoDAO.buscarPorId(prestamoId);
            if (prestamo == null) {
                throw new RegistroNoEncontradoExcepcion("No existe un préstamo con id " + prestamoId + ".");
            }
            return prestamo.getSaldoPendiente();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar el saldo.", e);
        }
    }
}
