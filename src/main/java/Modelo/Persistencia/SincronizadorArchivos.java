package Modelo.Persistencia;

import Modelo.Clases.Cliente;
import Modelo.Clases.Empleado;
import Modelo.Clases.Pago;
import Modelo.Clases.Prestamo;

import java.io.IOException;
import java.sql.SQLException;

public class SincronizadorArchivos {

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final PagoDAO pagoDAO = new PagoDAO();

    private final ArchivoRepositorio<Empleado> archivoEmpleados =
            new ArchivoRepositorio<>("datos/empleados.txt", Empleado::aLinea, Empleado::deLinea);
    private final ArchivoRepositorio<Cliente> archivoClientes =
            new ArchivoRepositorio<>("datos/clientes.txt", Cliente::aLinea, Cliente::deLinea);
    private final ArchivoRepositorio<Prestamo> archivoPrestamos =
            new ArchivoRepositorio<>("datos/prestamos.txt", Prestamo::aLinea, Prestamo::deLinea);
    private final ArchivoRepositorio<Pago> archivoPagos =
            new ArchivoRepositorio<>("datos/pagos.txt", Pago::aLinea, Pago::deLinea);

    public void sincronizarEmpleados() throws SQLException, IOException {
        archivoEmpleados.sobrescribir(empleadoDAO.listar());
    }

    public void sincronizarClientes() throws SQLException, IOException {
        archivoClientes.sobrescribir(clienteDAO.listar());
    }

    public void sincronizarPrestamos() throws SQLException, IOException {
        archivoPrestamos.sobrescribir(prestamoDAO.listar());
    }

    public void sincronizarPagos() throws SQLException, IOException {
        archivoPagos.sobrescribir(pagoDAO.listar());
    }

    public void sincronizarTodo() throws SQLException, IOException {
        sincronizarEmpleados();
        sincronizarClientes();
        sincronizarPrestamos();
        sincronizarPagos();
    }
}
