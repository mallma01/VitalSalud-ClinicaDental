package com.saludweb.saludweb.model;
import jakarta.persistence.*;
import java.time.*;
@Entity @Table(name="entregas_medicamentos")
public class EntregaMedicamento {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @ManyToOne(optional=false) @JoinColumn(name="paciente_id") private Paciente paciente;
 @ManyToOne @JoinColumn(name="usuario_id") private Usuario usuario;
 @ManyToOne(optional=false) @JoinColumn(name="medicamento_id") private Medicamento medicamento;
  private Integer cantidad;
  private LocalDateTime fechaEntrega;
 public Integer getId() { return id; }
 public void setId(Integer value) { this.id=value; }
 public Paciente getPaciente() { return paciente; }
 public void setPaciente(Paciente value) { this.paciente=value; }
 public Usuario getUsuario() { return usuario; }
 public void setUsuario(Usuario value) { this.usuario=value; }
 public Medicamento getMedicamento() { return medicamento; }
 public void setMedicamento(Medicamento value) { this.medicamento=value; }
 public Integer getCantidad() { return cantidad; }
 public void setCantidad(Integer value) { this.cantidad=value; }
 public LocalDateTime getFechaEntrega() { return fechaEntrega; }
 public void setFechaEntrega(LocalDateTime value) { this.fechaEntrega=value; }
}
