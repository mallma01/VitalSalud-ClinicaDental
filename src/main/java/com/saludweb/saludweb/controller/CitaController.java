
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
import com.saludweb.saludweb.model.*;
import com.saludweb.saludweb.repository.*;

// ==========================================
// LIBRERÍAS DE JAVA
// ==========================================
import java.time.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.List;

// ==========================================
// CONTROLADOR DE CITAS
// ==========================================
@Controller
@RequestMapping("/citas")
public class CitaController {

    // ======================================
    // REPOSITORIOS
    // ======================================
    private final CitaRepository repo;
    private final PacienteRepository pacientes;
    private final UsuarioRepository usuarios;

    // ======================================
    // CONSTRUCTOR
    // ======================================
    public CitaController(
            CitaRepository repo,
            PacienteRepository pacientes,
            UsuarioRepository usuarios) {

        this.repo = repo;
        this.pacientes = pacientes;
        this.usuarios = usuarios;
    }

    // ======================================
    // 1. LISTAR CITAS POR FECHA
    // ======================================
    @GetMapping
    public String listar(
            Model m,
            @RequestParam(required = false) String fecha) {

        LocalDate d;

        try {
            d = fecha == null || fecha.isBlank()
                    ? LocalDate.now()
                    : LocalDate.parse(fecha);

        } catch (Exception e) {
            d = LocalDate.now();
        }

        m.addAttribute("fechaFiltro", d);

        m.addAttribute(
                "registros",
                repo.findByFechaOrderByHoraAsc(d)
        );

        return "citas/lista";
    }

    // ======================================
    // 2. MOSTRAR FORMULARIO NUEVA CITA
    // ======================================
    @GetMapping("/nuevo")
    public String nuevo(Model m) {

        m.addAttribute("registro", new Cita());

        cargar(m);

        return "citas/formulario";
    }

