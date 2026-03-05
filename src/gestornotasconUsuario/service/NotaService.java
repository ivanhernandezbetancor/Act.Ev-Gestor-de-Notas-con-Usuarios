package gestornotasconUsuario.service;

import gestornotasconUsuario.model.Nota;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Clase de servicio que gestiona las operaciones CRUD sobre las notas 
public class NotaService {
    private static final String NOTAS_FILENAME = "notas.txt";

    // Crea el archivo de notas si no existe 
    private void inicializarNotasFile(Path carpetaUsuario) {
        try {
            Path notasFile = carpetaUsuario.resolve(NOTAS_FILENAME);
            if (!Files.exists(notasFile)) {
                Files.createFile(notasFile);
            }
        } catch (IOException e) {
            System.out.println("Error al inicializar archivo de notas: " + e.getMessage());
        }
    }

    // Escribe una nueva nota al final del archivo 
    public boolean crearNota(Path carpetaUsuario, String titulo, String contenido) {
        if (titulo.isEmpty() || contenido.isEmpty()) {
            return false;
        }

        try {
            inicializarNotasFile(carpetaUsuario);
            Path notasFile = carpetaUsuario.resolve(NOTAS_FILENAME);
            Nota nota = new Nota(titulo, contenido);
            String linea = nota.toString() + "\n";

            // try-with-resources: cierra el escritor automáticamente 
            try (var writer = Files.newBufferedWriter(notasFile, StandardOpenOption.APPEND)) {
                writer.write(linea);
            }

            return true;
        } catch (IOException e) {
            System.out.println("Error al crear nota: " + e.getMessage());
            return false;
        }
    }

    // Lee todas las notas del archivo línea a línea 
    public List<Nota> listarNotas(Path carpetaUsuario) {
        List<Nota> notas = new ArrayList<>();
        Path notasFile = carpetaUsuario.resolve(NOTAS_FILENAME);

        if (!Files.exists(notasFile)) {
            inicializarNotasFile(carpetaUsuario);
            return notas;
        }

        // try-with-resources: cierra el lector automáticamente 
        try (var reader = Files.newBufferedReader(notasFile)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    Nota nota = Nota.fromString(linea);
                    if (nota != null) {
                        notas.add(nota);
                    }
                }
            }
        } catch (IOException error) {
            System.out.println("Error al listar notas: " + error.getMessage());
        }

        return notas;
    }

    // Devuelve la nota en la posición indicada (base 1)
    public Nota obtenerNota(Path carpetaUsuario, int numero) {
        List<Nota> notas = listarNotas(carpetaUsuario);
        if (numero > 0 && numero <= notas.size()) {
            return notas.get(numero - 1);
        }
        return null;
    }

    // Elimina la nota en la posición indicada y reescribe el archivo 
    public boolean eliminarNota(Path carpetaUsuario, int numero) {
        List<Nota> notas = listarNotas(carpetaUsuario);

        if (numero < 1 || numero > notas.size()) {
            return false;
        }

        notas.remove(numero - 1);

        try {
            Path notasFile = carpetaUsuario.resolve(NOTAS_FILENAME);

            // Trunca el archivo y lo reescribe con las notas restantes 
            try (var writer = Files.newBufferedWriter(notasFile, StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Nota nota : notas) {
                    writer.write(nota.toString() + "\n");
                }
            }

            return true;
        } catch (IOException e) {
            System.out.println("Error al eliminar nota: " + e.getMessage());
            return false;
        }
    }

    // Devuelve el número total de notas del usuario
    public int contarNotas(Path carpetaUsuario) {
        return listarNotas(carpetaUsuario).size();
    }

    // Busca notas cuyo título o contenido contengan el término indicado
    public List<Nota> buscarNotas(Path carpetaUsuario, String termino) {
        List<Nota> todasLasNotas = listarNotas(carpetaUsuario);
        List<Nota> resultados = new ArrayList<>();

        String terminoLower = termino.toLowerCase();

        for (Nota nota : todasLasNotas) {
            if (nota.getTitulo().toLowerCase().contains(terminoLower) ||
                    nota.getContenido().toLowerCase().contains(terminoLower)) {
                resultados.add(nota);
            }
        }

        return resultados;
    }

    // Edita la nota en la posición indicada y reescribe el archivo 
    public boolean editarNota(Path carpetaUsuario, int numero, String nuevoTitulo, String nuevoContenido) {
        List<Nota> notas = listarNotas(carpetaUsuario);

        if (numero < 1 || numero > notas.size()) {
            return false;
        }

        notas.set(numero - 1, new Nota(nuevoTitulo, nuevoContenido));

        try {
            Path notasFile = carpetaUsuario.resolve(NOTAS_FILENAME);

            // Trunca el archivo y lo reescribe con la nota modificada 
            try (var writer = Files.newBufferedWriter(notasFile, StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Nota nota : notas) {
                    writer.write(nota.toString() + "\n");
                }
            }

            return true;
        } catch (IOException e) {
            System.out.println("Error al editar nota: " + e.getMessage());
            return false;
        }
    }
}