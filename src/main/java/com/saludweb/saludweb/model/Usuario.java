package com.saludweb.saludweb.model;
import jakarta.persistence.*;
import java.time.*;
@Entity @Table(name="usuarios")
public class Usuario {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
  private String nombre;
  private String rol;
  private String correo;
 public Integer getId() { return id; }
 public void setId(Integer value) { this.id=value; }
 public String getNombre() { return nombre; }
 public void setNombre(String value) { this.nombre=value; }
 public String getRol() { return rol; }
 public void setRol(String value) { this.rol=value; }
 public String getCorreo() { return correo; }
 
 public void setCorreo(String value) { this.correo=value; }
}
