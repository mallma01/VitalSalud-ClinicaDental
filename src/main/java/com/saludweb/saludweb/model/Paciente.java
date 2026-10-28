package com.saludweb.saludweb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
@Table(name = "pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, length = 8, unique = true)
    private String dni;

    @Column
    private Integer edad;

    @Column(length = 20)
    private String telefono;

    // GETTERS Y SETTERS

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // Edad dinámica: se calcula cada vez que se consulta la ficha.
    // Si el paciente antiguo no tiene fecha de nacimiento, conserva la edad registrada.
    @jakarta.persistence.Transient
    public Integer getEdadCalculada() {
        if (fechaNacimiento == null) {
            return edad;
        }
        java.time.LocalDate hoy = java.time.LocalDate.now();
        if (fechaNacimiento.isAfter(hoy)) {
            return null;
        }
        return java.time.Period.between(fechaNacimiento, hoy).getYears();
    }

    // Campos adicionales de ficha clinica (Hibernate update conserva los existentes)
    @Column
    private java.time.LocalDate fechaNacimiento;
    public java.time.LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(java.time.LocalDate v) { this.fechaNacimiento=v; }
    @Column
    private String sexo;
    public String getSexo() { return sexo; }
    public void setSexo(String v) { this.sexo=v; }
    @Column
    private String correo;
    public String getCorreo() { return correo; }
    public void setCorreo(String v) { this.correo=v; }
    @Column
    private String direccion;
    public String getDireccion() { return direccion; }
    public void setDireccion(String v) { this.direccion=v; }
    @Column
    private String contactoEmergencia;
    public String getContactoEmergencia() { return contactoEmergencia; }
    public void setContactoEmergencia(String v) { this.contactoEmergencia=v; }
    @Column
    private String grupoSanguineo;
    public String getGrupoSanguineo() { return grupoSanguineo; }
    public void setGrupoSanguineo(String v) { this.grupoSanguineo=v; }
    @Column
    private Integer talla;
    public Integer getTalla() { return talla; }
    public void setTalla(Integer v) { this.talla=v; }
    @Column
    private Double peso;
    public Double getPeso() { return peso; }
    public void setPeso(Double v) { this.peso=v; }
    @Column
    private String estado;
    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado=v; }
    @Column(length=1500)
    private String alergias;
    public String getAlergias() { return alergias; }
    public void setAlergias(String v) { this.alergias=v; }
    @Column(length=1500)
    private String enfermedades;
    public String getEnfermedades() { return enfermedades; }
    public void setEnfermedades(String v) { this.enfermedades=v; }
    @Column(length=1500)
    private String cirugiasMedicacion;
    public String getCirugiasMedicacion() { return cirugiasMedicacion; }
    public void setCirugiasMedicacion(String v) { this.cirugiasMedicacion=v; }
    @Column(length=1500)
    private String antecedentesFamiliares;
    public String getAntecedentesFamiliares() { return antecedentesFamiliares; }
    public void setAntecedentesFamiliares(String v) { this.antecedentesFamiliares=v; }

    
    // Calcula y actualiza la edad almacenada
    public void actualizarEdad() {

        if (fechaNacimiento == null) {
            return;
        }

        java.time.LocalDate hoy = java.time.LocalDate.now();

        if (fechaNacimiento.isAfter(hoy)) {
            throw new IllegalArgumentException(
                "La fecha de nacimiento no puede ser futura."
            );
        }

        this.edad = java.time.Period.between(
            fechaNacimiento,
            hoy
        ).getYears();
    }

    // Antes de insertar o actualizar en MySQL,
    // calcula la edad automáticamente.
    @jakarta.persistence.PrePersist
    @jakarta.persistence.PreUpdate
    public void prepararGuardado() {
        actualizarEdad();
    }


}
