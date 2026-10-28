package com.saludweb.saludweb.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.saludweb.saludweb.model.EntregaMedicamento;
public interface EntregaMedicamentoRepository extends JpaRepository<EntregaMedicamento,Integer> { boolean existsByPaciente_Id(Integer id);
}