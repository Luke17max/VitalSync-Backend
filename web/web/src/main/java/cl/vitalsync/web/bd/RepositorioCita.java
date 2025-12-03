
package cl.vitalsync.web.bd;

import cl.vitalsync.web.modelo.HoraMedica;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class RepositorioCita {
     // 1. Buscar Disponibles
    public List<HoraMedica> buscarDisponibles(String fechaInicio, String fechaFin) {
        List<HoraMedica> horas = new ArrayList<>();
        String sql = "SELECT c.id_cita, m.nombre_completo, m.especialidad, c.fecha_hora " +
                     "FROM CITA c JOIN MEDICO m ON c.rut_medico = m.rut_medico " +
                     "WHERE c.rut_paciente IS NULL AND c.estado = 'DISPONIBLE' " +
                     "AND c.fecha_hora BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD') + 0.99999";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("fecha_hora");
                horas.add(new HoraMedica(
                    rs.getInt("id_cita"),
                    rs.getString("nombre_completo"),
                    rs.getString("especialidad"),
                    ts != null ? ts.toLocalDateTime() : null
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return horas;
    }

    // 2. Asignar Paciente
    public boolean asignarPaciente(int idCita, String rutPaciente) {
        String sql = "UPDATE CITA SET rut_paciente = ?, estado = 'RESERVADA' WHERE id_cita = ? AND rut_paciente IS NULL";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rutPaciente);
            pstmt.setInt(2, idCita);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // 3. Buscar por Paciente
    public List<HoraMedica> buscarPorPaciente(String rutPaciente) {
        List<HoraMedica> misCitas = new ArrayList<>();
        String sql = "SELECT c.id_cita, m.nombre_completo AS medico, m.especialidad, c.fecha_hora, u.nombre_completo AS paciente, c.estado " +
                     "FROM CITA c JOIN MEDICO m ON c.rut_medico = m.rut_medico " +
                     "JOIN USUARIO u ON c.rut_paciente = u.rut_paciente " +
                     "WHERE c.rut_paciente = ? AND c.estado = 'RESERVADA' ORDER BY c.fecha_hora ASC";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rutPaciente);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("fecha_hora");
                misCitas.add(new HoraMedica(
                    rs.getInt("id_cita"),
                    rs.getString("medico"),
                    rs.getString("especialidad"),
                    ts != null ? ts.toLocalDateTime() : null,
                    rs.getString("paciente"),
                    rs.getString("estado")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return misCitas;
    }

    // 4. Obtener por ID
    public HoraMedica obtenerPorId(int idCita) {
        String sql = "SELECT c.id_cita, m.nombre_completo, m.especialidad, c.fecha_hora FROM CITA c " +
                     "JOIN MEDICO m ON c.rut_medico = m.rut_medico WHERE c.id_cita = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCita);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Timestamp ts = rs.getTimestamp("fecha_hora");
                return new HoraMedica(
                    rs.getInt("id_cita"),
                    rs.getString("nombre_completo"),
                    rs.getString("especialidad"),
                    ts != null ? ts.toLocalDateTime() : null
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // 5. Liberar Cita
    public boolean liberarCita(int idCita) {
        String sql = "UPDATE CITA SET rut_paciente = NULL, estado = 'DISPONIBLE' WHERE id_cita = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCita);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // 6. Agenda Médico 
    public List<HoraMedica> buscarAgendaMedico(String rutMedico, String fechaInicio, String fechaFin) {
        List<HoraMedica> agenda = new ArrayList<>();
        String sql = "SELECT c.id_cita, m.nombre_completo AS medico, m.especialidad, c.fecha_hora, u.nombre_completo AS paciente, c.estado " +
                     "FROM CITA c JOIN MEDICO m ON c.rut_medico = m.rut_medico " +
                     "JOIN USUARIO u ON c.rut_paciente = u.rut_paciente " +
                     "WHERE c.rut_medico = ? AND c.estado = 'RESERVADA' " +
                     "AND c.fecha_hora BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD') + 0.99999 ORDER BY c.fecha_hora ASC";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rutMedico);
            pstmt.setString(2, fechaInicio);
            pstmt.setString(3, fechaFin);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("fecha_hora");
                agenda.add(new HoraMedica(
                    rs.getInt("id_cita"),
                    rs.getString("medico"),
                    rs.getString("especialidad"),
                    ts != null ? ts.toLocalDateTime() : null,
                    rs.getString("paciente"),
                    rs.getString("estado")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return agenda;
    }
}
