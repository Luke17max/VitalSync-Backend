
package cl.vitalsync.web.servicio;

import java.util.regex.Pattern;

public class ValidacionUtil {
    // Solo letras y espacios, mín 3 caracteres
    private static final String REGEX_NOMBRE = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,50}$";
    
    // Solo números, entre 8 y 12 dígitos (acepta +569...)
    private static final String REGEX_TELEFONO = "^\\+?[0-9]{8,12}$";
    
    // Formato Email simple
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

    // ALGORITMO MÓDULO 11 (RUT CHILENO)
    public static boolean esRutValido(String rut) {
        if (rut == null || rut.trim().isEmpty()) return false;
        
        // Limpiar puntos y guión
        String rutLimpio = rut.replace(".", "").replace("-", "").toUpperCase();
        
        // Validar largo mínimo
        if (rutLimpio.length() < 7 || rutLimpio.length() > 9) return false;

        try {
            // Separar cuerpo y dígito verificador
            String cuerpo = rutLimpio.substring(0, rutLimpio.length() - 1);
            char dvIngresado = rutLimpio.charAt(rutLimpio.length() - 1);

            int rutAux = Integer.parseInt(cuerpo);
            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            char dvCalculado = (char) (s != 0 ? s + 47 : 75);

            return dvIngresado == dvCalculado;
        } catch (Exception e) {
            return false; // Si falla al parsear números, es inválido
        }
    }
    
    
    
}
