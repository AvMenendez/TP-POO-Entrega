package Model;
public class Nivel {
	// Cada nivel incrementa velocidad de drones, caída de misiles y frecuencia de
	// disparo en un 15% (consigna).
	public static final double INCREMENTO_POR_NIVEL = 0.15;

	private int numeroNivel;
	private double multiplicadorDificultad;

	// Crea el nivel con su número y su multiplicador de dificultad.
	public Nivel(int numeroNivel, double multiplicadorDificultad) {
		this.numeroNivel = numeroNivel;
		this.multiplicadorDificultad = multiplicadorDificultad;
	}

	// Pasa al siguiente nivel y sube la dificultad un 15%.
	public void avanzarNivel() {
		this.numeroNivel++;
		this.multiplicadorDificultad *= (1.0 + INCREMENTO_POR_NIVEL);
	}


	// Devuelve el multiplicador de dificultad actual.
	public double getMultiplicadorDificultad() {
		return multiplicadorDificultad;
	}

	// Devuelve el número de nivel actual.
	public int getNumeroNivel() {
		return numeroNivel;
	}
}