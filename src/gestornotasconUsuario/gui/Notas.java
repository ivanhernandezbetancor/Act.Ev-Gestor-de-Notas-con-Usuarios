package gestornotasconUsuario.gui;

import gestornotasconUsuario.model.Nota;
import gestornotasconUsuario.model.Usuario;
import gestornotasconUsuario.service.NotaService;
import gestornotasconUsuario.service.UsuarioService;
import gestornotasconUsuario.utils.Validador;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.nio.file.Path;
import java.util.List;

// Pantalla principal del gestor de notas 
public class Notas extends JPanel {
    private Ventana ventana;
    private UsuarioService usuarioService;
    private NotaService notaService;
    private Usuario usuarioActual;

    private JTextField titleField;
    private JTextField searchField;
    private JTextArea contentArea;
    private DefaultListModel<String> listModel;
    private JList<String> notasList;
    private JButton createButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton deleteAllButton;
    private JButton logoutButton;
    private JLabel statusLabel;
    private JLabel userLabel;

    // Índice de la nota seleccionada en la lista (-1 = ninguna)
    private int notaSeleccionada = -1;

    public Notas(Ventana ventana) {
        this.ventana = ventana;
        this.usuarioService = new UsuarioService();
        this.notaService = new NotaService();

        // BorderLayout como layout principal - UT5.2
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        crearComponentes();
    }

