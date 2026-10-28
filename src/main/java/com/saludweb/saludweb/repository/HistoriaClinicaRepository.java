package com.saludweb.saludweb.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.saludweb.saludweb.model.HistoriaClinica;
public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica,Integer> {

 boolean existsByPaciente_Id(Integer id);
 boolean existsByMedico_Id(Integer id);
}
