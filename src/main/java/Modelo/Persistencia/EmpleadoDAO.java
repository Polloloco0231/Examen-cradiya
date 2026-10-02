package Modelo.Persistencia;

import Modelo.Clases.Empleado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO implements Repositorio<Empleado> {

    @Override
    public void guardar(Empleado e) throws SQLException {
        String sql = "INSERT INTO empleados (nombre, documento, rol, correo, salario) VALUES (?,?,?,?,?)";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getDocumento());
            ps.setString(3, e.getRol());
            ps.setString(4, e.getCorreo());
            ps.setDouble(5, e.getSalario());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) e.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public List<Empleado> listar() throws SQLException {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM empleados";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    @Override
    public Empleado buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM empleados WHERE id = ?";
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    private Empleado mapear(ResultSet rs) throws SQLException {
        return new Empleado(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("documento"),
                rs.getString("correo"),
                rs.getString("rol"),
                rs.getDouble("salario"));
    }
}
