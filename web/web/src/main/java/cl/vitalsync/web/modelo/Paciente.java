
package cl.vitalsync.web.modelo;

import java.sql.Timestamp;


public class Paciente {
    private String rutPaciente;
    private String email;
    private String password; 
    private String nombreCompleto;
    private String telefono;
    private Timestamp fechaRegistro;

    public Paciente() {
    }

    // 2. Constructor de 4 Parámetros 
    public Paciente(String rutPaciente, String email, String password, String nombreCompleto) {
        this.rutPaciente = rutPaciente;
        this.email = email;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
    }

    // 3. Constructor de 5 Parámetros 
    
    public Paciente(String rutPaciente, String email, String password, String nombreCompleto, String telefono) {
        this.rutPaciente = rutPaciente;
        this.email = email;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
    }

    // 4. Constructor Completo con Fecha 
    public Paciente(String rutPaciente, String email, String password, String nombreCompleto, String telefono, Timestamp fechaRegistro) {
        this.rutPaciente = rutPaciente;
        this.email = email;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y Setters

    public String getRutPaciente() {
        return rutPaciente;
    }

    public void setRutPaciente(String rutPaciente) {
        this.rutPaciente = rutPaciente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    

    @Override
    public String toString() {
        return "Paciente: " + nombreCompleto + " [" + rutPaciente + "]";
    }
}
