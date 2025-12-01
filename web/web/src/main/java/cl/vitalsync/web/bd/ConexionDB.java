
package cl.vitalsync.web.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String RUTA_WALLET = "C:/WALLET";
    private static final String NOMBRE_SERVICIO = "vitalsyncbd2025_high";
    private static final String USER = "ADMIN";
    private static final String PASS = "Proyecto_VitalSync2025";


    static Connection getConnection() throws SQLException {

        System.setProperty("oracle.net.tns_admin", RUTA_WALLET);

        String dbUrl = "jdbc:oracle:thin:@" + NOMBRE_SERVICIO;

        return DriverManager.getConnection(dbUrl, USER, PASS);
    }

}
