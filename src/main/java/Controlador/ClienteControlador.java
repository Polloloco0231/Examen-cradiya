package Controlador;

import Modelo.Clases.Cliente;
import Modelo.Clases.Prestamo;
import Modelo.Excepciones.DatoInvalidoException;
import Modelo.Excepciones.PersistenciaException;
import Modelo.Excepciones.RegistroNoEncontradoExcepcion;
import Modelo.Persistencia.ClienteDAO;
import Modelo.Persistencia.PrestamoDAO;
import Modelo.Persistencia.SincronizadorArchivos;

import java.io.IOException;
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
        if (telefono != null && !telefono.isBlank() && !telefono.matches("\\d{7,15}")) {
            validarTelefono(telefono);
        }

        String correoFinal = (correo == null || correo.isBlank()) ? null : correo.trim();
        String telefonoFinal = (telefono == null || telefono.isBlank()) ? null : telefono.trim();

        Cliente cliente = new Cliente(nombre.trim(), documento.trim(), correoFinal, telefonoFinal);

        try {
            clienteDAO.guardar(cliente);
            archivos.sincronizarClientes();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo registrar el cliente. " + "Verifique que el documento no esté repetido.", e);
        } catch (IOException e) {
            throw new PersistenciaException("El cliente se guardó en la base de datos, " + "pero no se pudo actualizar clientes.txt.", e);
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

    public void validarTelefono(String telefono) throws DatoInvalidoException {
        if (telefono != null && !telefono.isBlank() && !telefono.matches("\\d{7,15}")) {
            throw new DatoInvalidoException("El teléfono debe tener entre 7 y 15 dígitos.");
        }
    }

    public List<Cliente> listar() throws PersistenciaException {
        try {
            return clienteDAO.listar();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar los clientes.", e);
        }
    }

    private final SincronizadorArchivos archivos = new SincronizadorArchivos();
}
