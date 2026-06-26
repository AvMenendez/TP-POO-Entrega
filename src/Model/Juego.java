package Model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Juego {

    public  static final int TAM_ESCUADRON  = 10;   // unidades por escuadrón (consigna)
    public  static final int NIVEL_MAX      = 15;   // tope de niveles: al superarlo, se gana el juego
    private static final int DRONES_ACTIVOS = 4;    // máximo de drones simultáneos en pantalla

    private static final double SEPARACION_SPAWN = Config.ANCHO_M / DRONES_ACTIVOS;

    private static final int ESPERA_SPAWN_MIN = 10;
    private static final int ESPERA_SPAWN_MAX = 40;

    private static final double DRON_VEL_BASE     = 22.0;
    private static final double MISIL_VEL_BASE    = 32.0;
    private static final double FREC_DISPARO_BASE = 0.04;

    // Puntaje por esquive según zona de explosión
    // > 150 m : explosión lejos,  avión a salvo       → 40 pts  (esquive limpio)
    // 80–150 m: explosión media,  avión roza la onda  → 20 pts  (esquive ajustado + daño leve)
    // 20–80 m : explosión cerca,  avión muy expuesto  → 10 pts  (esquive por los pelos + daño alto)
    // < 20 m  : explosión crítica, avión impactado    →  0 pts  + pierde una vida
    private static final int PUNTOS_NIVEL = 300;   // por superar un nivel
    private static final int PUNTOS_LEJOS = 40;    // explosión a > 150 m
    private static final int PUNTOS_MEDIA = 20;    // explosión entre 80 y 150 m
    private static final int PUNTOS_CERCA = 10;    // explosión entre 20 y 80 m (esquive por los pelos)

    // Distancias de zona (metros)
    private static final double DIST_LEJOS   = Config.RADIO_DANIO;       // 150
    private static final double DIST_MEDIA   = Config.RADIO_DANIO_ALTO;  // 80
    private static final double DIST_CERCA   = Config.RADIO_CRITICO;     // 20
    private static final double DIST_AMENAZA = Config.RADIO_AMENAZA;     // 600: tope del esquive

    // Daño por zona (% de energía)
    private static final int DANIO_MEDIA = 20;
    private static final int DANIO_CERCA = 40;

    // Atributos
    private Jugador         jugadorActual;
    private Nivel           nivelActual;
    private Escuadron       escuadronActual;
    private List<Misil>     misilesActivos;
    private List<Explosion> explosionesActivas;
    private EstadoJuego     estado;
    private int             ticksParaProximoSpawn;


    // Crea la partida con el jugador, el nivel y el escuadrón iniciales.
    public Juego(Jugador jugadorActual, Nivel nivelActual, Escuadron escuadronActual) {
        this.jugadorActual      = jugadorActual;
        this.nivelActual        = nivelActual;
        this.escuadronActual    = escuadronActual;
        this.misilesActivos     = new ArrayList<>();
        this.explosionesActivas = new ArrayList<>();
        this.estado             = EstadoJuego.JUGANDO;
    }


    // Prepara una partida nueva: limpia misiles/explosiones y crea los primeros drones.
    public void iniciarJuego() {
        misilesActivos.clear();
        explosionesActivas.clear();
        estado = EstadoJuego.JUGANDO;
        controlarSpawnDrones();
    }


    // Avanza un tick del juego: mueve todo, resuelve explosiones y revisa si se ganó/perdió.
    public void actualizarJuego() {
        if (estado != EstadoJuego.JUGANDO) {
            return;
        }

        Avion avion = jugadorActual.getAvionActivo();

        // El escuadrón mueve sus drones y devuelve los misiles que dispararon.
        misilesActivos.addAll(escuadronActual.actualizar());

        // Los misiles descienden y explotan al alcanzar su altitud de detonación
        // o al tocar la nave. En ambos casos dejan una explosión que daña por distancia.
        List<Misil> misilesAEliminar = new ArrayList<>();
        for (Misil misil : misilesActivos) {
            misil.descender();
            if (misil.haDetonado() || misil.impactaA(avion)) {
                explosionesActivas.add(new Explosion(misil.getPosicionX(), misil.getPosicionY()));
                misilesAEliminar.add(misil);
            }
        }
        misilesActivos.removeAll(misilesAEliminar);

        actualizarExplosiones(avion);

        controlarSpawnDrones();
        verificarCondicionVictoria();
    }

    private void actualizarExplosiones(Avion avion) {
        List<Explosion> aEliminar = new ArrayList<>();
        for (Explosion exp : explosionesActivas) {
            exp.envejecer();

            if (!exp.estaResuelta()) {
                double distancia = exp.distanciaAlAvion(avion);
                if (distancia <= DIST_LEJOS) {      // el avión entró en alguna zona de daño
                    aplicarDanioPorDistancia(distancia);
                    exp.marcarResuelta();
                } else if (exp.terminada()) {        // la explosión se apagó sin tocar al avión
                    if (distancia <= DIST_AMENAZA) { // 150–600 m: el misil fue una amenaza real → esquive
                        jugadorActual.sumarPuntos(PUNTOS_LEJOS);
                    }                                // > 600 m: nunca fue peligro → no otorga puntos
                    exp.marcarResuelta();
                }
            }

            if (exp.terminada()) {
                aEliminar.add(exp);
            }
        }
        explosionesActivas.removeAll(aEliminar);
    }

    // Aplica daño y puntaje según la zona en la que explotó el misil respecto al avión.
    //  Zona         Distancia      Puntos        Daño
    //  Lejos        > 150 m        40 pts        ninguno      (resuelto en actualizarExplosiones)
    //  Media        80 – 150 m     20 pts        -20% energía
    //  Cerca        20 – 80 m      10 pts        -40% energía (esquive por los pelos)
    //  Crítica      < 20 m          0 pts        pierde 1 vida
    private void aplicarDanioPorDistancia(double distancia) {
        if (distancia >= DIST_MEDIA) {              // 80–150 m: daño leve + puntos
            jugadorActual.sumarPuntos(PUNTOS_MEDIA);
            jugadorActual.recibirDanio(DANIO_MEDIA);
        } else if (distancia >= DIST_CERCA) {       // 20–80 m: daño alto + puntos reducidos
            jugadorActual.sumarPuntos(PUNTOS_CERCA);
            jugadorActual.recibirDanio(DANIO_CERCA);
        } else {                                    // < 20 m: impacto crítico, pierde una vida
            jugadorActual.modificarVidas(-1);
            GestorSonido.reproducir("/Sounds/perderVida.wav");
        }
    }


    public void controlarSpawnDrones() {
        if (escuadronActual.getDrones().size() >= DRONES_ACTIVOS
                || escuadronActual.getDronesRestantes() == 0) {
            return;
        }
        if (ticksParaProximoSpawn > 0) {
            ticksParaProximoSpawn--;
            return;
        }

        boolean desdeIzquierda = Math.random() < 0.5;
        if (!entradaLibre(desdeIzquierda)) {
            desdeIzquierda = !desdeIzquierda;
            if (!entradaLibre(desdeIzquierda)) {
                return;
            }
        }

        double mult       = nivelActual.getMultiplicadorDificultad();
        double velocidad  = DRON_VEL_BASE     * mult;
        double frecuencia = FREC_DISPARO_BASE * mult;
        double velMisil   = MISIL_VEL_BASE    * mult;
        int    cooldown   = Math.max(1, (int) Math.round(Config.COOLDOWN_DISPARO_TICKS / mult));

        float xSpawn    = desdeIzquierda ? 0f : (float) Config.ANCHO_M;
        int   direccion = desdeIzquierda ? 1 : -1;

        Dron nuevoDron = new Dron(xSpawn, (float) Config.ALTITUD_MAX,
                direccion, velocidad, frecuencia, velMisil, cooldown);
        escuadronActual.agregarDron(nuevoDron);

        ticksParaProximoSpawn = ESPERA_SPAWN_MIN
                + (int) (Math.random() * (ESPERA_SPAWN_MAX - ESPERA_SPAWN_MIN + 1));
    }

    private boolean entradaLibre(boolean desdeIzquierda) {
        double xEntrada = desdeIzquierda ? 0.0 : Config.ANCHO_M;
        for (Dron dron : escuadronActual.getDrones()) {
            if (Math.abs(dron.getPosicionX() - xEntrada) < SEPARACION_SPAWN) {
                return false;
            }
        }
        return true;
    }


    public void verificarCondicionVictoria() {
        if (!jugadorActual.estaVivo()) {
            estado = EstadoJuego.GAME_OVER;
            return;
        }

        if (escuadronActual.estaDestruido()) {
            jugadorActual.sumarPuntos(PUNTOS_NIVEL);
            GestorSonido.reproducir("/Sounds/subirDeNivel.wav");

            if (nivelActual.getNumeroNivel() >= NIVEL_MAX) {
                estado = EstadoJuego.VICTORIA;
                return;
            }

            nivelActual.avanzarNivel();
            escuadronActual = new Escuadron(TAM_ESCUADRON);
            controlarSpawnDrones();
        }
    }


    public EstadoJuego getEstado() {
        return estado;
    }

    public Jugador getJugadorActual() {
        return jugadorActual;
    }

    public Nivel getNivelActual() {
        return nivelActual;
    }

    public Escuadron getEscuadronActual() {
        return escuadronActual;
    }

    // Solo lectura: la Vista únicamente recorre estas listas para dibujarlas.
    public List<Misil> getMisilesActivos() {
        return Collections.unmodifiableList(misilesActivos);
    }

    public List<Explosion> getExplosionesActivas() {
        return Collections.unmodifiableList(explosionesActivas);
    }
}