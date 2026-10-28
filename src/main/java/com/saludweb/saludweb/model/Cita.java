
package com.saludweb.saludweb.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;

@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate fecha;

    private LocalTime hora;

    private String especialidad;

    private String estado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Usuario medico;

    @Column(name = "monto_consulta")
    private BigDecimal montoConsulta;

    @Column(name = "pagado")
    private Boolean pagado;

    // =========================
    // GETTERS Y SETTERS
    // =========================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getEstado() {
        return estado;
    }

    // Evita que una cita pagada vuelva a pendiente de pago
    public void setEstado(String estado) {

        if (Boolean.TRUE.equals(this.pagado)
                && "PENDIENTE_PAGO".equalsIgnoreCase(estado)) {

            this.estado = "PROGRAMADA";

        } else {

            this.estado = estado;

        }
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Usuario getMedico() {
        return medico;
    }

    public void setMedico(Usuario medico) {
        this.medico = medico;
    }

    public BigDecimal getMontoConsulta() {
        return montoConsulta;
    }

    public void setMontoConsulta(BigDecimal montoConsulta) {
        this.montoConsulta = montoConsulta;
    }

    public Boolean getPagado() {
        return pagado;
    }

    // Actualiza automáticamente el estado al pagar
    public void setPagado(Boolean pagado) {

        this.pagado = pagado;

        if (Boolean.TRUE.equals(pagado)
                && "PENDIENTE_PAGO".equalsIgnoreCase(this.estado)) {

            this.estado = "PROGRAMADA";

        }
    }
}
