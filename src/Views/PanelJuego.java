package Views;

import Model.Avion;
import Model.Config;
import Model.Dron;
import Model.Explosion;
import Model.Juego;
import Model.Misil;

import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

// Vista del juego: dibuja el estado del modelo convirtiendo metros -> píxeles. No contiene reglas.
public class PanelJuego extends JPanel {

    public static final int ANCHO = Config.ANCHO_PX;
    public static final int ALTO  = Config.ALTO_PX;

    // Tamaño de dibujo de cada objeto, en píxeles (solo visual; no afecta el daño).
    private static final int DRON_PX  = 96;       // dron (cuadrado)
    private static final int NAVE_PX  = 96;       // nave del jugador (cuadrado)
    private static final int MISIL_W  = 36;       // ancho del misil
    private static final int MISIL_H  = 66;       // alto del misil

    private final Juego juego;

    // Crea el panel del juego asociado al modelo que va a dibujar.
    public PanelJuego(Juego juego) {
        this.juego = juego;
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setBackground(Estilo.FONDO);
        setFocusable(true);
    }

    // Dibuja toda la escena: drones, misiles, explosiones, la nave y el HUD.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Mantiene el pixel-art nítido al escalar las imágenes.
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // Fondo: imagen estirada a toda la ventana. Si no está, queda el color liso del panel.
        // Se usa interpolación suave (bilineal) solo acá para que la foto no se vea pixelada;
        // los sprites siguen usando NEAREST_NEIGHBOR para mantener el pixel-art nítido.
        if (Sprites.FONDO != null) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(Sprites.FONDO, 0, 0, getWidth(), getHeight(), null);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        }

        // Drones enemigos
        for (Dron d : juego.getEscuadronActual().getDrones()) {
            int dx = Config.xAPixel(d.getPosicionX());
            int dy = Config.altitudAPixel(d.getPosicionY());
            if (Sprites.DRON != null) {
                dibujarSprite(g2, Sprites.DRON, dx, dy, DRON_PX, DRON_PX);
            } else {
                dibujarDron(g2, dx, dy);
            }
        }

        // Misiles cayendo
        for (Misil m : juego.getMisilesActivos()) {
            int mx = Config.xAPixel(m.getPosicionX());
            int my = Config.altitudAPixel(m.getPosicionY());
            if (Sprites.MISIL != null) {
                dibujarSprite(g2, Sprites.MISIL, mx, my, MISIL_W, MISIL_H);
            } else {
                dibujarMisil(g2, mx, my);
            }
        }

        // Explosiones (área de daño que crece y se desvanece)
        dibujarExplosiones(g2);

        // Nave del jugador
        Avion avion = juego.getJugadorActual().getAvionActivo();
        int ax = Config.xAPixel(avion.getPosicionX());
        int ay = Config.altitudAPixel(avion.getPosicionY());
        if (Sprites.NAVE != null) {
            dibujarSprite(g2, Sprites.NAVE, ax, ay, NAVE_PX, NAVE_PX);
        } else {
            dibujarAvion(g2, ax, ay);
        }

        dibujarHud(g2, avion);
    }

    // Dron tipo cuadricóptero: cuerpo central, cuatro brazos en X y hélices.
    private void dibujarDron(Graphics2D g2, int cx, int cy) {
        final int armX = 20, armY = 12;

        // Brazos en X
        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(60, 64, 72));
        g2.drawLine(cx, cy, cx - armX, cy - armY);
        g2.drawLine(cx, cy, cx + armX, cy - armY);
        g2.drawLine(cx, cy, cx - armX, cy + armY);
        g2.drawLine(cx, cy, cx + armX, cy + armY);
        g2.setStroke(new BasicStroke(1f));

        // Hélices (óvalos borrosos) en las puntas de los brazos
        dibujarRotor(g2, cx - armX, cy - armY);
        dibujarRotor(g2, cx + armX, cy - armY);
        dibujarRotor(g2, cx - armX, cy + armY);
        dibujarRotor(g2, cx + armX, cy + armY);

        // Cuerpo central
        g2.setColor(new Color(120, 126, 136));
        g2.fillRoundRect(cx - 11, cy - 9, 22, 18, 8, 8);
        g2.setColor(new Color(155, 161, 171)); // brillo superior
        g2.fillRoundRect(cx - 8, cy - 7, 16, 5, 4, 4);
        g2.setColor(new Color(40, 44, 50));    // cámara / gimbal inferior
        g2.fillOval(cx - 4, cy + 4, 8, 8);
    }

    private void dibujarRotor(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(22, 24, 28));
        g2.fillOval(x - 13, y - 3, 26, 6); // hélice horizontal
        g2.setColor(new Color(70, 74, 82));
        g2.fillOval(x - 2, y - 2, 4, 4);   // buje del motor
    }

    // Misil: sección/aletas rojas arriba y cuerpo verde que termina en punta hacia abajo.
    private void dibujarMisil(Graphics2D g2, int cx, int cy) {
        final int top = cy - 12;
        final int bot = cy + 12;

        // Aletas rojas
        g2.setColor(new Color(200, 45, 45));
        g2.fillPolygon(new int[]{cx - 7, cx - 3, cx - 3}, new int[]{top + 1, top + 9, top}, 3);
        g2.fillPolygon(new int[]{cx + 7, cx + 3, cx + 3}, new int[]{top + 1, top + 9, top}, 3);

        // Sección roja (ojiva/propulsor)
        g2.fillRect(cx - 3, top, 6, 7);
        g2.setColor(new Color(255, 95, 85)); // brillo
        g2.fillRect(cx - 3, top, 2, 7);

        // Cuerpo verde
        g2.setColor(new Color(85, 150, 60));
        g2.fillRect(cx - 3, top + 7, 6, 10);
        g2.setColor(new Color(120, 195, 85)); // brillo
        g2.fillRect(cx - 3, top + 7, 2, 10);

        // Punta verde hacia abajo
        g2.setColor(new Color(85, 150, 60));
        g2.fillPolygon(new int[]{cx - 3, cx + 3, cx}, new int[]{top + 17, top + 17, bot}, 3);
    }

    // Nave del jugador: fuselaje magenta con núcleo amarillo, cañones laterales y
    // llamas de motor azules (estilo pixel-art).
    private void dibujarAvion(Graphics2D g2, int cx, int cy) {
        // Llamas de motor (detrás del cuerpo)
        dibujarLlama(g2, cx,      cy + 12, 8, 18); // motor principal
        dibujarLlama(g2, cx - 14, cy + 10, 5, 12); // motor lateral izquierdo
        dibujarLlama(g2, cx + 14, cy + 10, 5, 12); // motor lateral derecho

        Color cuerpo = new Color(210, 50, 100);
        Color borde  = new Color(140, 25, 70);
        Color brillo = new Color(245, 110, 160);

        // Cañones laterales (barras verticales con punta)
        for (int s = -1; s <= 1; s += 2) {
            int bx = cx + s * 14;
            g2.setColor(borde);
            g2.fillRect(bx - 3, cy - 8, 6, 20);
            g2.setColor(cuerpo);
            g2.fillRect(bx - 2, cy - 8, 4, 20);
            g2.setColor(brillo);
            g2.fillPolygon(new int[]{bx, bx - 3, bx + 3}, new int[]{cy - 16, cy - 8, cy - 8}, 3);
        }

        // Alas (unen el fuselaje con los cañones)
        g2.setColor(borde);
        g2.fillPolygon(new int[]{cx - 4, cx - 16, cx - 4}, new int[]{cy - 2, cy + 10, cy + 12}, 3);
        g2.fillPolygon(new int[]{cx + 4, cx + 16, cx + 4}, new int[]{cy - 2, cy + 10, cy + 12}, 3);

        // Fuselaje (flecha apuntando hacia arriba)
        g2.setColor(cuerpo);
        g2.fillPolygon(
            new int[]{cx, cx - 8, cx - 6, cx + 6, cx + 8},
            new int[]{cy - 22, cy + 2, cy + 14, cy + 14, cy + 2},
            5);
        g2.setColor(brillo); // franja central
        g2.fillPolygon(new int[]{cx, cx - 2, cx + 2}, new int[]{cy - 20, cy + 10, cy + 10}, 3);

        // Núcleo amarillo brillante
        g2.setColor(new Color(255, 210, 60));
        g2.fillOval(cx - 5, cy - 4, 10, 10);
        g2.setColor(new Color(255, 255, 200));
        g2.fillOval(cx - 2, cy - 2, 5, 5);
    }

    private void dibujarLlama(Graphics2D g2, int x, int top, int w, int largo) {
        int L = largo + (int) (Math.random() * 6); // parpadeo
        g2.setColor(new Color(40, 110, 255, 200));
        g2.fillPolygon(new int[]{x - w, x + w, x}, new int[]{top, top, top + L}, 3);
        g2.setColor(new Color(120, 200, 255, 220));
        g2.fillPolygon(new int[]{x - w / 2, x + w / 2, x}, new int[]{top, top, top + L - 4}, 3);
        g2.setColor(new Color(230, 245, 255, 240));
        g2.fillPolygon(new int[]{x - w / 3, x + w / 3, x}, new int[]{top, top, top + L / 2}, 3);
    }

    // Dibuja todas las explosiones activas (con imagen o con figuras de respaldo).
    private void dibujarExplosiones(Graphics2D g2) {
        for (Explosion exp : juego.getExplosionesActivas()) {
            if (Sprites.EXPLOSION != null) {
                dibujarExplosionSprite(g2, exp);
            } else {
                dibujarExplosion(g2, exp);
            }
        }
    }

    // Dibuja una imagen centrada en (cx, cy) con el tamaño indicado.
    private void dibujarSprite(Graphics2D g2, BufferedImage img, int cx, int cy, int w, int h) {
        g2.drawImage(img, cx - w / 2, cy - h / 2, w, h, null);
    }

    // Explosión con imagen: crece y se desvanece a lo largo de su vida.
    private void dibujarExplosionSprite(Graphics2D g2, Explosion exp) {
        int    cx   = Config.xAPixel(exp.getPosicionX());
        int    cy   = Config.altitudAPixel(exp.getPosicionY());
        double prog = exp.getProgreso();

        double maxR = Config.RADIO_DANIO * Config.ESCALA_VISUAL_EXPLOSION / Config.METROS_POR_PIXEL;
        int    diam = (int) (2 * maxR * (0.5 + 0.5 * prog));
        float  alpha = (float) Math.max(0.0, 1.0 - prog * prog);

        Composite original = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.drawImage(Sprites.EXPLOSION, cx - diam / 2, cy - diam / 2, diam, diam, null);
        g2.setComposite(original);
    }

    // Bola de fuego pixelada: núcleo blanco -> amarillo -> naranja -> rojo -> borde
    // negro, con chispas alrededor. El patrón es estable por explosión y crece con ella.
    private void dibujarExplosion(Graphics2D g2, Explosion exp) {
        int    cx   = Config.xAPixel(exp.getPosicionX());
        int    cy   = Config.altitudAPixel(exp.getPosicionY());
        double prog = exp.getProgreso();

        double maxR  = Config.RADIO_DANIO * Config.ESCALA_VISUAL_EXPLOSION / Config.METROS_POR_PIXEL;
        double radio = maxR * (0.5 + 0.5 * prog);          // crece
        int    alpha = (int) (255 * (1.0 - prog * prog));  // se desvanece al final
        if (alpha <= 0) {
            return;
        }

        final int p     = 5;                               // tamaño del "pixel"
        final int seed  = System.identityHashCode(exp);
        final int celdas = (int) Math.ceil(radio * 1.35 / p);

        for (int gx = -celdas; gx <= celdas; gx++) {
            for (int gy = -celdas; gy <= celdas; gy++) {
                double d = Math.sqrt((double) gx * gx + (double) gy * gy) * p;
                double n = ruido(gx, gy, seed);
                double dn = d + (n - 0.5) * p * 2.0;        // borde irregular

                Color c = colorFuego(dn, radio);
                if (c == null) {                            // zona de chispas/escombros
                    if (dn <= radio * 1.3 && n > 0.88) {
                        c = (n > 0.94) ? new Color(240, 140, 30) : new Color(20, 14, 14);
                    } else {
                        continue;
                    }
                }
                g2.setColor(conAlpha(c, alpha));
                g2.fillRect(cx + gx * p - p / 2, cy + gy * p - p / 2, p, p);
            }
        }
    }

    // Elige el color del fuego según qué tan lejos del centro está el "pixel".
    private Color colorFuego(double d, double radio) {
        if (d <= radio * 0.22) return new Color(255, 255, 225); // blanco
        if (d <= radio * 0.42) return new Color(255, 225, 60);  // amarillo
        if (d <= radio * 0.62) return new Color(255, 150, 30);  // naranja
        if (d <= radio * 0.80) return new Color(230, 80, 20);   // naranja rojizo
        if (d <= radio * 0.95) return new Color(150, 30, 15);   // rojo oscuro
        if (d <= radio * 1.05) return new Color(20, 12, 12);    // borde negro
        return null;
    }

    // Devuelve el mismo color pero con un nivel de transparencia (alpha) dado.
    private Color conAlpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, a)));
    }

    // Genera un valor "al azar" pero estable para cada celda (da textura a la explosión).
    private double ruido(int x, int y, int seed) {
        int h = x * 374761393 + y * 668265263 + seed * 69069 + 0x9E3779B9;
        h = (h ^ (h >>> 13)) * 1274126177;
        h ^= (h >>> 16);
        return (h & 0x7fffffff) / (double) 0x7fffffff;
    }

    // Dibuja el HUD: nivel, puntaje, vidas, altitud, energía y la ayuda de controles.
    private void dibujarHud(Graphics2D g2, Avion avion) {
        // Barra superior (franja reservada para el HUD)
        g2.setColor(new Color(10, 12, 22, 225));
        g2.fillRect(0, 0, ANCHO, 36);

        g2.setColor(Color.WHITE);
        g2.drawString("Nivel: " + juego.getNivelActual().getNumeroNivel() + "/" + Juego.NIVEL_MAX, 12, 23);
        g2.drawString("Puntaje: " + juego.getJugadorActual().getPuntaje(),     120, 23);
        g2.drawString("Vidas: "   + juego.getJugadorActual().getVidas(),       250, 23);
        g2.drawString("Altitud: " + (int) avion.getPosicionY() + " m",        340, 23);

        // Barra de energía (a la derecha)
        int energia = (int) avion.getEnergia();
        int barX = ANCHO - 132, barY = 12, barW = 120, barH = 12;
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("E", barX - 14, 23);
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(barX, barY, barW, barH);
        g2.setColor(energia > 30 ? new Color(80, 220, 120) : new Color(230, 90, 90));
        g2.fillRect(barX, barY, (int) (barW / 100.0 * energia), barH);
        g2.setColor(Color.WHITE);
        g2.drawRect(barX, barY, barW, barH);

        g2.drawString("Flechas / WASD para esquivar los misiles", 12, ALTO - 14);
    }
}
