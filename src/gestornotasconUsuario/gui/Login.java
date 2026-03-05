package gestornotasconUsuario.gui;

import gestornotasconUsuario.model.Usuario;
import gestornotasconUsuario.service.UsuarioService;
import gestornotasconUsuario.utils.Validador;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

// Pantalla de login y registro de usuarios 
public class Login extends JPanel {
    private Ventana ventana;
    private UsuarioService usuarioService;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;

    public Login(Ventana ventana) {
        this.ventana = ventana;
        this.usuarioService = new UsuarioService();

        // GridBagLayout para centrar los componentes en la pantalla 
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 240, 240));

        crearComponentes();
    }

    private void crearComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título de la aplicación
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("GESTOR DE NOTAS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, gbc);

        // Campo de email
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        add(emailField, gbc);

        // Campo de contraseña (JPasswordField oculta los caracteres)
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        // Botones de acción con ActionListener mediante lambda 
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);

        JPanel buttonPanel = new JPanel();

        loginButton = new JButton("Iniciar Sesion");
        loginButton.addActionListener(e -> handleLogin());
        buttonPanel.add(loginButton);

        registerButton = new JButton("Registrarse");
        registerButton.addActionListener(e -> handleRegister());
        buttonPanel.add(registerButton);

        add(buttonPanel, gbc);

        // Etiqueta de estado para mostrar mensajes al usuario
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 10, 10, 10);
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLUE);
        add(statusLabel, gbc);
    }

    // Valida los campos e intenta iniciar sesión
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            updateStatus("ERROR : Email y contraseña requeridos", Color.RED);
            return;
        }

        if (!Validador.esEmailValido(email)) {
            updateStatus("ERROR : Email no valido", Color.RED);
            return;
        }

        Usuario usuario = usuarioService.login(email, password);

        if (usuario != null) {
            updateStatus("OK : Sesion iniciada", Color.GREEN);
            ventana.mostrarNotas(usuario);
        } else {
            updateStatus("ERROR : Email o contraseña incorrectos", Color.RED);
        }
    }

    // Valida los campos e intenta registrar un nuevo usuario
    private void handleRegister() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            updateStatus("ERROR : Email y contraseña requeridos", Color.RED);
            return;
        }

        if (!Validador.esEmailValido(email)) {
            updateStatus("ERROR : Email no valido", Color.RED);
            return;
        }

        // La contraseña debe tener mínimo 4 caracteres
        if (password.length() < 4) {
            updateStatus("ERROR : Contraseña minimo 4 caracteres", Color.RED);
            return;
        }

        if (usuarioService.usuarioExiste(email)) {
            updateStatus("ERROR : Email ya registrado", Color.RED);
            return;
        }

        if (usuarioService.registrar(email, password)) {
            updateStatus("OK : Usuario registrado. Inicia sesion", Color.GREEN);
            limpiarCampos();
        } else {
            updateStatus("ERROR : Error al registrar", Color.RED);
        }
    }

    // Actualiza el mensaje de estado con el color indicado
    private void updateStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    // Limpia todos los campos del formulario
    public void limpiarCampos() {
        emailField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
        emailField.requestFocus();
    }
}