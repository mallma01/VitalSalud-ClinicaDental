package com.saludweb.saludweb.model;
import jakarta.persistence.*;
import java.time.*;
@Entity @Table(name="recetas_medicamentos")
public class RecetaMedicamento {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @ManyToOne(optional=false) @JoinColumn(name="historia_id") private HistoriaClinica historia;
 @ManyToOne(optional=false) @JoinColumn(name="medicamento_id") private Medicamento medicamento;
  private Integer cantidad;
 public Integer getId() { return id; }
 public void setId(Integer value) { this.id=value; }
 public HistoriaClinica getHistoria() { return historia; }
 public void setHistoria(HistoriaClinica value) { this.historia=value; }
 public Medicamento getMedicamento() { return medicamento; }
 public void setMedicamento(Medicamento value) { this.medicamento=value; }
 public Integer getCantidad() { return cantidad; }
 public void setCantidad(Integer value) { this.cantidad=value; }
}
