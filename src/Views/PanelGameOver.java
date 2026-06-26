package Views;

import javax.swing.Box;
import java.awt.Dimension;

// Pantalla de fin de partida (derrota o victoria): muestra el puntaje y ofrece reiniciar o salir.
public class PanelGameOver extends PanelFondo {

    // Arma la pantalla con el puntaje final y los botones de reiniciar/menú/salir.
    // 'gano' decide el título: victoria (superó el último nivel) o derrota.
    public PanelGameOver(int puntaje, boolean gano, Runnable onReiniciar, Runnable onMenu, Runnable onSalir) {
        add(Box.createVerticalGlue());
        add(Estilo.titulo(gano ? "¡GANASTE!" : "GAME OVER", 48, gano ? Estilo.VICTORIA : Estilo.DERROTA));
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(Estilo.etiqueta("Puntaje final: " + puntaje, 22, Estilo.TEXTO));
        add(Box.createRigidArea(new Dimension(0, 32)));
        add(Estilo.botonTransparente("Volver a jugar", 18, onReiniciar));
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(Estilo.botonTransparente("Volver al inicio", 16, onMenu));
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(Estilo.botonTransparente("Salir", 16, onSalir));
        add(Box.createVerticalGlue());
    }
}
