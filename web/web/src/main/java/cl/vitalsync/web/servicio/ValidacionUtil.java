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

    /**
     * Valida el RUT chileno usando el algoritmo Módulo 11. Soporta formatos con
     * puntos, guion, sin ellos, y DV 'K' o 'k'.
     */
    public static boolean esRutValido(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }

        // 1. Limpiar: Quitar puntos y guiones, y pasar a Mayúsculas (k -> K)
        String rutLimpio = rut.replace(".", "").replace("-", "").trim().toUpperCase();

        // 2. Validar largo mínimo (ej: 1-9 son 2 caracteres). Antes estaba en 7 y fallaba con RUTs de prueba.
        if (rutLimpio.length() < 2) {
            return false;
        }

        try {
            // 3. Separar Cuerpo y DV
            String cuerpo = rutLimpio.substring(0, rutLimpio.length() - 1);
            char dvIngresado = rutLimpio.charAt(rutLimpio.length() - 1);

            // 4. Calcular DV esperado (Algoritmo Módulo 11 Compacto)
            int rutAux = Integer.parseInt(cuerpo);
            int s = 1;
            int m = 0;

            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }

            // Si el resultado es 'K', el valor char será 75. Si es número, será su ASCII.
            char dvCalculado = (char) (s != 0 ? s + 47 : 75);

            // 5. Comparar
            return dvIngresado == dvCalculado;

        } catch (NumberFormatException e) {
            // Si el cuerpo contiene letras, no es un RUT válido
            return false;
        }
    }

}
