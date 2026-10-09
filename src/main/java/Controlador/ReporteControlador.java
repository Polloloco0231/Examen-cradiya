package Controlador;

import Modelo.Clases.Cliente;
import Modelo.Clases.EstadoPrestamo;
import Modelo.Clases.Pago;
import Modelo.Clases.Prestamo;
import Modelo.Excepciones.PersistenciaException;
import Modelo.Persistencia.ClienteDAO;
import Modelo.Persistencia.PagoDAO;
import Modelo.Persistencia.PrestamoDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ReporteControlador {

    private final PrestamoDAO prestamoDAO;
    private final ClienteDAO clienteDAO;
    private final PagoDAO pagoDAO;

    public ReporteControlador() {
        this.prestamoDAO = new PrestamoDAO();
        this.clienteDAO = new ClienteDAO();
        this.pagoDAO = new PagoDAO();
    }

    public List<Prestamo> prestamosActivos() throws PersistenciaException {
        try {
            return prestamoDAO.listar().stream()
                    .filter(p -> p.getEstado() == EstadoPrestamo.ACTIVO)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar los préstamos activos.", e);
        }
    }

    public List<Prestamo> prestamosVencidos() throws PersistenciaException {
        try {
            return prestamoDAO.listar().stream()
                    .filter(Prestamo::estaVencido)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar los préstamos vencidos.", e);
        }
    }

    public List<Cliente> clientesMorosos() throws PersistenciaException {
        try {
            Set<Integer> idsMorosos = prestamoDAO.listar().stream()
                    .filter(Prestamo::estaVencido)
                    .map(Prestamo::getClienteId)
                    .collect(Collectors.toSet());

            return clienteDAO.listar().stream()
                    .filter(c -> idsMorosos.contains(c.getId()))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar los clientes morosos.", e);
        }
    }

    public double totalPrestado() throws PersistenciaException {
        try {
            return prestamoDAO.listar().stream()
                    .mapToDouble(Prestamo::getMonto)
                    .sum();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo calcular el total prestado.", e);
        }
    }

    public double totalRecaudado() throws PersistenciaException {
        try {
            return pagoDAO.listar().stream()
                    .mapToDouble(Pago::getMonto)
                    .sum();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo calcular el total recaudado.", e);
        }
    }

    public Map<Integer, Long> prestamosPorEmpleado() throws PersistenciaException {
        try {
            return prestamoDAO.listar().stream()
                    .collect(Collectors.groupingBy(Prestamo::getEmpleadoId,
                            Collectors.counting()));
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo calcular los préstamos por empleado.", e);
        }
    }

    public Optional<Cliente> clienteMayorDeuda() throws PersistenciaException {
        try {
            Map<Integer, Double> deudaPorCliente = prestamoDAO.listar().stream()
                    .filter(p -> p.getEstado() == EstadoPrestamo.ACTIVO)
                    .collect(Collectors.groupingBy(Prestamo::getClienteId,
                            Collectors.summingDouble(Prestamo::getSaldoPendiente)));

            Optional<Integer> idMayor = deudaPorCliente.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey);

            if (idMayor.isEmpty()) {
                return Optional.empty();
            }
            return Optional.ofNullable(clienteDAO.buscarPorId(idMayor.get()));
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo calcular el cliente con mayor deuda.", e);
        }
    }
}