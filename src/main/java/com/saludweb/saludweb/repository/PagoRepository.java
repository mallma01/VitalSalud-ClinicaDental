package com.saludweb.saludweb.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.saludweb.saludweb.model.Pago;
public interface PagoRepository extends JpaRepository<Pago,Integer>{boolean existsByCita_Id(Integer id);}
