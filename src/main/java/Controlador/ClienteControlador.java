package Controlador;

import Modelo.Clases.Cliente;
import Modelo.Clases.Prestamo;
import Modelo.Excepciones.DatoInvalidoException;
import Modelo.Excepciones.PersistenciaException;
import Modelo.Excepciones.RegistroNoEncontradoExcepcion;
import Modelo.Persistencia.ClienteDAO;
import Modelo.Persistencia.PrestamoDAO;

import java.sql.SQLException;
import java.util.List;

public class ClienteControlador {

    private ClienteDAO clienteDAO = null;
    private final PrestamoDAO prestamoDAO;

    public ClienteControlador() {
        this.clienteDAO = new ClienteDAO();
        this.prestamoDAO = new PrestamoDAO();
    }

    public Cliente registrar(String nombre, String documento, String correo, String telefono)
        throws RegistroNoEncontradoExcepcion, PersistenciaException, DatoInvalidoException {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre es obligatorio.");
        }
        if (documento == null || documento.isBlank()) {
            throw new DatoInvalidoException("El documento es obligatorio.");
        }
        if (correo != null && !correo.isBlank() && !correo.contains("@")) {
            throw new DatoInvalidoException("El correo no es valido.");
        }
        if (telefono != null && !telefono.isBlank() && !telefono.matches("\\d{7,15}")) {
            throw new DatoInvalidoException("El telefono debe tener entre 7 y 15 dígitos.");
        }

        Cliente cliente = new Cliente(nombre.trim(), documento.trim(), correo, telefono);

        try {
            clienteDAO.guardar(cliente);
        } catch (SQLException e){
            throw new PersistenciaException("No se pudo registrar el cliente" + " Varifique que el documento no esté repetido", e);
        }
        return cliente;

    }

    public Cliente buscarPorId(int id)
        throws RegistroNoEncontradoExcepcion, PersistenciaException{
        try {
            Cliente cliente = clienteDAO.buscarPorId(id);
            if (cliente == null){
                throw new RegistroNoEncontradoExcepcion("No existe un cliente con id " + id + ".");
            }
            return cliente;
        } catch (SQLException e){
            throw new PersistenciaException("No se pudo consultar el cliente", e);
        }
    }

    public List<Prestamo> consultarPrestamos(int clienteId)
        throws RegistroNoEncontradoExcepcion, PersistenciaException{
        buscarPorId(clienteId);
        try {
            return prestamoDAO.listarPorCliente(clienteId);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar los préstamos del cliente", e);
        }
    }
}
