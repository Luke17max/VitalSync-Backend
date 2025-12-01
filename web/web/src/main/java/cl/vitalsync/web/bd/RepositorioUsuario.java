package cl.vitalsync.web.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import cl.vitalsync.web.modelo.Paciente;

public class RepositorioUsuario {

    public boolean guardar(String rut, String email, String password, String nombre, String telefono) {
        String sql = "INSERT INTO USUARIO (rut_paciente, email, password_hash, nombre_completo, telefono) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rut);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, nombre);
            pstmt.setString(5, telefono);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[Repo Usuario] Error al guardar: " + e.getMessage());
            return false;
        }
    }

    public Paciente validarLogin(String email, String password) {
        String sql = "SELECT * FROM USUARIO WHERE email = ? AND password_hash = ?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Si encontramos al usuario, devolvemos el objeto lleno
                return new Paciente(
                        rs.getString("rut_paciente"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("nombre_completo"),
                        rs.getString("telefono")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Si no existe o la clave está mal
    }

}
