package com.saludweb.saludweb.model;
import jakarta.persistence.*;
import java.time.*;
@Entity @Table(name="medicamentos")
public class Medicamento {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
  private String nombre;
  private Integer stock;
  private String presentacion;
 public Integer getId() { return id; }
 public void setId(Integer value) { this.id=value; }
 public String getNombre() { return nombre; }
 public void setNombre(String value) { this.nombre=value; }
 public Integer getStock() { return stock; }
 public void setStock(Integer value) { this.stock=value; }
 public String getPresentacion() { return presentacion; }
 public void setPresentacion(String value) { this.presentacion=value; }
}
