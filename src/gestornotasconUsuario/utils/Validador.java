package gestornotasconUsuario.utils;

// Clase de utilidades para validar los datos introducidos por el usuario
public class Validador {

    // Comprueba que el email contenga @ y punto
    public static boolean esEmailValido(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.contains("@") && email.contains(".");
    }

    // Comprueba que la contraseña no esté vacía
    public static boolean esPasswordValida(String password) {
        return password != null && !password.isEmpty();
    }

    // Comprueba que el título no esté vacío
    public static boolean esTituloValido(String titulo) {
        return titulo != null && !titulo.isEmpty();
    }

    // Comprueba que el contenido no esté vacío
    public static boolean esContenidoValido(String contenido) {
        return contenido != null && !contenido.isEmpty();
    }

    // Comprueba que el input sea un número entero válido
    public static boolean esNumeroValido(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}