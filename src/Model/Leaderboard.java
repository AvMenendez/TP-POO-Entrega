package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Tabla de mejores partidas (top 5), persistida en un archivo de texto.
public class Leaderboard {

    public static final int MAXIMO = 5;
    private static final String ARCHIVO_POR_DEFECTO = "leaderboard.csv";

    // Una partida registrada: nombre del jugador y puntaje obtenido.
    public static class Entrada {
        private final String nombre;
        private final int    puntaje;

        public Entrada(String nombre, int puntaje) {
            this.nombre  = nombre;
            this.puntaje = puntaje;
        }

        public String getNombre()  { return nombre; }
        public int    getPuntaje() { return puntaje; }
    }

    private final File archivo;

    public Leaderboard() {
        this(new File(ARCHIVO_POR_DEFECTO));
    }

    public Leaderboard(File archivo) {
        this.archivo = archivo;
    }

    // Devuelve las mejores partidas ordenadas de mayor a menor puntaje (máx. 5).
    public List<Entrada> obtenerTop() {
        List<Entrada> entradas = cargar();
        entradas.sort(Comparator.comparingInt(Entrada::getPuntaje).reversed());
        if (entradas.size() > MAXIMO) {
            return new ArrayList<>(entradas.subList(0, MAXIMO));
        }
        return entradas;
    }

    // Registra una partida y conserva solo las MAXIMO mejores.
    public void registrar(String nombre, int puntaje) {
        List<Entrada> entradas = cargar();
        entradas.add(new Entrada(nombre, puntaje));
        entradas.sort(Comparator.comparingInt(Entrada::getPuntaje).reversed());
        if (entradas.size() > MAXIMO) {
            entradas = new ArrayList<>(entradas.subList(0, MAXIMO));
        }
        guardar(entradas);
    }

    private List<Entrada> cargar() {
        List<Entrada> entradas = new ArrayList<>();
        if (!archivo.exists()) {
            return entradas;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) {
                    continue;
                }
                int corte = linea.lastIndexOf(',');
                if (corte < 0) {
                    continue;
                }
                try {
                    String nombre  = linea.substring(0, corte).trim();
                    int    puntaje = Integer.parseInt(linea.substring(corte + 1).trim());
                    entradas.add(new Entrada(nombre, puntaje));
                } catch (NumberFormatException ignorada) {
                    // línea corrupta: se ignora
                }
            }
        } catch (IOException e) {
            // si no se puede leer, simplemente no hay tabla
        }
        return entradas;
    }

    private void guardar(List<Entrada> entradas) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            for (Entrada e : entradas) {
                pw.println(e.getNombre() + "," + e.getPuntaje());
            }
        } catch (IOException e) {
            // si no se puede guardar, la partida no queda registrada
        }
    }
}
