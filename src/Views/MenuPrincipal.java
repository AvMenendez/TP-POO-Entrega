package Views;

import javax.swing.Box;
import java.awt.Dimension;

// Pantalla inicial de Sky Defense con sus cuatro opciones.
public class MenuPrincipal extends PanelFondo {

    // Arma el menú con el título y los cuatro botones (cada uno ejecuta su acción).
    public MenuPrincipal(Runnable onJugar, Runnable onLeaderboard, Runnable onOpciones, Runnable onExit) {
        super(Sprites.FONDO_MENU); // fondo propio del menú (cae al general si no está)

        add(Box.createRigidArea(new Dimension(0, 60))); // margen superior fijo: título arriba
        String titulo = "<html>" + Estilo.coloreado("SKY", Estilo.DERROTA)
                + " " + Estilo.coloreado("DEFENSE", Estilo.ACENTO) + "</html>";
        add(Estilo.tituloSilkscreen(titulo, 52));
        add(Box.createVerticalGlue());
        add(Estilo.botonTransparente("Jugar", 20, onJugar));
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(Estilo.botonTransparente("Leaderboard", 20, onLeaderboard));
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(Estilo.botonTransparente("Opciones", 20, onOpciones));
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(Estilo.botonTransparente("Exit", 20, onExit));
        add(Box.createVerticalGlue());
    }
}
