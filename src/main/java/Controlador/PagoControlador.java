package Controlador;

import Modelo.Clases.EstadoPrestamo;
import Modelo.Clases.Pago;
import Modelo.Clases.Prestamo;
import Modelo.Excepciones.PagoInvalidoException;
import Modelo.Excepciones.PersistenciaException;
import Modelo.Excepciones.RegistroNoEncontradoExcepcion;
import Modelo.Persistencia.PagoDAO;
import Modelo.Persistencia.PrestamoDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PagoControlador {

    private final PagoDAO pagoDAO;
    private final PrestamoDAO prestamoDAO;

    public PagoControlador() {
        this.pagoDAO = new PagoDAO();
        this.prestamoDAO = new PrestamoDAO();
    }

    public Pago registrarPago(int prestamoId, double monto)
        throws PagoInvalidoException, RegistroNoEncontradoExcepcion, PersistenciaException{

        try {
            Prestamo prestamo = prestamoDAO.buscarPorId(prestamoId);
            if (prestamo == null) {
                throw new RegistroNoEncontradoExcepcion("No existe un préstamo con id " + prestamoId + ".");
            }
            if (prestamo.getEstado() == EstadoPrestamo.PAGADO) {
                throw new PagoInvalidoException("El préstamo ya estaba pagado.");
            }
            if (monto <= 0 ) {
                throw new PagoInvalidoException("El abono debe ser mayor que cero.");
            }
            if (monto > prestamo.getSaldoPendiente()) {
                throw new PagoInvalidoException("El abono ($" + monto + ") supera el saldo pendiente ($" + prestamo.getSaldoPendiente() + ").");
            }

            Pago pago = new Pago(prestamoId, LocalDate.now(), monto);
            pagoDAO.guardar(pago);
            prestamo.registrarAbono(monto);
            prestamoDAO.actualizar(prestamo);

            return pago;
        } catch (SQLException e) {
        throw new PersistenciaException("No se pudo registrar el pago.", e);
        }
    }

    public List<Pago> historial(int prestamoId)
        throws RegistroNoEncontradoExcepcion, PersistenciaException {
        try {
            if (prestamoDAO.buscarPorId(prestamoId) == null) {
                throw new RegistroNoEncontradoExcepcion("No existe un préstamo con id " + prestamoId + ".");
            }
            return pagoDAO.listarPorPrestamo(prestamoId);
        } catch (Exception e) {
            throw new PersistenciaException("No se pudo consultar el historial de pagos",e);
        }
    }

    public double consultarSaldo(int prestamoId)
        throws RegistroNoEncontradoExcepcion, PersistenciaException{
        try {
            Prestamo prestamo = prestamoDAO.buscarPorId(prestamoId);
            if (prestamo == null){
                throw new RegistroNoEncontradoExcepcion("No existe un préstamo con id " + prestamoId + ".");
            }
            return prestamo.getSaldoPendiente();
        } catch (Exception e) {
            throw new PersistenciaException("No se pudo consultar el saldo", e);
        }
    }


}
