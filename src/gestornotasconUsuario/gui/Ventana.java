package gestornotasconUsuario.gui;

import gestornotasconUsuario.model.Usuario;
import javax.swing.JFrame;

public class Ventana extends JFrame {
    private Login loginPanel;
    private Notas notasPanel;

    public Ventana() {
        // Configuración de la ventana principal
        setTitle(" Gestor de Notas ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setResizable(true);

        // Inicialización de los paneles (vistas)
        loginPanel = new Login(this);
        notasPanel = new Notas(this);

        // Se establece el login como vista inicial
        setContentPane(loginPanel);
        setVisible(true);
    }

    // Cambia la vista al panel de notas tras el login
    public void mostrarNotas(Usuario usuario) {
        notasPanel.setUsuarioActual(usuario);
        notasPanel.cargarNotas();
        setContentPane(notasPanel);
        revalidate();
        repaint();
    }

    // Cambia la vista de vuelta al login (cerrar sesión)
    public void mostrarLogin() {
        loginPanel.limpiarCampos();
        setContentPane(loginPanel);
        revalidate();
        repaint();
    }
}