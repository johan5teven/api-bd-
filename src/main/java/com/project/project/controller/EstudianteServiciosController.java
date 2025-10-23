package com.project.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.repository.BlogRepository;
import com.project.project.repository.CursoRepository;
import com.project.project.repository.MaterialRepository;
import com.project.project.repository.ProductoRepository;

@RestController
@RequestMapping("/api/estudiante")
public class EstudianteServiciosController {

    private final CursoRepository cursoRepo;
    private final ProductoRepository productoRepo;
    private final MaterialRepository materialRepo;
    private final BlogRepository blogRepo;

    public EstudianteServiciosController(CursoRepository cursoRepo, ProductoRepository productoRepo, MaterialRepository materialRepo, BlogRepository blogRepo) {
        this.cursoRepo = cursoRepo;
        this.productoRepo = productoRepo;
        this.materialRepo = materialRepo;
        this.blogRepo = blogRepo;
    }

    @GetMapping("/servicios")
    public Map<String,Object> servicios() {
        Map<String,Object> out = new HashMap<>();

        List<Map<String,Object>> cursos = cursoRepo.findAll().stream().map(c -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id", c.getId_curso());
            m.put("titulo", c.getTitulo());
            m.put("descripcion", c.getDescripcion());
            m.put("precio", c.getPrecio());
            m.put("estado", c.getEstado());
            if (c.getTutor() != null && c.getTutor().getUsuario() != null) {
                m.put("tutor", Map.of(
                    "id", c.getTutor().getId_tutor(),
                    "nombre", c.getTutor().getUsuario().getNombre(),
                    "correo", c.getTutor().getUsuario().getCorreo(),
                    "especialidad", c.getTutor().getEspecialidad()
                ));
            } else {
                m.put("tutor", null);
            }
            return m;
        }).collect(Collectors.toList());

        List<Map<String,Object>> productos = productoRepo.findAll().stream().map(p -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id", p.getId_producto());
            m.put("titulo", p.getNombre());
            m.put("descripcion", p.getDescripcion());
            m.put("precio", p.getPrecio());
            m.put("stock", p.getStock());
            return m;
        }).collect(Collectors.toList());

        List<Map<String,Object>> materiales = materialRepo.findAll().stream().map(mat -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id", mat.getId_material());
            m.put("titulo", mat.getTitulo());
            m.put("descripcion", mat.getTipo() != null ? mat.getTipo().name() : null);
            m.put("url", mat.getUrl_archivo());
            if (mat.getCurso() != null) m.put("curso", Map.of("id", mat.getCurso().getId_curso(), "titulo", mat.getCurso().getTitulo()));
            else m.put("curso", null);
            return m;
        }).collect(Collectors.toList());

        List<Map<String,Object>> guias = blogRepo.findAll().stream().map(b -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id", b.getId_post());
            m.put("titulo", b.getTitulo());
            m.put("descripcion", b.getContenido() != null ? (b.getContenido().length() > 120 ? b.getContenido().substring(0,120) + "..." : b.getContenido()) : "");
            m.put("url", "/blog/" + b.getId_post());
            return m;
        }).collect(Collectors.toList());

        out.put("cursos", cursos);
        out.put("productos", productos);
        out.put("materiales", materiales);
        out.put("guias", guias);
        return out;
    }

    @GetMapping("/materiales")
    public List<Map<String,Object>> materiales() {
        return materialRepo.findAll().stream().map(mat -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id_material", mat.getId_material());
            m.put("titulo", mat.getTitulo());
            m.put("tipo", mat.getTipo() != null ? mat.getTipo().name() : null);
            m.put("url", mat.getUrl_archivo());
            m.put("curso", mat.getCurso() != null ? Map.of("id", mat.getCurso().getId_curso(), "titulo", mat.getCurso().getTitulo()) : null);
            return m;
        }).collect(Collectors.toList());
    }

}
