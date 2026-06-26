package Views;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

// Panel base de las pantallas de menú. Concentra el armado común (tamaño, layout vertical,
// margen) y dibuja el fondo general (Sprites.FONDO) estirado a toda la ventana, con un velo
// oscuro encima para que el texto siga legible. Si la imagen no está, queda el color de
// respaldo. Todas las pantallas lo heredan, así no repiten este boilerplate (DRY) y se ocupan
// solo de su propio contenido (SRP).
public class PanelFondo extends JPanel {

    // Velo oscuro semitransparente sobre la imagen (0 = sin velo, 255 = negro total).
    private static final int VELO_ALPHA = 120;

    private final BufferedImage imagenFondo;

    // Usa el fondo general (Sprites.FONDO).
    public PanelFondo() {
        this(Sprites.FONDO);
    }

    // Usa una imagen de fondo específica (cae al fondo general si la propia es null).
    public PanelFondo(BufferedImage imagenFondo) {
        this.imagenFondo = imagenFondo != null ? imagenFondo : Sprites.FONDO;
        setPreferredSize(new Dimension(PanelJuego.ANCHO, PanelJuego.ALTO));
        setBackground(Estilo.FONDO);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo == null) {
            return; // sin imagen: queda el color de respaldo que pintó super
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), null);
        g2.setColor(new Color(0, 0, 0, VELO_ALPHA));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
