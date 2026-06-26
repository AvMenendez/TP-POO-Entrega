package Views;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

// Carga (una sola vez) las imágenes de los objetos del juego desde Res/Sprites/.
// Si una imagen no está, queda null y la Vista usa su dibujo de respaldo.
public final class Sprites {

    private Sprites() { }

    // Extensiones de imagen que Java puede leer de fábrica (WebP NO está incluido).
    private static final String[] EXTENSIONES = {".png", ".gif", ".jpg", ".jpeg", ".bmp"};
    private static final String[] CARPETAS = {"Res/Sprites/", "TP-POO-main/Res/Sprites/"};

    public static final BufferedImage DRON      = cargar("dron");
    public static final BufferedImage MISIL     = cargar("misil");
    public static final BufferedImage EXPLOSION = cargar("explosion");
    public static final BufferedImage NAVE      = cargar("nave");
    public static final BufferedImage FONDO     = cargar("fondo");        // fondo general
    public static final BufferedImage FONDO_MENU = cargar("fondo_menu");  // fondo propio del menú

    // Busca y carga la imagen por su nombre; si no la encuentra, devuelve null.
    private static BufferedImage cargar(String base) {
        for (String carpeta : CARPETAS) {
            for (String ext : EXTENSIONES) {
                File f = new File(carpeta + base + ext);
                if (f.exists()) {
                    try {
                        BufferedImage img = ImageIO.read(f);
                        if (img != null) {
                            return img;
                        }
                    } catch (Exception e) {
                        System.err.println("No se pudo leer " + f.getPath() + ": " + e.getMessage());
                    }
                }
            }
        }
        // Classpath (por si se empaquetan dentro del jar/bin)
        try (InputStream in = Sprites.class.getResourceAsStream("/Sprites/" + base + ".png")) {
            if (in != null) {
                return ImageIO.read(in);
            }
        } catch (Exception e) {
            // sin imagen -> respaldo
        }
        return null;
    }
}
