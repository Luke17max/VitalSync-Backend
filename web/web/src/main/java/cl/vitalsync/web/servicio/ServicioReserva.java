
package cl.vitalsync.web.servicio;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import cl.vitalsync.web.modelo.HoraMedica;
import cl.vitalsync.web.bd.RepositorioCita;
import cl.vitalsync.web.bd.RepositorioUsuario;
import cl.vitalsync.web.modelo.Paciente;
import cl.vitalsync.web.modelo.Medico;
import cl.vitalsync.web.bd.RepositorioMedico;


public class ServicioReserva {
    private final RepositorioUsuario usuarioRepo = new RepositorioUsuario();
    private final RepositorioCita citaRepo = new RepositorioCita();
    private final RepositorioMedico medicoRepo = new RepositorioMedico();

      // LOGIN
    public Paciente iniciarSesion(String email, String password) {
        return usuarioRepo.validarLogin(email, password);
    }
     // --- LOGIN MÉDICO  ---
    public Medico iniciarSesionMedico(String rut, String password) {
        return medicoRepo.validarLogin(rut, password);
    }

    // REGISTRO
    public boolean registrarNuevoPaciente(String rut, String email, String password, String nombre, String telefono) {
        if (password == null || password.length() < 4) return false;
        return usuarioRepo.guardar(rut, email, password, nombre, telefono);
    }

    // 1. Obtener horas disponibles
    public List<HoraMedica> obtenerHorarioSemanal(String fechaInicio, String fechaFin) {
        return citaRepo.buscarDisponibles(fechaInicio, fechaFin);
    }

    // 2. Agendar hora
    public boolean agendarHora(int idCita, String rutPaciente) {
        return citaRepo.asignarPaciente(idCita, rutPaciente);
    }

    // 3. Ver mis citas (Paciente)
    public List<HoraMedica> obtenerCitasDePaciente(String rutPaciente) {
        return citaRepo.buscarPorPaciente(rutPaciente);
    }

    // 4. Cancelar Cita (Regla de Negocio: > 24 horas)
    public String cancelarCita(int idCita) {
        HoraMedica cita = citaRepo.obtenerPorId(idCita);
        
        if (cita == null) return "Error: La cita no existe.";

        // Validación de tiempo: calcular diferencia en horas
        long horasDiferencia = Duration.between(LocalDateTime.now(), cita.getFechaHora()).toHours();

        if (horasDiferencia < 24) {
            return "Fallo: No se puede cancelar con menos de 24 horas de antelación.";
        }

        boolean exito = citaRepo.liberarCita(idCita);
        return exito ? "Éxito: Cita cancelada." : "Error al cancelar.";
    }
    
    // 5. Ver Agenda Médico (¡ESTE ES EL MÉTODO QUE TE FALTABA!)
    public List<HoraMedica> obtenerAgendaMedico(String rutMedico, String fechaInicio, String fechaFin) {
        return citaRepo.buscarAgendaMedico(rutMedico, fechaInicio, fechaFin);
    }
}
