package Modelo.Persistencia;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ArchivoRepositorio<T> {

    private final Path ruta;
    private final Function<T, String> aLinea;
    private final Function<String, T> deLinea;

    public ArchivoRepositorio(String nombreArchivo,
                              Function<T, String> aLinea,
                              Function<String, T> deLinea) {
        this.ruta = Paths.get(nombreArchivo);
        this.aLinea = aLinea;
        this.deLinea = deLinea;
    }

    public void guardar(T objeto) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            bw.write(aLinea.apply(objeto));
            bw.newLine();
        }
    }

    public List<T> listar() throws IOException {
        List<T> lista = new ArrayList<>();
        if (!Files.exists(ruta)) return lista;
        for (String linea : Files.readAllLines(ruta, StandardCharsets.UTF_8)) {
            if (!linea.isBlank()) lista.add(deLinea.apply(linea));
        }
        return lista;
    }

    public void sobrescribir(List<T> objetos) throws IOException {
        List<String> lineas = new ArrayList<>();
        for (T o : objetos) lineas.add(aLinea.apply(o));
        Files.write(ruta, lineas, StandardCharsets.UTF_8);
    }
}