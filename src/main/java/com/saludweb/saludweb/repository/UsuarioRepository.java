package com.saludweb.saludweb.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.saludweb.saludweb.model.Usuario;
public interface UsuarioRepository extends JpaRepository<Usuario,Integer> { boolean existsByNombreIgnoreCase(String nombre);
}
