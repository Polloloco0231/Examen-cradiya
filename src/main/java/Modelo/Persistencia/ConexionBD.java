package Modelo.Persistencia;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

    private static final String URL;
    private static final String USUARIO;
    private static final String CONTRASENA;

    static {
        Properties props = new Properties();
        try (InputStream in = ConexionBD.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                        "No se encontró config.properties. Copie config.properties " +
                                "a config.properties en src/main/resources y complete sus datos.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer config.properties", e);
        }
        URL = props.getProperty("db.url");
        USUARIO = props.getProperty("db.usuario");
        CONTRASENA = props.getProperty("db.contrasena");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}