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
     * Valida el RUT chileno recorriendo la cadena de texto (Más seguro). Acepta
     * cualquier formato: 12.345.678-5, 123456785, 12345678-K
     */
    public static boolean esRutValido(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }

        // 1. Limpieza Agresiva: Elimina cualquier cosa que no sea número o K
        String rutLimpio = rut.replaceAll("[^0-9kK]", "").toUpperCase();

        // 2. Validar largo mínimo (ej: 1-9 = 19 -> 2 caracteres)
        if (rutLimpio.length() < 2) {
            return false;
        }

        try {
            // 3. Separar Cuerpo y Dígito Verificador
            String cuerpo = rutLimpio.substring(0, rutLimpio.length() - 1);
            char dvIngresado = rutLimpio.charAt(rutLimpio.length() - 1);

            // 4. Validar que el cuerpo sean solo números (por si acaso)
            if (!cuerpo.matches("[0-9]+")) {
                return false;
            }

            // 5. Calcular DV esperado recorriendo el String de atrás hacia adelante
            int suma = 0;
            int multiplicador = 2;

            for (int i = cuerpo.length() - 1; i >= 0; i--) {
                // Obtener el valor numérico del caracter en la posición i
                int digito = Character.getNumericValue(cuerpo.charAt(i));

                suma += digito * multiplicador;
                multiplicador++;

                if (multiplicador == 8) {
                    multiplicador = 2; // Reiniciar la serie 2,3,4,5,6,7
                }
            }

            // 6. Calcular el resto
            int resto = 11 - (suma % 11);

            char dvCalculado;
            if (resto == 11) {
                dvCalculado = '0';
            } else if (resto == 10) {
                dvCalculado = 'K';
            } else {
                // Convertir número a char (ej: 5 -> '5')
                dvCalculado = (char) (resto + '0');
            }

            // 7. Comparar
            return dvIngresado == dvCalculado;

        } catch (Exception e) {
            System.err.println("Error validando RUT: " + e.getMessage());
            return false;
        }
    }

}
