package Model;

public abstract class ObjetoVolador {
	// Todo en metros: posicionX es la posición horizontal y posicionY es la ALTITUD
	// (a mayor valor, más alto). La conversión a píxeles la hace la Vista (ver Config).
	protected float posicionX;
	protected float posicionY;



	// Crea el objeto en una posición y altitud iniciales (en metros).
	public ObjetoVolador(float posicionX, float posicionY) {
		this.posicionX = posicionX;
		this.posicionY = posicionY;
	}

	// Devuelve la posición horizontal (en metros).
	public float getPosicionX() {
		return posicionX;
	}
	// Devuelve la altitud (en metros).
	public float getPosicionY() {
		return posicionY;
	}

	// Suma un desplazamiento a la posición actual (mueve el objeto).
	protected void actualizarPosicion(float deltaX, float deltaY) {
        this.posicionX += deltaX;
        this.posicionY += deltaY;
    }
}
