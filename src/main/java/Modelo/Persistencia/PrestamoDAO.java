package Modelo.Persistencia;

import Modelo.Clases.EstadoPrestamo;
import Modelo.Clases.Prestamo;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO implements Repositorio<Prestamo> {

    @Override
    public void guardar(Prestamo p) throws SQLException {
        String sql = "INSERT INTO prestamos (cliente_id, empleado_id, monto, interes, cuotas, " +
                "monto_total, cuota_mensual, saldo_pendiente, fecha_inicio, fecha_vencimiento, estado) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getClienteId());
            ps.setInt(2, p.getEmpleadoId());
            ps.setDouble(3, p.getMonto());
            ps.setDouble(4, p.getInteres());
            ps.setInt(5, p.getCuotas());
            ps.setDouble(6, p.getMontoTotal());
            ps.setDouble(7, p.getCuotaMensual());
            ps.setDouble(8, p.getSaldoPendiente());
            ps.setDate(9, Date.valueOf(p.getFechaInicio()));
            ps.setDate(10, Date.valueOf(p.getFechaVencimiento()));
            ps.setString(11, p.getEstado().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getInt(1));
            }
        }
    }

    public static void actualizar(Prestamo p) throws SQLException {
        String sql = "UPDATE prestamos SET saldo_pendiente = ?, estado = ? WHERE id = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, p.getSaldoPendiente());
            ps.setString(2, p.getEstado().name());
            ps.setInt(3, p.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Prestamo> listar() throws SQLException {
        List<Prestamo> lista = new ArrayList<>();
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM prestamos");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    @Override
    public Prestamo buscarPorId(int id) throws SQLException {
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM prestamos WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public List<Prestamo> listarPorCliente(int clienteId) throws SQLException {
        List<Prestamo> lista = new ArrayList<>();
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM prestamos WHERE cliente_id = ?")) {
            ps.setInt(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Prestamo mapear(ResultSet rs) throws SQLException {
        Prestamo p = new Prestamo();
        p.setId(rs.getInt("id"));
        p.setClienteId(rs.getInt("cliente_id"));
        p.setEmpleadoId(rs.getInt("empleado_id"));
        p.setMonto(rs.getDouble("monto"));
        p.setInteres(rs.getDouble("interes"));
        p.setCuotas(rs.getInt("cuotas"));
        p.setMontoTotal(rs.getDouble("monto_total"));
        p.setCuotaMensual(rs.getDouble("cuota_mensual"));
        p.setSaldoPendiente(rs.getDouble("saldo_pendiente"));
        p.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
        p.setFechaVencimiento(rs.getDate("fecha_vencimiento").toLocalDate());
        p.setEstado(EstadoPrestamo.valueOf(rs.getString("estado")));
        return p;
    }
}