    private void crearComponentes() {
        // Panel superior: usuario conectado + botón cerrar sesión
        JPanel topPanel = new JPanel(new BorderLayout());
        userLabel = new JLabel("Conectado como : ");
        userLabel.setFont(new Font("Arial", Font.BOLD, 12));
        topPanel.add(userLabel, BorderLayout.WEST);

        logoutButton = new JButton("Cerrar Sesion");
        logoutButton.addActionListener(e -> logout());
        topPanel.add(logoutButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // JSplitPane divide la pantalla en lista (izq) y editor (der) 
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(crearPanelLista());
        splitPane.setRightComponent(crearPanelEdicion());
        splitPane.setDividerLocation(200);
        add(splitPane, BorderLayout.CENTER);

        // Panel inferior: mensajes de estado
        JPanel bottomPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Listo");
        statusLabel.setForeground(Color.BLUE);
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel crearPanelLista() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("MIS NOTAS"));

        // JList con DefaultListModel para mostrar los títulos 
        listModel = new DefaultListModel<>();
        notasList = new JList<>(listModel);
        notasList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ListSelectionListener: carga la nota al seleccionarla 
        notasList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                notaSeleccionada = notasList.getSelectedIndex();
                if (notaSeleccionada >= 0) {
                    cargarNotaEnEdicion(notaSeleccionada);
                }
            }
        });

        panel.add(new JScrollPane(notasList), BorderLayout.CENTER);

        // Panel de búsqueda con DocumentListener para filtrar en tiempo real
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.add(new JLabel("Buscar:"), BorderLayout.WEST);

        searchField = new JTextField();
        // DocumentListener: reacciona a cada cambio en el campo de búsqueda
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                buscarNotas();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscarNotas();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                buscarNotas();
            }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);

        panel.add(searchPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelEdicion() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("EDITAR NOTA"));

        // GridLayout para organizar los campos en filas 
        JPanel inputPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        // Campo de título
        JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
        titlePanel.add(new JLabel("Titulo :"), BorderLayout.WEST);
        titleField = new JTextField();
        titlePanel.add(titleField, BorderLayout.CENTER);
        inputPanel.add(titlePanel);

        // Área de contenido con scroll
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.add(new JLabel("Contenido:"), BorderLayout.NORTH);
        contentArea = new JTextArea(10, 30);
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        inputPanel.add(contentPanel);

        // Botones CRUD con ActionListener mediante lambda 
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 5));

        createButton = new JButton("Crear");
        createButton.addActionListener(e -> crearNota());
        buttonPanel.add(createButton);

        editButton = new JButton("Editar");
        editButton.addActionListener(e -> editarNota());
        buttonPanel.add(editButton);

        deleteButton = new JButton("Eliminar");
        deleteButton.addActionListener(e -> eliminarNota());
        buttonPanel.add(deleteButton);

        clearButton = new JButton("Limpiar");
        clearButton.addActionListener(e -> limpiarCampos());
        buttonPanel.add(clearButton);

        inputPanel.add(buttonPanel);
        panel.add(inputPanel, BorderLayout.CENTER);

        // Botón para borrar todas las notas con advertencia
        JPanel deleteAllPanel = new JPanel();
        deleteAllButton = new JButton("Borrar Todo");
        deleteAllButton.setBackground(new Color(255, 200, 200));
        deleteAllButton.addActionListener(e -> eliminarTodas());
        deleteAllPanel.add(deleteAllButton);

        panel.add(deleteAllPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Crea una nueva nota con los datos del formulario
    private void crearNota() {
        String titulo = titleField.getText().trim();
        String contenido = contentArea.getText().trim();

        if (!Validador.esTituloValido(titulo)) {
            updateStatus("ERROR : Titulo no puede estar vacio", Color.RED);
            return;
        }

        if (!Validador.esContenidoValido(contenido)) {
            updateStatus("ERROR : Contenido no puede estar vacio", Color.RED);
            return;
        }

        Path carpetaUsuario = usuarioService.getCarpetaUsuario(usuarioActual.getEmail());

        if (notaService.crearNota(carpetaUsuario, titulo, contenido)) {
            updateStatus("OK : Nota creada correctamente", Color.GREEN);
            cargarNotas();
            limpiarCampos();
        } else {
            updateStatus("ERROR : Error al crear la nota", Color.RED);
        }
    }

    // Guarda los cambios en la nota seleccionada
    private void editarNota() {
        if (notaSeleccionada < 0) {
            updateStatus("ERROR : Selecciona una nota para editar", Color.RED);
            return;
        }

        String nuevoTitulo = titleField.getText().trim();
        String nuevoContenido = contentArea.getText().trim();

        if (!Validador.esTituloValido(nuevoTitulo)) {
            updateStatus("ERROR : Titulo no puede estar vacio", Color.RED);
            return;
        }

        if (!Validador.esContenidoValido(nuevoContenido)) {
            updateStatus("ERROR : Contenido no puede estar vacio", Color.RED);
            return;
        }

        Path carpetaUsuario = usuarioService.getCarpetaUsuario(usuarioActual.getEmail());

        if (notaService.editarNota(carpetaUsuario, notaSeleccionada + 1, nuevoTitulo, nuevoContenido)) {
            updateStatus("OK: Nota editada correctamente", Color.GREEN);
            cargarNotas();
            limpiarCampos();
        } else {
            updateStatus("ERROR : Error al editar la nota", Color.RED);
        }
    }

    // Elimina la nota seleccionada tras confirmación con JOptionPane 
    private void eliminarNota() {
        if (notaSeleccionada < 0) {
            updateStatus("ERROR : Selecciona una nota para eliminar", Color.RED);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Eliminar esta nota?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Path carpetaUsuario = usuarioService.getCarpetaUsuario(usuarioActual.getEmail());

            if (notaService.eliminarNota(carpetaUsuario, notaSeleccionada + 1)) {
                updateStatus("OK : Nota eliminada correctamente", Color.GREEN);
                cargarNotas();
                limpiarCampos();
            } else {
                updateStatus("ERROR : Error al eliminar la nota", Color.RED);
            }
        }
    }

    // Elimina todas las notas tras doble confirmación con JOptionPane 
    private void eliminarTodas() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "ADVERTENCIA : Eliminar TODAS las notas?\nEsta accion NO se puede deshacer.",
                "ADVERTENCIA",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Path carpetaUsuario = usuarioService.getCarpetaUsuario(usuarioActual.getEmail());
            List<Nota> notas = notaService.listarNotas(carpetaUsuario);

            for (int i = notas.size(); i >= 1; i--) {
                notaService.eliminarNota(carpetaUsuario, i);
            }

            updateStatus("OK : Todas las notas eliminadas", Color.GREEN);
            cargarNotas();
            limpiarCampos();
        }
    }

    // Filtra la lista en tiempo real según el texto del campo búsqueda - UT5.2 pág.
    // 34
    private void buscarNotas() {
        String termino = searchField.getText().trim();

        if (termino.isEmpty()) {
            cargarNotas();
            return;
        }

        Path carpetaUsuario = usuarioService.getCarpetaUsuario(usuarioActual.getEmail());
        List<Nota> resultados = notaService.buscarNotas(carpetaUsuario, termino);

        listModel.clear();
        for (Nota nota : resultados) {
            listModel.addElement(nota.getTitulo());
        }

        updateStatus("INFO : " + resultados.size() + " resultado(s)", Color.BLUE);
    }

    // Carga los datos de la nota seleccionada en los campos del formulario
    private void cargarNotaEnEdicion(int indice) {
        Path carpetaUsuario = usuarioService.getCarpetaUsuario(usuarioActual.getEmail());
        Nota nota = notaService.obtenerNota(carpetaUsuario, indice + 1);

        if (nota != null) {
            titleField.setText(nota.getTitulo());
            contentArea.setText(nota.getContenido());
        }
    }

    // Carga todas las notas del usuario en la JList
    public void cargarNotas() {
        listModel.clear();
        notaSeleccionada = -1;

        Path carpetaUsuario = usuarioService.getCarpetaUsuario(usuarioActual.getEmail());
        List<Nota> notas = notaService.listarNotas(carpetaUsuario);

        for (Nota nota : notas) {
            listModel.addElement(nota.getTitulo());
        }

        if (notas.isEmpty()) {
            updateStatus("INFO : No tienes notas aun", Color.BLUE);
        } else {
            updateStatus("OK : " + notas.size() + " nota(s) cargada(s)", Color.BLUE);
        }
    }

    // Limpia los campos del formulario sin borrar notas
    private void limpiarCampos() {
        titleField.setText("");
        contentArea.setText("");
        notaSeleccionada = -1;
        notasList.clearSelection();
        searchField.setText("");
        updateStatus("Campos limpiados", Color.BLUE);
    }

    // Cierra la sesión y vuelve a la pantalla de login tras confirmación
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Cerrar sesion?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            limpiarCampos();
            ventana.mostrarLogin();
        }
    }

    // Establece el usuario actual y actualiza la etiqueta
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        userLabel.setText("Conectado como: " + usuario.getEmail());
    }

    // Muestra un mensaje de estado con el color indicado
    private void updateStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
}