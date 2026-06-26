package Model;

// Parámetros del mundo de juego. El modelo trabaja en METROS (como la consigna);
// la Vista convierte a píxeles con la misma escala en ambos ejes.
public final class Config {

    private Config() { }

    // Pantalla
    public static final int ANCHO_PX = 1000;
    public static final int ALTO_PX  = 780;

    // Tiempo del bucle de juego.
    public static final int MS_POR_TICK = 30; // duración de cada tick (~33 fps)
    // Intervalo mínimo entre misiles de un mismo dron: 1 segundo, en ticks.
    public static final int COOLDOWN_DISPARO_TICKS = Math.max(1, Math.round(1000f / MS_POR_TICK));

    // Escala única para ambos ejes (metros por píxel). Más chico = cámara más cerca
    // = objetos más grandes en pantalla (el rango de altitud debe seguir entrando).
    public static final double METROS_POR_PIXEL = 6.0;

    // Mundo en metros
    public static final double ANCHO_M     = ANCHO_PX * METROS_POR_PIXEL; // 6000 m de ancho

    // Altitudes (metros) — reglas de la consigna
    public static final double ALTITUD_MIN    = 1000;  // altitud mínima del avión
    public static final double ALTITUD_MAX    = 5000;  // altitud máxima del avión y altura de los drones

    // Nave del jugador (tunables de jugabilidad, antes dispersos en el Controlador)
    public static final double VEL_HORIZONTAL  = 56;   // m por tick que se mueve la nave de lado
    public static final double VEL_ALTITUD     = 56;   // m por tick que sube/baja la nave
    public static final double ALTITUD_INICIAL = 1500; // altitud (m) al empezar la partida
    public static final int    VIDAS_INICIALES = 3;    // vidas con las que arranca el jugador
    public static final double DETONACION_MIN = 1200;  // altitud mínima de detonación de un misil
    public static final double DETONACION_MAX = 4500;  // altitud máxima de detonación de un misil

    // Distancias de DAÑO de la consigna (metros), exactas: >150 a salvo + puntos;
    // 80–150 -20%; 20–80 -40%; <20 pierde una vida. (ESCALA = 1.0 = consigna pura.)
    public static final double ESCALA_RADIO_EXPLOSION = 1.0;
    public static final double RADIO_DANIO      = 150 * ESCALA_RADIO_EXPLOSION; // daño; fuera, a salvo + puntos
    public static final double RADIO_DANIO_ALTO = 80  * ESCALA_RADIO_EXPLOSION; // daño mayor
    public static final double RADIO_CRITICO    = 20  * ESCALA_RADIO_EXPLOSION; // pierde una vida

    // Banda exterior de "amenaza" (metros, medidos al cuerpo del avión): solo los misiles
    // que detonan dentro de RADIO_DANIO..RADIO_AMENAZA cuentan como esquive real (+40 pts).
    // Más allá de esto el misil nunca fue un peligro y no otorga puntos, para que el puntaje
    // (y por lo tanto las vidas extra) no se dispare por misiles que caen lejos del avión.
    public static final double RADIO_AMENAZA = 600;

    // Radio del CUERPO del avión y del misil (metros). Dos usos:
    //  - Daño: la distancia de la explosión se mide al CUERPO del avión (centro − RADIO_AVION),
    //    porque la aeronave ocupa espacio; los tramos 20/80/150 se aplican a esa distancia.
    //  - Choque: si el cuerpo del misil (RADIO_MISIL) toca el del avión, el misil DETONA en
    //    el acto (no se lo puede atravesar) y el daño sale de la misma tabla por distancia.
    public static final double RADIO_AVION = 200;
    public static final double RADIO_MISIL = 40;

    // Escala SOLO visual de la explosión (no afecta el daño): la bola de fuego se
    // dibuja más grande que el radio de daño para que se vea dramática.
    public static final double ESCALA_VISUAL_EXPLOSION = 2.5;

    // Margen superior en píxeles: deja una franja arriba para el HUD, de modo que los
    // drones (que vuelan a la altitud máxima) queden por debajo de la barra superior.
    private static final int MARGEN_SUP_PX = 70;

    // Conversión horizontal: metros -> píxel.
    public static int xAPixel(double xMetros) {
        return (int) (xMetros / METROS_POR_PIXEL);
    }

    // Conversión vertical: una altitud mayor se dibuja más arriba (y menor).
    public static int altitudAPixel(double altitudMetros) {
        return (int) (MARGEN_SUP_PX + (ALTITUD_MAX - altitudMetros) / METROS_POR_PIXEL);
    }
}
