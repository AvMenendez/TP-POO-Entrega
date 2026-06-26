package Model;

// Estado global de la partida; lo usa la capa de presentación para saber
// cuándo detener el bucle y mostrar la pantalla de fin (derrota o victoria).
public enum EstadoJuego {
    JUGANDO,
    GAME_OVER,   // el jugador se quedó sin vidas
    VICTORIA     // el jugador superó el último nivel (NIVEL_MAX)
}
