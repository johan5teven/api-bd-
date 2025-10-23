package com.project.project.config;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.project.project.model.Estudiante;
import com.project.project.model.Usuario;
import com.project.project.repository.EstudianteRepository;
import com.project.project.repository.UsuarioRepository;

@Configuration
@Profile("!test")
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner init(UsuarioRepository usuarioRepository, EstudianteRepository estudianteRepository) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                long count = estudianteRepository.count();
                if (count == 0) {
                    log.info("No estudiantes found, inserting sample data");

                    insertIfNotExists("ana@example.com", "Ana", "Gomez", "pass1", "intermedio", "programacion,matematicas", usuarioRepository, estudianteRepository);
                    insertIfNotExists("carlos@example.com", "Carlos", "Perez", "pass2", "principiante", "diseño,ux", usuarioRepository, estudianteRepository);
                    insertIfNotExists("maria@example.com", "María", "Lopez", "pass3", "avanzado", "liderazgo,negocios", usuarioRepository, estudianteRepository);
                } else {
                    log.info("Estudiantes already present: {}", count);
                }
            }

            private void insertIfNotExists(String correo, String nombre, String apellido, String contrasena, String nivel, String intereses,
                    UsuarioRepository usuarioRepository, EstudianteRepository estudianteRepository) {
                if (usuarioRepository.existsByCorreo(correo)) {
                    log.info("Usuario with correo {} already exists, skipping", correo);
                    return;
                }

                Usuario u = new Usuario();
                u.setNombre(nombre);
                u.setApellido(apellido);
                u.setCorreo(correo);
                u.setContrasena(contrasena);
                u.setRol(Usuario.Rol.estudiante);
                u.setFecha_registro(LocalDate.now());
                u.setEstado(Usuario.Estado.activo);
                Usuario saved = usuarioRepository.save(u);

                Estudiante e = new Estudiante();
                e.setNivel(nivel);
                e.setIntereses(intereses);
                e.setUsuario(saved);
                estudianteRepository.save(e);
                log.info("Inserted sample usuario {} and estudiante id {}", correo, saved.getId_usuario());
            }
        };
    }
}
