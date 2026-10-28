
package com.saludweb.saludweb.controller;

import com.saludweb.saludweb.model.Cita;
import com.saludweb.saludweb.model.Pago;
import com.saludweb.saludweb.repository.CitaRepository;
import com.saludweb.saludweb.repository.PagoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PagoController {

    private final PagoRepository pagos;
    private final CitaRepository citas;

    public PagoController(
            PagoRepository pagos,
            CitaRepository citas) {

        this.pagos = pagos;
        this.citas = citas;
    }

    // ==========================================
    // 1. MOSTRAR HISTORIAL Y CITAS PENDIENTES
    // ==========================================
    @GetMapping("/pagos")
    public String lista(Model m) {

        m.addAttribute("pagos", pagos.findAll());

        m.addAttribute(
            "citasPendientes",
            citas.findAll().stream()

                .filter(c ->
                    !Boolean.TRUE.equals(c.getPagado())
                )

                .filter(c ->
                    !"CANCELADA".equalsIgnoreCase(c.getEstado())
                )

                .filter(c ->
                    !pagos.existsByCita_Id(c.getId())
                )

                .sorted(
                    Comparator.comparing(Cita::getFecha)
                        .thenComparing(Cita::getHora)
                )

                .toList()
        );

        return "pagos";
    }

    // ==========================================
    // 2. ABRIR FORMULARIO DE COBRO
    // ==========================================
    @GetMapping("/pagos/cobrar/{id}")
    public String cobrar(
            @PathVariable Integer id,
            Model m,
            RedirectAttributes ra) {

        Cita c = citas.findById(id).orElse(null);

        if (c == null) {
            ra.addFlashAttribute(
                "error",
                "La cita seleccionada no existe."
            );
            return "redirect:/pagos";
        }

        if (Boolean.TRUE.equals(c.getPagado())
                || pagos.existsByCita_Id(id)
                || "CANCELADA".equalsIgnoreCase(c.getEstado())) {

            ra.addFlashAttribute(
                "error",
                "Esta cita ya está pagada o cancelada."
            );

            return "redirect:/pagos";
        }

        m.addAttribute("cita", c);

        return "pagos/formulario";
    }

    // ==========================================
    // 3. REGISTRAR PAGO EN MYSQL
    // ==========================================
    @PostMapping("/pagos/registrar")
    @Transactional
    public String registrar(
            @RequestParam Integer citaId,
            @RequestParam String metodo,
            @RequestParam BigDecimal monto,
            @RequestParam(required = false) BigDecimal recibido,
            @RequestParam(required = false) String referencia,
            RedirectAttributes ra) {

        Cita c = citas.findById(citaId).orElse(null);

        if (c == null) {
            ra.addFlashAttribute(
                "error",
                "La cita seleccionada no existe."
            );
            return "redirect:/pagos";
        }

        // EVITAR PAGOS DUPLICADOS
        if (Boolean.TRUE.equals(c.getPagado())
                || pagos.existsByCita_Id(citaId)
                || "CANCELADA".equalsIgnoreCase(c.getEstado())) {

            ra.addFlashAttribute(
                "error",
                "No se puede registrar el pago. "
                + "La cita ya fue pagada o está cancelada."
            );

            return "redirect:/pagos";
        }

        // VALIDAR MÉTODO DE PAGO
        Set<String> metodosPermitidos = Set.of(
            "EFECTIVO",
            "TARJETA",
            "TRANSFERENCIA",
            "YAPE",
            "PLIN"
        );

        if (metodo == null
                || !metodosPermitidos.contains(metodo)) {

            ra.addFlashAttribute(
                "error",
                "Selecciona un método de pago válido."
            );
            return "redirect:/pagos";
        }

        // VALIDAR MONTO DE CONSULTA
        if (monto == null
                || c.getMontoConsulta() == null
                || monto.compareTo(BigDecimal.ZERO) <= 0
                || monto.compareTo(c.getMontoConsulta()) != 0) {

            ra.addFlashAttribute(
                "error",
                "El monto debe coincidir con el precio de la cita."
            );
            return "redirect:/pagos";
        }

        // VALIDAR DINERO RECIBIDO
        if ("EFECTIVO".equals(metodo)) {

            if (recibido == null
                    || recibido.compareTo(monto) < 0) {

                ra.addFlashAttribute(
                    "error",
                    "El monto recibido debe cubrir "
                    + "el costo de la consulta."
                );
                return "redirect:/pagos";
            }

        } else {

            if (recibido == null
                    || recibido.compareTo(monto) != 0) {

                ra.addFlashAttribute(
                    "error",
                    "Para este método, el monto recibido "
                    + "debe ser exacto."
                );
                return "redirect:/pagos";
            }
        }

        // ==========================================
        // 4. GUARDAR PAGO
        // ==========================================
        Pago p = new Pago();

        p.setCita(c);
        p.setMonto(monto);
        p.setMetodo(metodo);
        p.setReferencia(referencia);
        p.setFechaPago(LocalDateTime.now());

        pagos.save(p);

        // ==========================================
        // 5. SINCRONIZAR CITA CON EL PAGO
        // ==========================================
        c.setPagado(true);

        // Una cita pendiente pasa a PROGRAMADA
        // después de confirmar el pago.
        // No modificamos ATENDIDA ni otros estados.
        if ("PENDIENTE_PAGO".equalsIgnoreCase(c.getEstado())) {
            c.setEstado("PROGRAMADA");
        }

        citas.save(c);

        // ==========================================
        // 6. CONFIRMACIÓN
        // ==========================================
        ra.addFlashAttribute(
            "ok",
            "¡Pago registrado correctamente! "
            + "La cita quedó marcada como pagada."
        );

        return "redirect:/pagos";
    }
}
