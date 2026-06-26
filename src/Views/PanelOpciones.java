package Views;

import Model.GestorSonido;

import javax.swing.Box;
import javax.swing.JButton;
import java.awt.Dimension;
import java.util.function.BooleanSupplier;

// Pantalla de opciones: permite silenciar la música y los efectos por separado.
public class PanelOpciones extends PanelFondo {

    // Arma la pantalla con los toggles de sonido y el botón volver.
    public PanelOpciones(Runnable onVolver) {
        add(Box.createVerticalGlue());
        add(Estilo.titulo("OPCIONES", 40, Estilo.ACENTO));
        add(Box.createRigidArea(new Dimension(0, 28)));
        add(crearToggle("Musica", GestorSonido::musicaSilenciada,
                () -> GestorSonido.setMusicaSilenciada(!GestorSonido.musicaSilenciada())));
        add(Box.createRigidArea(new Dimension(0, 16)));
        add(crearToggle("SFX", GestorSonido::efectosSilenciados,
                () -> GestorSonido.setEfectosSilenciados(!GestorSonido.efectosSilenciados())));
        add(Box.createRigidArea(new Dimension(0, 36)));
        add(Estilo.botonTransparente("Volver", 18, onVolver));
        add(Box.createVerticalGlue());
    }

    // Botón que alterna un silencio y refleja el estado (ON/OFF) en su propio texto.
    private JButton crearToggle(String etiqueta, BooleanSupplier estaSilenciado, Runnable alternar) {
        JButton[] ref = new JButton[1];
        ref[0] = Estilo.botonTransparente(textoToggle(etiqueta, estaSilenciado.getAsBoolean()), 20, () -> {
            alternar.run();
            ref[0].setText(textoToggle(etiqueta, estaSilenciado.getAsBoolean()));
        });
        return ref[0];
    }

    // "Música: ON" / "Efectos: OFF", según el estado de silencio.
    private static String textoToggle(String etiqueta, boolean silenciado) {
        return etiqueta + ": " + (silenciado ? "OFF" : "ON");
    }
}
