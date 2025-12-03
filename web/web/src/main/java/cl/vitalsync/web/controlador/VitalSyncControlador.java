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
        return "login_medico"; 
    }

    // --- LOGIN MÉDICO (ACCIÓN) ---
    @PostMapping("/medico-login")
    public String procesarLoginMedico(@RequestParam String rut, @RequestParam String password, HttpSession session, RedirectAttributes flash) {
        
        // Validación previa de formato
        if (!ValidacionUtil.esRutValido(rut)) {
            flash.addFlashAttribute("error", "Formato de RUT inválido.");
            return "redirect:/medico-login";
        }

        Medico medico = servicio.iniciarSesionMedico(rut, password);
        
        if (medico != null) {
            session.setAttribute("medicoLogueado", medico);
            return "redirect:/medico";
        } else {
            flash.addFlashAttribute("error", "Credenciales incorrectas o médico no registrado.");
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
        
        // 1. Validar que el email tenga formato correcto (ej: texto@texto.com)
        if (!ValidacionUtil.esEmailValido(email)) {
            flash.addFlashAttribute("error", "El formato del correo no es válido.");
            return "redirect:/"; // Vuelve a cargar el login con el error
        }

        // 2. Validar que la contraseña no venga vacía
        if (password == null || password.trim().isEmpty()) {
            flash.addFlashAttribute("error", "Debe ingresar su contraseña.");
            return "redirect:/";
        }

        // 3. Si pasa las validaciones, intentamos buscar en la Base de Datos
        Paciente paciente = servicio.iniciarSesion(email, password);
        
        if (paciente != null) {
            session.setAttribute("usuarioLogueado", paciente);
            return "redirect:/";
        } else {
            flash.addFlashAttribute("error", "Credenciales incorrectas o usuario no registrado.");
            return "redirect:/"; 
        }
    }

    // --- REGISTRO ---
    @PostMapping("/registro")
    public String registro(@RequestParam String rut, @RequestParam String nombre,
            @RequestParam String email, @RequestParam String telefono,
            @RequestParam String password, HttpSession session, RedirectAttributes flash) {

        // 1. Validar Nombre (Sin símbolos raros)
        if (!ValidacionUtil.esNombreValido(nombre)) {
            flash.addFlashAttribute("error", "El nombre contiene caracteres inválidos.");
            return "redirect:/";
        }

        // 2. Validar Teléfono (Solo números)
        if (!ValidacionUtil.esTelefonoValido(telefono)) {
            flash.addFlashAttribute("error", "El teléfono debe contener solo números.");
            return "redirect:/";
        }

        // 3. Validar RUT 
        if (!ValidacionUtil.esRutValido(rut)) {
            flash.addFlashAttribute("error", "El RUT ingresado no es válido.");
            return "redirect:/";
        }

        // 4. Validar Contraseña (Mínimo 4 caracteres)
        if (password == null || password.length() < 4) {
            flash.addFlashAttribute("error", "La contraseña es muy corta.");
            return "redirect:/";
        }

        // SI PASA TODO, INTENTAMOS GUARDAR
        boolean exito = servicio.registrarNuevoPaciente(rut, email, password, nombre, telefono);

        if (exito) {
            Paciente nuevo = new Paciente(rut, email, password, nombre, telefono);
            session.setAttribute("usuarioLogueado", nuevo);
            flash.addFlashAttribute("mensaje", "¡Bienvenido a VitalSync!");
            flash.addFlashAttribute("tipo", "success");
        } else {
            flash.addFlashAttribute("error", "Error: El RUT o Correo ya están registrados.");
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
