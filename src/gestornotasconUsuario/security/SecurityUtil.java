package gestornotasconUsuario.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Clase de seguridad para hashear y verificar contraseñas con SHA-256
public class SecurityUtil {

    // Genera un hash SHA-256 de la contraseña: nunca se guarda en texto plano
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());

            // Convierte el array de bytes a cadena hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }

    // Verifica si la contraseña introducida coincide con el hash guardado
    public static boolean verifyPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }
}