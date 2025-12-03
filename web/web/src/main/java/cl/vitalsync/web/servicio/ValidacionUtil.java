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

    // --- NUEVO MÉTODO: LIMPIAR RUT ---
    // Transforma "12.345.678-k" a "12345678K"
    public static String limpiarRut(String rut) {
        if (rut == null) {
            return "";
        }
        return rut.replaceAll("[^0-9kK]", "").toUpperCase();
    }

    // --- VALIDACIÓN ROBUSTA (Acepta con o sin guion) ---
    public static boolean esRutValido(String rut) {
        // 1. Limpiamos el RUT antes de validar
        String rutLimpio = limpiarRut(rut);

        // 2. Validar largo mínimo (ej: 1-9 = 19 -> 2 caracteres)
        if (rutLimpio.length() < 2) {
            return false;
        }

        try {
            // 3. Separar Cuerpo y Dígito Verificador
            String cuerpo = rutLimpio.substring(0, rutLimpio.length() - 1);
            char dvIngresado = rutLimpio.charAt(rutLimpio.length() - 1);

            // 4. Calcular DV esperado
            int rutAux = Integer.parseInt(cuerpo);
            int suma = 0;
            int multiplicador = 2;

            for (int i = cuerpo.length() - 1; i >= 0; i--) {
                int digito = Character.getNumericValue(cuerpo.charAt(i));
                suma += digito * multiplicador;
                multiplicador++;
                if (multiplicador == 8) {
                    multiplicador = 2;
                }
            }

            int resto = 11 - (suma % 11);
            char dvCalculado;
            if (resto == 11) {
                dvCalculado = '0';
            } else if (resto == 10) {
                dvCalculado = 'K';
            } else {
                dvCalculado = (char) (resto + '0');
            }

            return dvIngresado == dvCalculado;

        } catch (Exception e) {
            return false;
        }
    }

}
