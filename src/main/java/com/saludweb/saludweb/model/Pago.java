package com.saludweb.saludweb.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
@Entity @Table(name="pagos_citas") public class Pago {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @OneToOne(optional=false) @JoinColumn(name="cita_id",nullable=false,unique=true) private Cita cita;
 @Column(nullable=false,precision=10,scale=2) private BigDecimal monto;
 @Column(nullable=false,length=30) private String metodo;
 @Column(nullable=false) private LocalDateTime fechaPago;
 private String referencia;
 public Integer getId(){return id;}public void setId(Integer v){id=v;}
 public Cita getCita(){return cita;}public void setCita(Cita v){cita=v;}
 public BigDecimal getMonto(){return monto;}public void setMonto(BigDecimal v){monto=v;}
 public String getMetodo(){return metodo;}public void setMetodo(String v){metodo=v;}
 public LocalDateTime getFechaPago(){return fechaPago;}public void setFechaPago(LocalDateTime v){fechaPago=v;}
 public String getReferencia(){return referencia;}public void setReferencia(String v){referencia=v;}
}
