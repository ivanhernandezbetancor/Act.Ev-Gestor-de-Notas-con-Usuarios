package gestornotasconUsuario.service;

import gestornotasconUsuario.model.Usuario;
import gestornotasconUsuario.security.SecurityUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

// Clase de servicio que gestiona el registro, login y persistencia de usuarios 
public class UsuarioService {
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path USERS_FILE = DATA_DIR.resolve("users.txt");
    private static final Path USUARIOS_DIR = DATA_DIR.resolve("usuarios");

    public UsuarioService() {
        initDirectories();
    }

    // Crea la estructura de carpetas necesaria si no existe 
    private void initDirectories() {
        try {
            if (!Files.exists(DATA_DIR)) {
                Files.createDirectories(DATA_DIR);
            }
            if (!Files.exists(USUARIOS_DIR)) {
                Files.createDirectories(USUARIOS_DIR);
            }
            if (!Files.exists(USERS_FILE)) {
                Files.createFile(USERS_FILE);
            }
        } catch (IOException e) {
            System.out.println("Error al inicializar directorios: " + e.getMessage());
        }
    }

    // Registra un nuevo usuario guardando su email y contraseña hasheada 
    public boolean registrar(String email, String password) {
        if (usuarioExiste(email)) {
            return false;
        }

        if (email.isEmpty() || password.isEmpty()) {
            return false;
        }

        try {
            // La contraseña se guarda hasheada, nunca en texto plano
            String passwordHash = SecurityUtil.hashPassword(password);
            String linea = email + ";" + passwordHash + "\n";

            // try-with-resources: cierra el escritor automáticamente 
            try (var writer = Files.newBufferedWriter(USERS_FILE, StandardOpenOption.APPEND)) {
                writer.write(linea);
            }

            // Crea una carpeta personal para las notas del usuario
            Path carpetaUsuario = USUARIOS_DIR.resolve(sanitizeEmail(email));
            if (!Files.exists(carpetaUsuario)) {
                Files.createDirectory(carpetaUsuario);
            }

            return true;
        } catch (IOException error) {
            System.out.println("Error al registrar: " + error.getMessage());
            return false;
        }
    }

    // Comprueba si un email ya está registrado en el archivo de usuarios 
    public boolean usuarioExiste(String email) {
        try (var reader = Files.newBufferedReader(USERS_FILE)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linea.split(";");
                if (partes.length >= 1) {
                    String emailEnArchivo = partes[0];
                    if (emailEnArchivo.equals(email)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al verificar usuario: " + e.getMessage());
        }
        return false;
    }

    // Busca el usuario en el archivo y verifica la contraseña con el hash 
    public Usuario login(String email, String password) {
        try (var reader = Files.newBufferedReader(USERS_FILE)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linea.split(";");
                if (partes.length == 2) {
                    String emailEnArchivo = partes[0];
                    String passwordHash = partes[1];

                    if (emailEnArchivo.equals(email)) {

                        // Verifica comparando el hash, nunca el texto plano
                        
                        if (SecurityUtil.verifyPassword(password, passwordHash)) {
                            return new Usuario(email, password);
                        }
                        return null;
                    }
                }
            }
        } catch (IOException error) {
            System.out.println("Error al hacer login: " + error.getMessage());
        }
        return null;
    }

    // Devuelve la ruta de la carpeta personal del usuario
    public Path getCarpetaUsuario(String email) {
        return USUARIOS_DIR.resolve(sanitizeEmail(email));
    }

    // Sanitiza el email para usarlo como nombre de carpeta válido
    private String sanitizeEmail(String email) {
        return email.replace("@", "_").replace(".", "_");
    }
}