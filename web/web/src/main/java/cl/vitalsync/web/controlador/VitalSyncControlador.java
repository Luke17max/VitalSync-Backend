package cl.vitalsync.web.controlador;

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

    // --- PANTALLA PRINCIPAL (PACIENTE) ---
    @GetMapping("/")
    public String inicio(HttpSession session, Model model) {
        Paciente paciente = (Paciente) session.getAttribute("usuarioLogueado");
        if (paciente == null) {
            return "login";
        }

        try {
            List<HoraMedica> disponibles = servicio.obtenerHorarioSemanal("2025-12-01", "2025-12-31");
            List<HoraMedica> misCitas = servicio.obtenerCitasDePaciente(paciente.getRutPaciente());

            model.addAttribute("usuario", paciente);
            model.addAttribute("disponibles", disponibles);
            model.addAttribute("misCitas", misCitas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "index";
    }

    // --- PORTAL MÉDICO (PROTEGIDO) ---
    @GetMapping("/medico")
    public String portalMedico(HttpSession session, Model model) {
        // 1. Verificar si hay un MÉDICO en sesión
        Medico medico = (Medico) session.getAttribute("medicoLogueado");

        if (medico == null) {
            return "redirect:/medico-login"; // Si no, mandar al login de doctores
        }

        try {
            List<HoraMedica> agenda = servicio.obtenerAgendaMedico(medico.getRutMedico(), "2025-12-01", "2025-12-31");
            model.addAttribute("nombreMedico", medico.getNombreCompleto());
            model.addAttribute("especialidad", medico.getEspecialidad());
            model.addAttribute("agenda", agenda);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "medico";
    }

    // --- LOGIN MÉDICO (VISTA) ---
    @GetMapping("/medico-login")
    public String loginMedicoPage() {
        return "login_medico"; // Carga login_medico.html
    }

    // --- LOGIN MÉDICO (ACCIÓN) ---
    @PostMapping("/medico-login")
    public String procesarLoginMedico(@RequestParam String rut, @RequestParam String password, HttpSession session, RedirectAttributes flash) {
        Medico medico = servicio.iniciarSesionMedico(rut, password);

        if (medico != null) {
            session.setAttribute("medicoLogueado", medico);
            return "redirect:/medico";
        } else {
            flash.addFlashAttribute("error", "Credenciales incorrectas.");
            return "redirect:/medico-login";
        }
    }

    @GetMapping("/medico-logout")
    public String logoutMedico(HttpSession session) {
        session.removeAttribute("medicoLogueado");
        return "redirect:/";
    }

    // --- LOGIN PACIENTE ---
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, RedirectAttributes flash) {
        Paciente paciente = servicio.iniciarSesion(email, password);
        if (paciente != null) {
            session.setAttribute("usuarioLogueado", paciente);
            return "redirect:/";
        } else {
            flash.addFlashAttribute("error", "Credenciales incorrectas.");
            return "redirect:/";
        }
    }

    // --- REGISTRO ---
    @PostMapping("/registro")
    public String registro(@RequestParam String rut, @RequestParam String nombre,
            @RequestParam String email, @RequestParam String telefono,
            @RequestParam String password, HttpSession session, RedirectAttributes flash) {
        boolean exito = servicio.registrarNuevoPaciente(rut, email, password, nombre, telefono);
        if (exito) {
            Paciente nuevo = new Paciente(rut, email, password, nombre, telefono);
            session.setAttribute("usuarioLogueado", nuevo);
            flash.addFlashAttribute("mensaje", "¡Bienvenido a VitalSync!");
            flash.addFlashAttribute("tipo", "success");
        } else {
            flash.addFlashAttribute("error", "Error al registrar.");
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // --- RESERVAS Y CANCELACIONES ---
    @PostMapping("/reservar")
    public String reservar(@RequestParam int idCita, HttpSession session, RedirectAttributes flash) {
        Paciente paciente = (Paciente) session.getAttribute("usuarioLogueado");
        if (paciente == null) {
            return "redirect:/";
        }

        if (servicio.agendarHora(idCita, paciente.getRutPaciente())) {
            flash.addFlashAttribute("mensaje", "✅ Cita reservada exitosamente.");
            flash.addFlashAttribute("tipo", "success");
        } else {
            flash.addFlashAttribute("mensaje", "❌ Error: La hora ya no está disponible.");
            flash.addFlashAttribute("tipo", "danger");
        }
        return "redirect:/";
    }

    @PostMapping("/cancelar")
    public String cancelar(@RequestParam int idCita, RedirectAttributes flash) {
        String resultado = servicio.cancelarCita(idCita);
        flash.addFlashAttribute("mensaje", resultado);
        flash.addFlashAttribute("tipo", resultado.startsWith("Éxito") ? "success" : "warning");
        return "redirect:/";
    }
}
