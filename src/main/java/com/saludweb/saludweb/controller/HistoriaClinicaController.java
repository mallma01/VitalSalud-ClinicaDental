
package com.saludweb.saludweb.controller;

// ==========================================
// IMPORTACIONES DE SPRING BOOT
// ==========================================
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// ==========================================
// MODELOS Y REPOSITORIOS
// ==========================================
import com.saludweb.saludweb.model.*;
import com.saludweb.saludweb.repository.*;

// ==========================================
// CONTROLADOR DE HISTORIAS CLÍNICAS
// ==========================================
@Controller
@RequestMapping("/historias")
public class HistoriaClinicaController {

    // ======================================
    // REPOSITORIOS
    // ======================================
    private final HistoriaClinicaRepository repo;
    private final PacienteRepository pacientes;
    private final UsuarioRepository usuarios;

    // ======================================
    // CONSTRUCTOR
    // ======================================
    public HistoriaClinicaController(
            HistoriaClinicaRepository repo,
            PacienteRepository pacientes,
            UsuarioRepository usuarios) {

        this.repo = repo;
        this.pacientes = pacientes;
        this.usuarios = usuarios;
    }

    // ======================================
    // 1. LISTAR HISTORIAS CLÍNICAS
    // ======================================
    @GetMapping
    public String listar(Model model) {

        // Obtener todas las historias clínicas
        model.addAttribute(
                "registros",
                repo.findAll()
        );

        // Mostrar la vista de listado
        return "historias/lista";
    }

    // ======================================
    // 2. MOSTRAR FORMULARIO NUEVA HISTORIA
    // ======================================
    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        // Crear una historia clínica vacía
        model.addAttribute(
                "registro",
                new HistoriaClinica()
        );

        // Cargar pacientes
        model.addAttribute(
                "pacientes",
                pacientes.findAll()
        );

        // Cargar usuarios
        model.addAttribute(
                "usuarios",
                usuarios.findAll()
        );

        // Mostrar formulario
        return "historias/formulario";
    }

    // ======================================
    // 3. EDITAR HISTORIA CLÍNICA
    // ======================================
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            Model model) {

        // Buscar historia clínica por ID
        model.addAttribute(
                "registro",
                repo.findById(id).orElseThrow()
        );

        // Cargar pacientes
        model.addAttribute(
                "pacientes",
                pacientes.findAll()
        );

        // Cargar usuarios
        model.addAttribute(
                "usuarios",
                usuarios.findAll()
        );

        // Mostrar formulario con datos
        return "historias/formulario";
    }

    // ======================================
    // 4. GUARDAR HISTORIA CLÍNICA
    // ======================================
    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("registro") HistoriaClinica obj) {

        // Asignar fecha actual si no existe
        if (obj.getFechaAtencion() == null) {

            obj.setFechaAtencion(
                    java.time.LocalDateTime.now()
            );
        }

        // Guardar o actualizar en MySQL
        repo.save(obj);

        // Regresar al listado
        return "redirect:/historias";
    }

    // ======================================
    // 5. ELIMINAR HISTORIA CLÍNICA
    // ======================================
    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Integer id) {

        // Eliminar registro por ID
        repo.deleteById(id);

        // Regresar al listado
        return "redirect:/historias";
    }

}
