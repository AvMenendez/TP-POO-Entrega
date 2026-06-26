package Model;


public class Misil extends ObjetoVolador {

	private final double altitudDetonacion;   // altitud (m) a la que explota; aleatoria al lanzarse
	private final double velocidadCaida;      // m por tick (escala con el nivel)

	// Al lanzarse, el sistema de detonación se programa a una altitud aleatoria
	// entre DETONACION_MIN y DETONACION_MAX (consigna). Sin sistema de guía: cae recto.
	public Misil(float posicionX, float altitud, double velocidadCaida) {
		super(posicionX, altitud);
		this.velocidadCaida    = velocidadCaida;
		this.altitudDetonacion = Config.DETONACION_MIN
				+ Math.random() * (Config.DETONACION_MAX - Config.DETONACION_MIN);
	}

	// Desciende en línea recta: baja su altitud.
	public void descender() {
		actualizarPosicion(0, (float) -velocidadCaida);
	}

	// Explota al alcanzar (o pasar) su altitud de detonación programada.
	public boolean haDetonado() {
		return getPosicionY() <= altitudDetonacion;
	}

	// Impacta de frente a la nave si su cuerpo toca el hitbox del avión: la distancia
	// entre centros es menor que la suma de ambos radios (no se lo puede atravesar).
	public boolean impactaA(Avion avion) {
		double dx = getPosicionX() - avion.getPosicionX();
		double dy = getPosicionY() - avion.getPosicionY();
		double distancia = Math.sqrt(dx * dx + dy * dy);
		return distancia <= avion.getRadio() + Config.RADIO_MISIL;
	}
}
