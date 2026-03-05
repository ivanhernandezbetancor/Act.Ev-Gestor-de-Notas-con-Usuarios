package gestornotasconUsuario.model;

// Clase modelo que representa un usuario con email y contraseña
public class Usuario {
    private String email;
    private String password;

    public Usuario(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // El usuario a texto con formato email;password
    @Override
    public String toString() {
        return email + ";" + password;
    }

    // El email para usarlo como nombre de carpeta en el sistema de archivos
    public String sanitizeEmail() {
        return email.replace("@", "_").replace(".", "_");
    }
}