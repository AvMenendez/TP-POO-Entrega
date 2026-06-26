package Model;

public class Avion extends ObjetoVolador{

	private static final float ENERGIA_MAXIMA = 100f;

	private float energia;

	// Crea la nave en una posición/altitud con la energía al máximo.
	public Avion(float posicionX, float posicionY) {
		super (posicionX, posicionY);
		this.energia = ENERGIA_MAXIMA;
	}

	// Mueve la nave a izquierda/derecha.
	public void desplazarLateral(int deltaX) {
		actualizarPosicion(deltaX, 0);
	}

	// Cambia la altitud de la nave hacia una nueva altura.
	public void variarAltitud(int nuevaY) {
		float deltaY = nuevaY - getPosicionY() ;
		actualizarPosicion(0, deltaY);
	}

	// Resta energía a la nave (sin bajar de 0).
	public void recibirDanio(int porcentaje) {
		this.energia = Math.max(0, this.energia - porcentaje);
	}

	// Indica si la nave todavía tiene energía.
	public boolean estaActivo() {
		return this.energia>0;
	}

	// Repone la energía al máximo (se usa al perder una vida).
	public void reponerEnergia() {
		this.energia = ENERGIA_MAXIMA;
	}

	// Radio (en metros) del cuerpo de la nave: su hitbox para el choque con misiles.
	public float getRadio() {
		return (float) Config.RADIO_AVION;
	}

	// Devuelve la energía actual de la nave.
	public float getEnergia() {
		return energia;
	}
}
