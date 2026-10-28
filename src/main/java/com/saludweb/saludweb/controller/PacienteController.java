
package com.saludweb.saludweb.controller;

import com.saludweb.saludweb.model.Paciente;
import com.saludweb.saludweb.repository.PacienteRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteRepository repo;

    public PacienteController(PacienteRepository repo) {
        this.repo = repo;
    }

    // ==========================================
    // 1. LISTAR PACIENTES
    // ==========================================
    @GetMapping
    public String listar(Model model) {

        model.addAttribute("registros", repo.findAll());

        return "pacientes/lista";
    }

    // ==========================================
    // 2. REGISTRAR NUEVO PACIENTE
    // ==========================================
    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        Paciente p = new Paciente();
        p.setEstado("Activo");

        model.addAttribute("registro", p);

        return "pacientes/formulario";
    }

    // ==========================================
    // 3. EDITAR PACIENTE
    // ==========================================
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            Model model) {

        Paciente p = repo.findById(id).orElseThrow();

        model.addAttribute("registro", p);

        return "pacientes/formulario";
    }

    // ==========================================
    // 4. GUARDAR O ACTUALIZAR PACIENTE
    // ==========================================
    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("registro") Paciente obj,
            RedirectAttributes ra) {

        // Estado por defecto
        if (obj.getEstado() == null ||
            obj.getEstado().isBlank()) {

            obj.setEstado("Activo");
        }

        // Conservar datos anteriores si no llegan
        // desde el formulario de edición
        if (obj.getId() != null) {

            Paciente prev = repo.findById(
                    obj.getId()
            ).orElseThrow();

            if (obj.getTelefono() == null) {
                obj.setTelefono(prev.getTelefono());
            }

            if (obj.getEdad() == null) {
                obj.setEdad(prev.getEdad());
            }
        }

        // Evitar DNI duplicado
        Integer idActual = obj.getId() == null
                ? -1
                : obj.getId();

        if (repo.existsByDniAndIdNot(
                obj.getDni(),
                idActual)) {

            ra.addFlashAttribute(
                    "error",
                    "El DNI ya está registrado."
            );

            return "redirect:/pacientes";
        }

        repo.save(obj);

        ra.addFlashAttribute(
                "ok",
                "Paciente guardado correctamente."
        );

        return "redirect:/pacientes";
    }

    // ==========================================
    // 5. ELIMINAR DEFINITIVAMENTE PACIENTE
    // ==========================================
    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Integer id,
            RedirectAttributes ra) {

        try {

            // Buscar paciente
            Paciente paciente = repo.findById(id)
                    .orElseThrow();

            // Eliminar de MySQL
            repo.delete(paciente);

            // Ejecutar DELETE inmediatamente
            repo.flush();

            ra.addFlashAttribute(
                    "ok",
                    "Paciente eliminado definitivamente."
            );

        } catch (DataIntegrityViolationException e) {

            ra.addFlashAttribute(
                    "error",
                    "No se puede eliminar este paciente " +
                    "porque tiene citas, historias clínicas " +
                    "u otros registros asociados."
            );
        }

        return "redirect:/pacientes";
    }

    // ==========================================
    // 6. REACTIVAR PACIENTE
    // ==========================================
    @PostMapping("/activar/{id}")
    public String activar(
            @PathVariable Integer id) {

        Paciente p = repo.findById(id).orElseThrow();

        p.setEstado("Activo");

        repo.save(p);

        return "redirect:/pacientes";
    }
}
