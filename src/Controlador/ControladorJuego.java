package Controlador;

import Model.Avion;
import Model.Config;
import Model.EstadoJuego;
import Model.Escuadron;
import Model.GestorSonido;
import Model.Juego;
import Model.Jugador;
import Model.Leaderboard;
import Model.Nivel;
import Views.MenuPrincipal;
import Views.PanelJuego;
import Views.PanelLeaderboard;
import Views.PanelNombre;
import Views.PanelOpciones;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

// Orquesta la partida: crea el modelo, ejecuta el game loop y navega entre menú / juego /
// leaderboard / opciones / game over. El teclado lo maneja ControladorTeclado (SRP).
public class ControladorJuego {

    private static final int DELAY_MS = Config.MS_POR_TICK; // duración de cada tick (~33 fps)

    private final JFrame frame = new JFrame("Sky Defense");
    private final ControladorGameOver controladorGameOver = new ControladorGameOver();
    private final ControladorTeclado teclado = new ControladorTeclado();
    private final Leaderboard leaderboard = new Leaderboard();

    private Juego      juego;
    private PanelJuego panelJuego;
    private Timer      timer;
    private String     nombreJugador = "Anónimo";

    // Arma la ventana principal y muestra el menú inicial.
    public void iniciar() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        mostrarMenu();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Muestra el menú principal y detiene el juego si estaba corriendo.
    private void mostrarMenu() {
        detenerTimer();
        GestorSonido.reproducirMusicaEnLoop("/Sounds/Entry_Screen_Rimworld_OST.wav"); // música de inicio
        mostrar(new MenuPrincipal(this::mostrarPedirNombre, this::mostrarLeaderboard, this::mostrarOpciones, this::salir));
    }

    // Muestra la pantalla para ingresar el nombre y luego empieza la partida.
    private void mostrarPedirNombre() {
        mostrar(new PanelNombre(nombre -> {
            this.nombreJugador = nombre;
            nuevaPartida();
        }, this::mostrarMenu));
    }

    // Muestra la tabla de mejores puntajes.
    private void mostrarLeaderboard() {
        mostrar(new PanelLeaderboard(leaderboard.obtenerTop(), this::mostrarMenu));
    }

    // Muestra la pantalla de opciones.
    private void mostrarOpciones() {
        mostrar(new PanelOpciones(this::mostrarMenu));
    }

    // Crea una partida nueva (modelo, vista y bucle de juego) y la arranca.
    private void nuevaPartida() {
        teclado.reiniciar();
        GestorSonido.reproducirMusicaEnLoop("/Sounds/Enemy_Approaching.wav"); // música durante la partida

        Avion avion         = new Avion((float) (Config.ANCHO_M / 2), (float) Config.ALTITUD_INICIAL);
        Jugador jugador     = new Jugador(nombreJugador, Config.VIDAS_INICIALES, avion);
        Nivel nivel         = new Nivel(1, 1.0);
        Escuadron escuadron = new Escuadron(Juego.TAM_ESCUADRON);

        juego = new Juego(jugador, nivel, escuadron);
        juego.iniciarJuego();

        panelJuego = new PanelJuego(juego);
        panelJuego.addKeyListener(teclado);
        mostrar(panelJuego);

        detenerTimer();
        timer = new Timer(DELAY_MS, e -> tick());
        timer.start();
    }

    // Un paso del juego: mueve la nave, actualiza el modelo y redibuja.
    private void tick() {
        moverAvion();
        juego.actualizarJuego();
        panelJuego.repaint();

        EstadoJuego estado = juego.getEstado();
        if (estado != EstadoJuego.JUGANDO) {        // fin de partida: derrota o victoria
            detenerTimer();
            int puntaje = juego.getJugadorActual().getPuntaje();
            leaderboard.registrar(juego.getJugadorActual().getNombreUsuario(), puntaje);
            mostrarFin(puntaje, estado == EstadoJuego.VICTORIA);
        }
    }

    // Mueve la nave según las teclas activas, sin salirse de los límites.
    private void moverAvion() {
        Avion avion = juego.getJugadorActual().getAvionActivo();
        float x   = avion.getPosicionX();   // horizontal (m)
        float alt = avion.getPosicionY();   // altitud (m)

        if (teclado.izquierda() && x > 0)              avion.desplazarLateral((int) -Config.VEL_HORIZONTAL);
        if (teclado.derecha()   && x < Config.ANCHO_M) avion.desplazarLateral((int)  Config.VEL_HORIZONTAL);
        // "Arriba" sube la altitud; "abajo" la baja, dentro del rango permitido.
        if (teclado.arriba()    && alt < Config.ALTITUD_MAX)
            avion.variarAltitud((int) Math.min(Config.ALTITUD_MAX, alt + Config.VEL_ALTITUD));
        if (teclado.abajo()     && alt > Config.ALTITUD_MIN)
            avion.variarAltitud((int) Math.max(Config.ALTITUD_MIN, alt - Config.VEL_ALTITUD));
    }

    // Muestra la pantalla de fin de partida (derrota o victoria) con el puntaje obtenido.
    private void mostrarFin(int puntaje, boolean gano) {
        mostrar(controladorGameOver.crearPanel(puntaje, gano, this::nuevaPartida, this::mostrarMenu, this::salir));
    }

    // Detiene el bucle de juego si está activo.
    private void detenerTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    // Cierra la aplicación.
    private void salir() {
        frame.dispose();
        System.exit(0);
    }

    // Cambia la pantalla que se ve en la ventana.
    private void mostrar(JComponent componente) {
        frame.setContentPane(componente);
        frame.revalidate();
        frame.repaint();
        componente.requestFocusInWindow();
    }
}
