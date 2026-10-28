package com.saludweb.saludweb.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;
import com.saludweb.saludweb.model.Usuario;
import com.saludweb.saludweb.repository.UsuarioRepository;
@Configuration public class MedicosIniciales {
 @Bean CommandLineRunner cargarMedicos(UsuarioRepository repo){return args->{
  for(String nombre:new String[]{"Dr. Denis Ulloa","Dr. Salvatierra"}){
   if(!repo.existsByNombreIgnoreCase(nombre)){Usuario u=new Usuario();u.setNombre(nombre);u.setRol("MEDICO");repo.save(u);}
  }
 };}
}
