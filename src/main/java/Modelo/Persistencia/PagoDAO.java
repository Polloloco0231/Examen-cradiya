package Modelo.Persistencia;

import Modelo.Clases.Pago;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO implements Repositorio<Pago> {

    @Override
    public void guardar(Pago p) throws SQLException {
        String sql = "INSERT INTO pagos (prestamo_id, fecha_pago, monto) VALUES (?,?,?)";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getPrestamoId());
            ps.setDate(2, Date.valueOf(p.getFechaPago()));
            ps.setDouble(3, p.getMonto());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public List<Pago> listar() throws SQLException {
        List<Pago> lista = new ArrayList<>();
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM pagos");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    @Override
    public Pago buscarPorId(int id) throws SQLException {
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM pagos WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // Para el histórico de pagos de un préstamo
    public List<Pago> listarPorPrestamo(int prestamoId) throws SQLException {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT * FROM pagos WHERE prestamo_id = ? ORDER BY fecha_pago";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, prestamoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Pago mapear(ResultSet rs) throws SQLException {
        return new Pago(
                rs.getInt("id"),
                rs.getInt("prestamo_id"),
                rs.getDate("fecha_pago").toLocalDate(),
                rs.getDouble("monto"));
    }
}
