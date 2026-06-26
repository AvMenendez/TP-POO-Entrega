package Views;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;

// Estilos visuales compartidos por las pantallas: paleta de colores y fábricas de
// componentes (botones, títulos, etiquetas). Concentra en un solo lugar el "look" de la
// interfaz para no repetir el mismo código en cada panel (DRY) y poder cambiarlo desde
// un único punto (Open/Closed). Es una Pure Fabrication: no es del dominio del juego.
public final class Estilo {

    private Estilo() { }

    private static final String FUENTE_BOTON = "Silkscreen"; // instalada en el sistema (Res/Fuentes/)
    private static final String FUENTE_TEXTO = "SansSerif";

    // Paleta (una sola fuente de verdad para los colores).
    public static final Color FONDO       = new Color(18, 22, 38);   // fondo de respaldo
    public static final Color ACENTO      = new Color(80, 200, 255);  // celeste (títulos, hover)
    public static final Color ORO         = new Color(255, 200, 80);  // leaderboard
    public static final Color VICTORIA    = new Color(90, 220, 120);  // ¡GANASTE!
    public static final Color DERROTA     = new Color(230, 90, 90);   // GAME OVER
    public static final Color TEXTO       = Color.WHITE;
    public static final Color TEXTO_TENUE = Color.LIGHT_GRAY;

    // Crea un botón transparente (sin fondo ni borde): solo el texto blanco en Silkscreen,
    // que se resalta en celeste al pasar el mouse.
    public static JButton botonTransparente(String texto, float tam, Runnable accion) {
        JButton b = new JButton(texto);
        b.setFont(new Font(FUENTE_BOTON, Font.BOLD, (int) tam));
        b.setForeground(TEXTO);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setFocusable(false);

        // Transparente: solo se ve el texto.
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Resalta el texto cuando el mouse está encima.
        ChangeListener resalte = e ->
            b.setForeground(b.getModel().isRollover() ? ACENTO : TEXTO);
        b.getModel().addChangeListener(resalte);

        b.addActionListener(e -> accion.run());
        return b;
    }

    // Crea un título centrado (SansSerif en negrita) con el tamaño y color dados.
    public static JLabel titulo(String texto, float tam, Color color) {
        return etiqueta(texto, Font.BOLD, tam, color);
    }

    // Crea una etiqueta de texto centrada (SansSerif normal) con tamaño y color dados.
    public static JLabel etiqueta(String texto, float tam, Color color) {
        return etiqueta(texto, Font.PLAIN, tam, color);
    }

    private static JLabel etiqueta(String texto, int estilo, float tam, Color color) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font(FUENTE_TEXTO, estilo, (int) tam));
        l.setForeground(color);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    // Crea un título centrado con la fuente Silkscreen. Acepta HTML en el texto, así se
    // pueden combinar colores por palabra (ver coloreado(...)).
    public static JLabel tituloSilkscreen(String htmlTexto, float tam) {
        JLabel l = new JLabel(htmlTexto);
        l.setFont(new Font(FUENTE_BOTON, Font.BOLD, (int) tam));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Con HTML el label se estira a lo ancho; centra el texto dentro del label.
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    // Envuelve un texto en HTML para pintarlo con un color (para usar dentro de un título).
    public static String coloreado(String texto, Color color) {
        return String.format("<font color='#%02X%02X%02X'>%s</font>",
                color.getRed(), color.getGreen(), color.getBlue(), texto);
    }
}
