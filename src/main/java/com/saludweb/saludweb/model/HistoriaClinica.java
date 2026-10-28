package com.saludweb.saludweb.model;
import jakarta.persistence.*;
import java.time.*;
@Entity @Table(name="historias_clinicas")
public class HistoriaClinica {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
  private LocalDateTime fechaAtencion;
 @Column(length=1000) private String sintomas;
 @Column(length=1000) private String diagnostico;
 @Column(length=1000) private String tratamiento;
 @ManyToOne(optional=false) @JoinColumn(name="paciente_id") private Paciente paciente;
 @ManyToOne @JoinColumn(name="medico_id") private Usuario medico;
 public Integer getId() { return id; }
 public void setId(Integer value) { this.id=value; }
 public LocalDateTime getFechaAtencion() { return fechaAtencion; }
 public void setFechaAtencion(LocalDateTime value) { this.fechaAtencion=value; }
 public String getSintomas() { return sintomas; }
 public void setSintomas(String value) { this.sintomas=value; }
 public String getDiagnostico() { return diagnostico; }
 public void setDiagnostico(String value) { this.diagnostico=value; }
 public String getTratamiento() { return tratamiento; }
 public void setTratamiento(String value) { this.tratamiento=value; }
 public Paciente getPaciente() { return paciente; }
 public void setPaciente(Paciente value) { this.paciente=value; }
 public Usuario getMedico() { return medico; }
 public void setMedico(Usuario value) { this.medico=value; }
}
