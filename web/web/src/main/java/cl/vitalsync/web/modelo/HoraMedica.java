
package cl.vitalsync.web.modelo;

import java.time.LocalDateTime;


public class HoraMedica {
     private int idCita;           
    private String nombreMedico;
    private String especialidad;
    private LocalDateTime fechaHora;
    private String nombrePaciente; // Nombre del paciente para mostrar
    private String estado;

    // Constructor simple
    public HoraMedica(int idCita, String nombreMedico, String especialidad, LocalDateTime fechaHora) {
        this.idCita = idCita;
        this.nombreMedico = nombreMedico;
        this.especialidad = especialidad;
        this.fechaHora = fechaHora;
    }

    // Constructor completo
    public HoraMedica(int idCita, String nombreMedico, String especialidad, LocalDateTime fechaHora, String nombrePaciente, String estado) {
        this.idCita = idCita;
        this.nombreMedico = nombreMedico;
        this.especialidad = especialidad;
        this.fechaHora = fechaHora;
        this.nombrePaciente = nombrePaciente;
        this.estado = estado;
    }

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    

    @Override
    public String toString() {
        String infoExtra = (nombrePaciente != null) ? " | Paciente: " + nombrePaciente : "";
        return String.format("ID: %d | %s (%s) | %s%s", idCita, nombreMedico, especialidad, fechaHora, infoExtra);
    }
}
