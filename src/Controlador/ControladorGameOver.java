package Controlador;

import Views.PanelGameOver;

import javax.swing.JPanel;

// Arma la pantalla de fin de partida y la conecta con las acciones de reiniciar / menú / salir.
public class ControladorGameOver {

    // Crea el panel de fin con el puntaje, si ganó o perdió, y los botones de reiniciar/menú/salir.
    public JPanel crearPanel(int puntaje, boolean gano, Runnable onReiniciar, Runnable onMenu, Runnable onSalir) {
        return new PanelGameOver(puntaje, gano, onReiniciar, onMenu, onSalir);
    }
}
