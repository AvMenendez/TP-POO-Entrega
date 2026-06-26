package Views;

import Model.Leaderboard;

import javax.swing.Box;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

// Muestra las 5 mejores partidas registradas.
public class PanelLeaderboard extends PanelFondo {

    // Arma la pantalla listando las mejores partidas (o un aviso si no hay ninguna).
    public PanelLeaderboard(List<Leaderboard.Entrada> top, Runnable onVolver) {
        add(Box.createVerticalGlue());
        add(Estilo.titulo("LEADERBOARD", 40, Estilo.ORO));
        add(Box.createRigidArea(new Dimension(0, 28)));

        if (top.isEmpty()) {
            add(crearLinea("Todavía no hay partidas registradas.", false));
        } else {
            for (int i = 0; i < top.size(); i++) {
                Leaderboard.Entrada e = top.get(i);
                add(crearLinea((i + 1) + ".  " + e.getNombre() + "  —  " + e.getPuntaje() + " pts", true));
                add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }

        add(Box.createRigidArea(new Dimension(0, 28)));
        add(Estilo.botonTransparente("Volver", 18, onVolver));
        add(Box.createVerticalGlue());
    }

    // Crea una línea de texto del listado (con o sin fuente monoespaciada).
    private JLabel crearLinea(String texto, boolean monospace) {
        JLabel label = new JLabel(texto);
        label.setForeground(Estilo.TEXTO);
        label.setFont(new Font(monospace ? "Monospaced" : "SansSerif", Font.PLAIN, 22));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
}
