
package com.saludweb.saludweb.controller;

// ==========================================
// IMPORTACIONES DE SPRING BOOT
// ==========================================
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// ==========================================
// MODELOS Y REPOSITORIOS
// ==========================================
import com.saludweb.saludweb.model.Usuario;
import com.saludweb.saludweb.repository.*;

// ==========================================
// CONTROLADOR DE MÉDICOS
// ==========================================
@Controller
@RequestMapping("/medicos")
public class MedicoController {

    // ======================================
    // REPOSITORIOS
    // ======================================
    private final UsuarioRepository repo;
    private final CitaRepository citas;
    private final HistoriaClinicaRepository historias;

    // ======================================
    // CONSTRUCTOR
    // ======================================
    public MedicoController(
            UsuarioRepository repo,
            CitaRepository citas,
            HistoriaClinicaRepository historias) {

        this.repo = repo;
        this.citas = citas;
        this.historias = historias;
    }

    // ======================================
    // 1. LISTAR MÉDICOS
    // ======================================
    @GetMapping
    public String lista(Model m) {

        // Obtener únicamente usuarios con rol MÉDICO
        m.addAttribute(
                "medicos",
                repo.findAll()
                        .stream()
                        .filter(x ->
                                "MEDICO".equalsIgnoreCase(
                                        x.getRol()
                                )
                        )
                        .toList()
        );

        // Mostrar listado de médicos
        return "medicos/lista";
    }

    // ======================================
    // 2. MOSTRAR FORMULARIO NUEVO MÉDICO
    // ======================================
    @GetMapping("/nuevo")
    public String nuevo(Model m) {

        // Crear un usuario vacío
        Usuario u = new Usuario();

        // Asignar rol MÉDICO
        u.setRol("MEDICO");

        // Enviar objeto al formulario
        m.addAttribute("registro", u);

        return "medicos/formulario";
    }

    // ======================================
    // 3. EDITAR MÉDICO
    // ======================================
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            Model m) {

        // Buscar médico por ID
        m.addAttribute(
                "registro",
                repo.findById(id).orElseThrow()
        );

        // Mostrar formulario con datos existentes
        return "medicos/formulario";
    }

    // ======================================
    // 4. GUARDAR O ACTUALIZAR MÉDICO
    // ======================================
    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Usuario registro) {

        // Asegurar que el usuario tenga rol MÉDICO
        registro.setRol("MEDICO");

        // Guardar en MySQL
        repo.save(registro);

        // Regresar al listado
        return "redirect:/medicos";
    }

    // ======================================
    // 5. ELIMINAR MÉDICO
    // ======================================
    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Integer id,
            RedirectAttributes ra) {

        // Verificar si tiene citas o historias clínicas
        if (
                citas.existsByMedico_Id(id)
                || historias.existsByMedico_Id(id)
        ) {

            ra.addFlashAttribute(
                    "error",
                    "No se puede eliminar: el medico tiene citas o atenciones asociadas."
            );

        } else {

            // Eliminar médico si no tiene registros asociados
            repo.deleteById(id);
        }

        return "redirect:/medicos";
    }

}
