package Controlador;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Traduce el teclado en intención de movimiento: lleva qué direcciones están activas
// (flechas o WASD). Separa el manejo de input del orquestador del juego (SRP): el
// ControladorJuego solo consulta el estado, no se entera de códigos de tecla.
public class ControladorTeclado extends KeyAdapter {

    private boolean izquierda, derecha, arriba, abajo;

    // Se llama al apretar una tecla: la marca como presionada.
    @Override
    public void keyPressed(KeyEvent e) {
        actualizarTecla(e.getKeyCode(), true);
    }

    // Se llama al soltar una tecla: la marca como no presionada.
    @Override
    public void keyReleased(KeyEvent e) {
        actualizarTecla(e.getKeyCode(), false);
    }

    // Activa o desactiva la dirección de movimiento según la tecla.
    private void actualizarTecla(int codigo, boolean presionada) {
        switch (codigo) {
            case KeyEvent.VK_LEFT:  case KeyEvent.VK_A: izquierda = presionada; break;
            case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: derecha   = presionada; break;
            case KeyEvent.VK_UP:    case KeyEvent.VK_W: arriba    = presionada; break;
            case KeyEvent.VK_DOWN:  case KeyEvent.VK_S: abajo     = presionada; break;
            default: /* otras teclas se ignoran */ break;
        }
    }

    // Olvida todas las teclas (al empezar una partida nueva).
    public void reiniciar() {
        izquierda = derecha = arriba = abajo = false;
    }

    public boolean izquierda() { return izquierda; }
    public boolean derecha()   { return derecha; }
    public boolean arriba()    { return arriba; }
    public boolean abajo()     { return abajo; }
}
