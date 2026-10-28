package com.saludweb.saludweb.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.saludweb.saludweb.model.Cita;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
public interface CitaRepository extends JpaRepository<Cita,Integer> {
 List<Cita> findByFechaOrderByHoraAsc(LocalDate fecha);
 boolean existsByFechaAndHoraAndMedico_IdAndEstadoNot(LocalDate fecha,LocalTime hora,Integer medicoId,String estado);


 boolean existsByPaciente_Id(Integer id);
 boolean existsByMedico_Id(Integer id);
}
