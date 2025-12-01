
package cl.vitalsync.web.modelo;


public class Medico {
    private String rutMedico;
    private String nombreCompleto;
    private String especialidad;

    // Constructor vacío 
    public Medico() {
    }

    // Constructor con datos
    public Medico(String rutMedico, String nombreCompleto, String especialidad) {
        this.rutMedico = rutMedico;
        this.nombreCompleto = nombreCompleto;
        this.especialidad = especialidad;
    }

    // Getters y Setters

    public String getRutMedico() {
        return rutMedico;
    }

    public void setRutMedico(String rutMedico) {
        this.rutMedico = rutMedico;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
    

    @Override
    public String toString() {
        return nombreCompleto + " - " + especialidad;
    }
    
}