    // ======================================
    // 3. MOSTRAR FORMULARIO EDITAR CITA
    // ======================================
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            Model m) {

        m.addAttribute(
                "registro",
                repo.findById(id).orElseThrow()
        );

        cargar(m);

        return "citas/formulario";
    }

    // ======================================
    // 4. CARGAR PACIENTES Y MÉDICOS
    // ======================================
    private void cargar(Model m) {

        // Lista de pacientes
        m.addAttribute(
                "pacientes",
                pacientes.findAll()
        );

        // Lista de usuarios con rol MÉDICO
        m.addAttribute(
                "medicos",
                usuarios.findAll()
                        .stream()
                        .filter(u ->
                                "MEDICO".equalsIgnoreCase(
                                        u.getRol()
                                )
                        )
                        .toList()
        );
    }

    // ======================================
    // 5. GUARDAR O ACTUALIZAR CITA
    // ======================================
    @PostMapping("/guardar")
    public String guardar(
            @RequestParam(required = false) Integer id,
            @RequestParam Integer pacienteId,
            @RequestParam(required = false) Integer medicoId,
            @RequestParam String especialidad,
            @RequestParam LocalDate fecha,
            @RequestParam LocalTime hora,
            @RequestParam(required = false) String estado,
            RedirectAttributes ra) {

        // Crear nueva cita o buscar la existente
        Cita c = id == null
                ? new Cita()
                : repo.findById(id).orElseThrow();

        // ----------------------------------
        // VALIDAR FECHA PASADA
        // ----------------------------------
        if (
                fecha.isBefore(LocalDate.now()) &&
                (id == null || !fecha.equals(c.getFecha()))
        ) {

            ra.addFlashAttribute(
                    "error",
                    "No puedes programar una fecha pasada."
            );

            return "redirect:/citas/nuevo";
        }

        // ----------------------------------
        // VALIDAR MÉDICO SELECCIONADO
        // ----------------------------------
        if (medicoId == null) {

            ra.addFlashAttribute(
                    "error",
                    "Selecciona un medico para reservar un horario."
            );

            return "redirect:/citas/nuevo";
        }

        // ----------------------------------
        // VALIDAR HORARIO OCUPADO
        // ----------------------------------
        // Impide citas simultáneas para el
        // mismo médico o el mismo paciente.

        boolean ocupada = repo
                .findByFechaOrderByHoraAsc(fecha)
                .stream()
                .anyMatch(x ->

                        // Excluir la cita actual al editar
                        !Objects.equals(
                                x.getId(),
                                c.getId()
                        )

                        // Ignorar citas canceladas
                        && !"CANCELADA".equalsIgnoreCase(
                                x.getEstado()
                        )

                        // Verificar misma hora
                        && hora.equals(x.getHora())

                        // Verificar médico o paciente
                        && (
                                (
                                        x.getMedico() != null
                                        && medicoId.equals(
                                                x.getMedico().getId()
                                        )
                                )
                                ||
                                (
                                        x.getPaciente() != null
                                        && pacienteId.equals(
                                                x.getPaciente().getId()
                                        )
                                )
                        )
                );

        // ----------------------------------
        // MOSTRAR ERROR SI ESTÁ OCUPADO
        // ----------------------------------
        if (ocupada) {

            ra.addFlashAttribute(
                    "error",
                    "Horario ocupado: el medico o el paciente ya tiene una cita activa a esa hora."
            );

            return "redirect:/citas/nuevo";
        }

        // ----------------------------------
        // ASIGNAR PACIENTE Y MÉDICO
        // ----------------------------------
        c.setPaciente(
                pacientes.findById(pacienteId).orElseThrow()
        );

        c.setMedico(
                medicoId == null
                        ? null
                        : usuarios.findById(medicoId).orElseThrow()
        );

        // ----------------------------------
        // ASIGNAR DATOS DE LA CITA
        // ----------------------------------
        c.setEspecialidad(especialidad);
        c.setFecha(fecha);
        c.setHora(hora);

        // ----------------------------------
        // ESTADO DE LA CITA
        // ----------------------------------
        if (c.getEstado() == null) {

            c.setEstado("PENDIENTE_PAGO");

        } else if (
                estado != null
                && Set.of(
                        "PENDIENTE_PAGO",
                        "PROGRAMADA",
                        "ATENDIDA",
                        "CANCELADA"
                ).contains(estado)
                && !Boolean.TRUE.equals(c.getPagado())
        ) {

            c.setEstado(estado);
        }

        // ----------------------------------
        // ESTADO INICIAL DEL PAGO
        // ----------------------------------
        if (c.getPagado() == null) {

            c.setPagado(false);
        }

        // ----------------------------------
        // PRECIO DE LA CONSULTA
        // ----------------------------------
        if (c.getMontoConsulta() == null) {

            c.setMontoConsulta(
                    new BigDecimal("60.00")
            );
        }

        // ----------------------------------
        // GUARDAR EN LA BASE DE DATOS
        // ----------------------------------
        repo.save(c);

        // ----------------------------------
        // REGRESAR A LA AGENDA
        // ----------------------------------
        return "redirect:/citas?fecha=" + fecha;
    }

    // ======================================
    // 6. CONSULTAR HORARIOS OCUPADOS
    // ======================================
    @GetMapping("/ocupados")
    @ResponseBody
    public List<String> ocupados(
            @RequestParam LocalDate fecha,
            @RequestParam Integer medicoId,
            @RequestParam(required = false) Integer pacienteId,
            @RequestParam(required = false) Integer excluirId) {

        return repo
                .findByFechaOrderByHoraAsc(fecha)
                .stream()

                // Excluir cita actual
                .filter(x ->
                        !Objects.equals(
                                x.getId(),
                                excluirId
                        )
                )

                // Ignorar citas canceladas
                .filter(x ->
                        !"CANCELADA".equalsIgnoreCase(
                                x.getEstado()
                        )
                )

                // Filtrar por médico o paciente
                .filter(x ->

                        (
                                x.getMedico() != null
                                && medicoId.equals(
                                        x.getMedico().getId()
                                )
                        )

                        ||

                        (
                                pacienteId != null
                                && x.getPaciente() != null
                                && pacienteId.equals(
                                        x.getPaciente().getId()
                                )
                        )
                )

                // Convertir horas a HH:mm
                .map(x ->
                        x.getHora()
                                .toString()
                                .substring(0, 5)
                )

                // Eliminar horas repetidas
                .distinct()
                .toList();
    }

    // ======================================
    // 7. CANCELAR CITA
    // ======================================
    @PostMapping("/cancelar/{id}")
    public String cancelar(
            @PathVariable Integer id,
            RedirectAttributes ra) {

        Cita c = repo
                .findById(id)
                .orElseThrow();

        // No cancelar citas pagadas
        if (Boolean.TRUE.equals(c.getPagado())) {

            ra.addFlashAttribute(
                    "error",
                    "La cita esta pagada. Gestiona primero la devolucion; no se cancelo."
            );

            return "redirect:/citas?fecha="
                    + c.getFecha();
        }

        // Cambiar estado a cancelada
        c.setEstado("CANCELADA");

        repo.save(c);

        return "redirect:/citas?fecha="
                + c.getFecha();
    }

    // ======================================
    // 8. ELIMINAR CITA
    // ======================================
    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Integer id,
            RedirectAttributes ra) {

        Cita c = repo
                .findById(id)
                .orElseThrow();

        // No eliminar citas pagadas
        if (Boolean.TRUE.equals(c.getPagado())) {

            ra.addFlashAttribute(
                    "error",
                    "No se puede eliminar una cita pagada; conserva el registro contable."
            );

            return "redirect:/citas?fecha="
                    + c.getFecha();
        }

        // Guardar fecha antes de eliminar
        LocalDate fecha = c.getFecha();

        // Eliminar cita
        repo.delete(c);

        ra.addFlashAttribute(
                "ok",
                "Cita eliminada correctamente."
        );

        return "redirect:/citas?fecha="
                + fecha;
    }
}
