package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Escuadron {

    private int        dronesRestantes;
    private List<Dron> drones;


    // Crea el escuadrón con la cantidad de drones que le quedan por enviar.
     public Escuadron(int dronesRestantes) {
        this.dronesRestantes = dronesRestantes;
        this.drones          = new ArrayList<>();
    }

    // Mueve todos los drones, quita los que salieron y devuelve los misiles disparados.
    public List<Misil> actualizar() {
        List<Misil> misilesGenerados = new ArrayList<>();
        List<Dron>  dronesAEliminar  = new ArrayList<>();

        for (Dron dron : drones) {
            dron.desplazarse();

            if (dron.estaFueraDePantalla()) {
                dronesAEliminar.add(dron);
                continue;
            }

            Misil misil = dron.evaluarDisparo();
            if (misil != null) {
                misilesGenerados.add(misil);
            }
        }

        drones.removeAll(dronesAEliminar);

        return misilesGenerados;
    }


    // Devuelve cuántos drones faltan por enviar a la pantalla.
    public int getDronesRestantes() {
        return dronesRestantes;
    }

    // Indica si ya no quedan drones por enviar ni en pantalla (nivel superado).
    public boolean estaDestruido() {
        return dronesRestantes == 0 && drones.isEmpty();
    }

    // Agrega un dron a la pantalla y descuenta uno de los que quedaban.
    public void agregarDron(Dron dron) {
        if (dronesRestantes > 0) {
            drones.add(dron);
            dronesRestantes--;
        }
    }

    // Devuelve la lista de drones en pantalla en SOLO LECTURA (la Vista solo la recorre).
    public List<Dron> getDrones() {
        return Collections.unmodifiableList(drones);
    }
}
