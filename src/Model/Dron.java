package Model;

public class Dron extends ObjetoVolador {

	private int    direccionHorizontal;   // +1 mueve a la derecha, -1 a la izquierda
	private double velocidadMovimiento;   // m por tick
	private double frecuenciaDisparo;     // probabilidad de disparar en cada tick
	private double velocidadMisil;        // velocidad de caída de los misiles que lanza (m por tick)
	private int    cooldownDisparo;       // intervalo (ticks) entre misiles, ya escalado por nivel
	private int    cooldownRestante;      // ticks que faltan para poder volver a disparar

	// Crea el dron con su posición, dirección, velocidad y datos de disparo. El cooldown
	// se recibe ya escalado por el nivel (como la velocidad y la frecuencia).
	public Dron(float posicionX, float altitud, int direccionHorizontal,
			double velocidadMovimiento, double frecuenciaDisparo, double velocidadMisil,
			int cooldownDisparo) {
		super(posicionX, altitud);
		this.direccionHorizontal = direccionHorizontal;
		this.velocidadMovimiento = velocidadMovimiento;
		this.frecuenciaDisparo   = frecuenciaDisparo;
		this.velocidadMisil      = velocidadMisil;
		this.cooldownDisparo     = cooldownDisparo;
	}

	public void desplazarse() {
		float deltaX = (float) (direccionHorizontal * velocidadMovimiento);
		actualizarPosicion(deltaX, 0);
	}

	public Misil evaluarDisparo() {
		// Respeta el intervalo mínimo entre misiles del mismo dron.
		if (cooldownRestante > 0) {
			cooldownRestante--;
			return null;
		}
		if (Math.random() < frecuenciaDisparo) {
			cooldownRestante = cooldownDisparo;
			return new Misil(getPosicionX(), getPosicionY(), velocidadMisil);
		}
		return null;
	}

	// Sale de escena al cruzar cualquiera de los bordes laterales del mundo.
	public boolean estaFueraDePantalla() {
		float x = getPosicionX();
		return x < 0f || x > Config.ANCHO_M;
	}
}
