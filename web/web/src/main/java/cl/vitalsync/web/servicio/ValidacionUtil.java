package cl.vitalsync.web.servicio;

import java.util.regex.Pattern;

public class ValidacionUtil {

    private static final String REGEX_NOMBRE = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,50}$";
    private static final String REGEX_TELEFONO = "^\\+?[0-9]{8,12}$";
    private static final String REGEX_EMAIL = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    public static boolean esNombreValido(String nombre) {
        return nombre != null && Pattern.matches(REGEX_NOMBRE, nombre);
    }

    public static boolean esTelefonoValido(String fono) {
        return fono != null && Pattern.matches(REGEX_TELEFONO, fono);
    }

    public static boolean esEmailValido(String email) {
        return email != null && Pattern.matches(REGEX_EMAIL, email);
    }

    
    // Mantenemos el limpiador para guardar datos ordenados
    public static String limpiarRut(String rut) {
        if (rut == null) {
            return "";
        }
        return rut.replace(".", "").replace("-", "").trim().toUpperCase();
    }

    /**
     * Validación de RUT RELAJADA. Solo verifica que no esté vacío. Acepta
     * cualquier número o formato.
     */
    public static boolean esRutValido(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }

        // Ya no hacemos cálculo matemático (Módulo 11). 
        // Si tiene más de 2 caracteres, lo damos por bueno.
        return rut.length() > 2;
    }

}
