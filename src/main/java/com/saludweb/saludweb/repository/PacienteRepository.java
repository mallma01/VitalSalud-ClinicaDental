package com.saludweb.saludweb.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.saludweb.saludweb.model.Paciente;
public interface PacienteRepository extends JpaRepository<Paciente,Integer> { boolean existsByDniAndIdNot(String dni,Integer id); }
