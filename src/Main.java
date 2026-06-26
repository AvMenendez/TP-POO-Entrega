import Controlador.ControladorJuego;

import javax.swing.SwingUtilities;

public class Main {
    // Punto de entrada del programa: arranca el juego.
    public static void main(String[] args) {
        // Toda la UI Swing debe construirse en el Event Dispatch Thread.
        SwingUtilities.invokeLater(() -> new ControladorJuego().iniciar());
    }
}
