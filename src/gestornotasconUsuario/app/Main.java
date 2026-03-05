package gestornotasconUsuario.app;

import gestornotasconUsuario.gui.Ventana;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {

        // Aplicar Nimbus LookAndFeel para mejorar la apariencia visual 
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.out.println("No se pudo aplicar Nimbus: " + e.getMessage());
        }

        // Lanzar la interfaz gráfica en el hilo de eventos de Swing 
        SwingUtilities.invokeLater(() -> {
            new Ventana();
        });
    }
}