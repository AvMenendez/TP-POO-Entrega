package Model;

// Explosión que deja un misil al detonar. Ocupa un área (radio de daño) durante
// unos instantes, de modo que el daño cubre una zona y no solo el impacto directo.
public class Explosion extends ObjetoVolador {

    private static final int DURACION_TICKS = 15; // ~0,45 s a 30 ms por tick

    private int     vidaRestante;
    private boolean resuelta;   // si ya se evaluó su efecto sobre el avión

    // Crea la explosión en un punto, con su tiempo de vida inicial.
    public Explosion(float x, float altitud) {
        super(x, altitud);
        this.vidaRestante = DURACION_TICKS;
        this.resuelta = false;
    }

    // Descuenta un tick de vida (la explosión se va apagando).
    public void envejecer() {
        vidaRestante--;
    }

    // Indica si la explosión ya terminó (se agotó su vida).
    public boolean terminada() {
        return vidaRestante <= 0;
    }

    // Indica si ya se calculó su efecto sobre el avión.
    public boolean estaResuelta() {
        return resuelta;
    }

    // Marca la explosión como ya evaluada (para no aplicar daño dos veces).
    public void marcarResuelta() {
        resuelta = true;
    }

    // 0.0 recién creada -> 1.0 a punto de desaparecer (para animar el radio y el desvanecido).
    public double getProgreso() {
        return 1.0 - (double) vidaRestante / DURACION_TICKS;
    }

    // Distancia 2D (en metros) entre la explosión y el CUERPO del avión: a la distancia
    // hasta su centro se le resta el radio del cuerpo (`RADIO_AVION`), porque la aeronave
    // ocupa espacio (la consigna habla de "la distancia entre la explosión y la aeronave").
    // Los tramos 20/80/150 m de la tabla se aplican sobre esta distancia al cuerpo.
    public double distanciaAlAvion(Avion a) {
        double dx = getPosicionX() - a.getPosicionX();
        double dy = getPosicionY() - a.getPosicionY();
        double distanciaAlCentro = Math.sqrt(dx * dx + dy * dy);
        return Math.max(0.0, distanciaAlCentro - a.getRadio());
    }
}
