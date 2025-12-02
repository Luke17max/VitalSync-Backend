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

    // --- ALGORITMO MÓDULO 11  ---
    public static boolean esRutValido(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }

        // 1. Limpiar y Estandarizar (Quitar puntos, guion y pasar a MAYÚSCULAS)
        String rutLimpio = rut.replace(".", "").replace("-", "").toUpperCase();

        // Validar largo mínimo (ej: 1111111-1 son 8 caracteres)
        if (rutLimpio.length() < 7) {
            return false;
        }

        try {
            // 2. Separar Cuerpo y Dígito Verificador (DV)
            String cuerpo = rutLimpio.substring(0, rutLimpio.length() - 1);
            char dvIngresado = rutLimpio.charAt(rutLimpio.length() - 1);

            // 3. Calcular DV esperado usando Módulo 11
            int rutAux = Integer.parseInt(cuerpo);
            int s = 0;
            int m = 2;

            while (rutAux > 0) {
                s += (rutAux % 10) * m;
                rutAux /= 10;
                m++;
                if (m > 7) {
                    m = 2;
                }
            }

            int resto = 11 - (s % 11);

            char dvCalculado;
            if (resto == 11) {
                dvCalculado = '0';
            } else if (resto == 10) {
                dvCalculado = 'K'; // Aquí aseguramos que 10 sea 'K'
            } else {
                dvCalculado = (char) (resto + '0'); // Convertir número a char (ej: 5 -> '5')
            }

            // 4. Comparar
            return dvIngresado == dvCalculado;

        } catch (NumberFormatException e) {
            return false; // Si el cuerpo no es numérico, el RUT es inválido
        }
    }

}
