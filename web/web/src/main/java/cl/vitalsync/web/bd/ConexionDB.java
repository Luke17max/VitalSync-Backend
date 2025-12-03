package cl.vitalsync.web.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    
    private static final String NOMBRE_SERVICIO = "vitalsyncbd2025_high";
    private static final String USER = "ADMIN";
    private static final String PASS = "Proyecto_VitalSync2025";

    public static Connection getConnection() throws SQLException {

        // 2. Detección Inteligente de Ruta (Local vs Nube)
        String walletPath;
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            
           
            walletPath = "C:\\WALLET";
        } else {
            // NUBE (RAILWAY / LINUX)
            // Maven guarda los recursos aquí al compilar en la nube
            walletPath = "/app/target/classes/wallet";
        }

        // 3. Construcción de la URL JDBC 
        // Estructura: jdbc:oracle:thin:@ALIAS?TNS_ADMIN=RUTA
        String dbUrl = "jdbc:oracle:thin:@" + NOMBRE_SERVICIO + "?TNS_ADMIN=" + walletPath;

        // 4. Conectar
        try {
            return DriverManager.getConnection(dbUrl, USER, PASS);
        } catch (SQLException e) {
            System.err.println("❌ Error crítico de conexión.");
            System.err.println("URL intentada: " + dbUrl);
            System.err.println("Ruta Wallet: " + walletPath);
            throw e;
        }
    }
}
