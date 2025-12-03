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

    
    public static boolean esRutValido(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }

        // 1. Limpiar y Estandarizar: Quitar puntos, guion y pasar a Mayúsculas
        String rutLimpio = rut.replace(".", "").replace("-", "").trim().toUpperCase();

        // 2. Validar largo mínimo (ej: 1-9 son 2 caracteres)
        if (rutLimpio.length() < 2) {
            return false;
        }

        try {
            // 3. Separar Cuerpo y Dígito Verificador (DV)
            // El DV es siempre el último caracter
            char dvIngresado = rutLimpio.charAt(rutLimpio.length() - 1);
            
            String cuerpo = rutLimpio.substring(0, rutLimpio.length() - 1);

            // 4. Calcular DV esperado 
            int rutAux = Integer.parseInt(cuerpo);
            int suma = 0;
            int multiplicador = 2;

            // Recorremos el número de derecha a izquierda
            while (rutAux > 0) {
                int digito = rutAux % 10;
                suma += digito * multiplicador;
                rutAux /= 10; // Avanzar al siguiente dígito

                multiplicador++;
                if (multiplicador == 8) {
                    multiplicador = 2; // Reiniciar la serie 2,3,4,5,6,7
                }
            }

            // Cálculo del resto
            int resto = 11 - (suma % 11);

            char dvCalculado;
            if (resto == 11) {
                dvCalculado = '0';
            } else if (resto == 10) {
                dvCalculado = 'K';
            } else {
                dvCalculado = (char) (resto + '0'); // Convertir el número a caracter ASCII
            }

            // 5. Comparar
            return dvIngresado == dvCalculado;

        } catch (NumberFormatException e) {
            // Si el cuerpo contiene letras, no es un RUT válido
            return false;
        } catch (Exception e) {
            // Cualquier otro error inesperado
            return false;
        }
    }

}
