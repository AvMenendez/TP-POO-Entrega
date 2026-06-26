package Views;

import Model.GestorSonido;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;

// Pide el nombre del jugador antes de comenzar la partida.
public class PanelNombre extends PanelFondo {

    private final JTextField campoNombre = new JTextField();

    // Arma la pantalla con el campo de nombre y los botones de comenzar/volver.
    public PanelNombre(Consumer<String> onComenzar, Runnable onVolver) {
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // margen lateral propio

        campoNombre.setMaximumSize(new Dimension(280, 38));
        campoNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        campoNombre.setFont(new Font("SansSerif", Font.PLAIN, 20));
        campoNombre.setHorizontalAlignment(JTextField.CENTER);

        // Confirmar con el botón o con Enter dentro del campo.
        Runnable confirmar = () -> {
            // La música del menú la corta nuevaPartida() al arrancar la música de partida.
            GestorSonido.reproducir("/Sounds/iniciarJuego.wav");
            onComenzar.accept(nombreIngresado());
        };
        campoNombre.addActionListener(e -> confirmar.run());

        add(Box.createVerticalGlue());
        add(Estilo.titulo("¿Listo para marcar un nuevo récord?", 26, Estilo.ACENTO));
        add(Box.createRigidArea(new Dimension(0, 28)));
        add(Estilo.etiqueta("Ingresá tu nombre:", 18, Estilo.TEXTO_TENUE));
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(campoNombre);
        add(Box.createRigidArea(new Dimension(0, 28)));
        add(Estilo.botonTransparente("Despegar!", 20, confirmar));
        add(Box.createRigidArea(new Dimension(0, 12)));
        add(Estilo.botonTransparente("Volver", 16, onVolver));
        add(Box.createVerticalGlue());
    }

    // Devuelve el nombre escrito, o "Anónimo" si quedó vacío.
    private String nombreIngresado() {
        String nombre = campoNombre.getText().trim();
        return nombre.isEmpty() ? "Anónimo" : nombre;
    }

    // Al mostrarse la pantalla, pone el cursor directamente en el campo de nombre.
    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(campoNombre::requestFocusInWindow);
    }
}
