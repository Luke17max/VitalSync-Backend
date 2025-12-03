package cl.vitalsync.web.controlador;

import cl.vitalsync.web.servicio.ValidacionUtil;
import cl.vitalsync.web.modelo.HoraMedica;
import cl.vitalsync.web.modelo.Paciente;
import cl.vitalsync.web.modelo.Medico;
import cl.vitalsync.web.servicio.ServicioReserva;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class VitalSyncControlador {
    // Instanciamos tu servicio (que ya tiene la lógica de 24h y conexión a BD)

    private final ServicioReserva servicio = new ServicioReserva();

     // --- INICIO ---
    @GetMapping("/")
    public String inicio(HttpSession session, Model model) {
        Paciente paciente = (Paciente) session.getAttribute("usuarioLogueado");
        if (paciente == null) return "login"; 

        try {
            // Usar RUT limpio del usuario en sesión
            String rutLimpio = ValidacionUtil.limpiarRut(paciente.getRutPaciente());
            
            List<HoraMedica> disponibles = servicio.obtenerHorarioSemanal("2025-12-01", "2025-12-31");
            List<HoraMedica> misCitas = servicio.obtenerCitasDePaciente(rutLimpio);

            model.addAttribute("usuario", paciente);
            model.addAttribute("disponibles", disponibles);
            model.addAttribute("misCitas", misCitas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "index";
    }

    // --- LOGIN PACIENTE ---
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, RedirectAttributes flash) {
        if (!ValidacionUtil.esEmailValido(email)) {
            flash.addFlashAttribute("error", "Formato de correo inválido.");
            return "redirect:/";
        }
        
        Paciente paciente = servicio.iniciarSesion(email, password);
        
        if (paciente != null) {
            session.setAttribute("usuarioLogueado", paciente);
            return "redirect:/";
        } else {
            flash.addFlashAttribute("error", "Credenciales incorrectas.");
            return "redirect:/"; 
        }
    }

    // --- LOGIN MÉDICO ---
    @PostMapping("/medico-login")
    public String loginMedico(@RequestParam String rut, @RequestParam String password, HttpSession session, RedirectAttributes flash) {
        // 1. Limpiar RUT antes de validar
        String rutLimpio = ValidacionUtil.limpiarRut(rut);

        if (!ValidacionUtil.esRutValido(rutLimpio)) {
            flash.addFlashAttribute("error", "RUT inválido.");
            return "redirect:/medico-login";
        }

        // 2. Usar RUT limpio para buscar en BD
        Medico medico = servicio.iniciarSesionMedico(rutLimpio, password);
        
        if (medico != null) {
            session.setAttribute("medicoLogueado", medico);
            return "redirect:/medico";
        } else {
            flash.addFlashAttribute("error", "Credenciales incorrectas.");
            return "redirect:/medico-login";
        }
    }

    // --- REGISTRO (EL CAMBIO CLAVE ESTÁ AQUÍ) ---
    @PostMapping("/registro")
    public String registro(@RequestParam String rut, @RequestParam String nombre, 
                           @RequestParam String email, @RequestParam String telefono,
                           @RequestParam String password, HttpSession session, RedirectAttributes flash) {
        
        // 1. Limpiar RUT
        String rutLimpio = ValidacionUtil.limpiarRut(rut);

        // 2. Validaciones
        if (!ValidacionUtil.esNombreValido(nombre)) {
            flash.addFlashAttribute("error", "Nombre inválido (solo letras).");
            return "redirect:/";
        }
        if (!ValidacionUtil.esRutValido(rutLimpio)) {
            flash.addFlashAttribute("error", "RUT inválido. Revise el dígito verificador.");
            return "redirect:/";
        }

        // 3. Guardar usando el RUT LIMPIO
        boolean exito = servicio.registrarNuevoPaciente(rutLimpio, email, password, nombre, telefono);
        
        if (exito) {
            // Guardar en sesión con el formato limpio
            Paciente nuevo = new Paciente(rutLimpio, email, password, nombre, telefono);
            session.setAttribute("usuarioLogueado", nuevo);
            flash.addFlashAttribute("mensaje", "¡Bienvenido a VitalSync!");
            flash.addFlashAttribute("tipo", "success");
        } else {
            flash.addFlashAttribute("error", "Error: El RUT o Email ya existen.");
        }
        return "redirect:/";
    }

    // ... (Resto de métodos: logout, reservar, cancelar, portal medico) se mantienen igual ...
    // Asegúrate de copiar los métodos reservar/cancelar/medico del código anterior si no están aquí.
    
    @GetMapping("/logout")
    public String logout(HttpSession session) { session.invalidate(); return "redirect:/"; }

    @PostMapping("/reservar")
    public String reservar(@RequestParam int idCita, HttpSession session, RedirectAttributes flash) {
        Paciente paciente = (Paciente) session.getAttribute("usuarioLogueado");
        if (paciente == null) return "redirect:/";

        // Usar RUT limpio del objeto usuario
        if (servicio.agendarHora(idCita, paciente.getRutPaciente())) {
            flash.addFlashAttribute("mensaje", "✅ Cita reservada.");
            flash.addFlashAttribute("tipo", "success");
        } else {
            flash.addFlashAttribute("mensaje", "❌ Hora no disponible.");
            flash.addFlashAttribute("tipo", "danger");
        }
        return "redirect:/";
    }

    @PostMapping("/cancelar")
    public String cancelar(@RequestParam int idCita, RedirectAttributes flash) {
        String res = servicio.cancelarCita(idCita);
        flash.addFlashAttribute("mensaje", res);
        flash.addFlashAttribute("tipo", res.startsWith("Éxito") ? "success" : "warning");
        return "redirect:/";
    }
    
    @GetMapping("/medico")
    public String portalMedico(HttpSession session, Model model) {
        Medico medico = (Medico) session.getAttribute("medicoLogueado");
        if (medico == null) return "redirect:/medico-login";
        
        // Usar RUT limpio del médico
        List<HoraMedica> agenda = servicio.obtenerAgendaMedico(medico.getRutMedico(), "2025-12-01", "2025-12-31");
        model.addAttribute("nombreMedico", medico.getNombreCompleto());
        model.addAttribute("especialidad", medico.getEspecialidad());
        model.addAttribute("agenda", agenda);
        return "medico";
    }
    
    @GetMapping("/medico-login") public String loginMedicoPage() { return "login_medico"; }
    @GetMapping("/medico-logout") public String logoutMedico(HttpSession session) { session.removeAttribute("medicoLogueado"); return "redirect:/"; }
    @GetMapping({"/login", "/registro", "/reservar", "/cancelar"}) public String redireccionar() { return "redirect:/"; }
}
