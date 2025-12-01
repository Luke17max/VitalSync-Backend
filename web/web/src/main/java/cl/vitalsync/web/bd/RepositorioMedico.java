
package cl.vitalsync.web.bd;

import cl.vitalsync.web.modelo.Medico;
import java.sql.*;

public class RepositorioMedico {
    public Medico validarLogin(String rut, String password) {
        // Verifica RUT y Contraseña en la tabla MEDICO
        String sql = "SELECT * FROM MEDICO WHERE rut_medico = ? AND password_hash = ?";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, rut);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Medico(
                        rs.getString("rut_medico"),
                        rs.getString("nombre_completo"),
                        rs.getString("especialidad")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
}
