package Model;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.io.File;
import java.net.URL;

// Reproduce los sonidos del juego: efectos puntuales (reproducir) y música de fondo en loop.
// La música y los efectos se pueden silenciar por separado (ver PanelOpciones).
public class GestorSonido {

    private static Clip musicaFondo;
    private static String pistaActual;        // música de fondo deseada (null = ninguna)
    private static boolean musicaSilenciada;
    private static boolean efectosSilenciados;

    // Carpetas donde viven los .wav, según desde dónde se ejecute (igual que Sprites).
    private static final String[] CARPETAS = {"Res/Sounds/", "TP-POO-main/Res/Sounds/"};

    // Abre el .wav buscándolo en disco (Res/Sounds/...) y, si no, en el classpath.
    // 'ruta' acepta tanto "/Sounds/x.wav" como "x.wav": nos quedamos con el nombre del archivo.
    private static AudioInputStream abrir(String ruta) throws Exception {
        String nombre = ruta.substring(ruta.lastIndexOf('/') + 1);
        for (String carpeta : CARPETAS) {
            File f = new File(carpeta + nombre);
            if (f.exists()) {
                return AudioSystem.getAudioInputStream(f);
            }
        }
        // Respaldo: por si en algún momento se empaquetan dentro del jar/bin.
        URL url = GestorSonido.class.getResource("/Sounds/" + nombre);
        return url != null ? AudioSystem.getAudioInputStream(url) : null;
    }

    // Reproduce un efecto puntual una sola vez (nada si los efectos están silenciados).
    public static void reproducir(String rutaRelativa) {
        if (efectosSilenciados) {
            return;
        }
        try {
            AudioInputStream audioIn = abrir(rutaRelativa);
            if (audioIn == null) {
                System.err.println("Sonido no encontrado: " + rutaRelativa);
                return;
            }
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            // Cerrar (y liberar la línea) recién cuando termina de sonar. El listener
            // mantiene viva la referencia al clip mientras suena, así el GC no lo corta.
            clip.addLineListener(evento -> {
                if (evento.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            clip.start();
        } catch (Exception e) {
            System.err.println("Error al reproducir " + rutaRelativa + ": " + e.getMessage());
        }
    }

    // Fija la música de fondo deseada y la arranca en bucle (nada si está silenciada).
    // Si esa misma pista ya está sonando, no la reinicia.
    public static void reproducirMusicaEnLoop(String rutaRelativa) {
        if (rutaRelativa.equals(pistaActual) && musicaFondo != null && musicaFondo.isRunning()) {
            return;
        }
        pistaActual = rutaRelativa;
        if (musicaSilenciada) {
            cerrarClip();            // muteada: recordamos la pista pero no suena
        } else {
            arrancarMusica(rutaRelativa);
        }
    }

    // Silencia o reactiva la música. Al reactivarla, retoma la pista de fondo deseada.
    public static void setMusicaSilenciada(boolean silenciada) {
        musicaSilenciada = silenciada;
        if (silenciada) {
            cerrarClip();
        } else if (pistaActual != null) {
            arrancarMusica(pistaActual);
        }
    }

    public static boolean musicaSilenciada() {
        return musicaSilenciada;
    }

    // Silencia o reactiva los efectos puntuales.
    public static void setEfectosSilenciados(boolean silenciados) {
        efectosSilenciados = silenciados;
    }

    public static boolean efectosSilenciados() {
        return efectosSilenciados;
    }

    // Abre y arranca la música en bucle desde el principio.
    private static void arrancarMusica(String ruta) {
        try {
            cerrarClip();
            AudioInputStream audioIn = abrir(ruta);
            if (audioIn == null) {
                System.err.println("Música no encontrada: " + ruta);
                return;
            }
            musicaFondo = AudioSystem.getClip();
            musicaFondo.open(audioIn);
            musicaFondo.loop(Clip.LOOP_CONTINUOUSLY); // Arranca y repite automáticamente
        } catch (Exception e) {
            System.err.println("Error al reproducir música " + ruta + ": " + e.getMessage());
        }
    }

    // Frena y libera la línea de audio de la música (sin olvidar la pista deseada).
    private static void cerrarClip() {
        if (musicaFondo != null) {
            if (musicaFondo.isRunning()) {
                musicaFondo.stop();
            }
            musicaFondo.close(); // libera la línea de audio
            musicaFondo = null;
        }
    }
}
