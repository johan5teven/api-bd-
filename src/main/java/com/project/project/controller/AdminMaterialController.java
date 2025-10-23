package com.project.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.model.Material;
import com.project.project.repository.CursoRepository;
import com.project.project.repository.MaterialRepository;

@RestController
@RequestMapping("/api/admin/materiales")
public class AdminMaterialController {

    @Autowired private MaterialRepository materialRepo;
    @Autowired private CursoRepository cursoRepo;

    @GetMapping("/listar")
    public List<Map<String,Object>> listar() {
        return materialRepo.findAll().stream().map(m -> {
            Map<String,Object> r = new HashMap<>();
            r.put("id_material", m.getId_material());
            r.put("titulo", m.getTitulo());
            r.put("tipo", m.getTipo());
            r.put("curso_id", m.getCurso() != null ? m.getCurso().getId_curso() : null);
            return r;
        }).toList();
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestBody Map<String,Object> body) {
        try {
            Material m = new Material();
            m.setTitulo((String) body.get("titulo"));
            if (body.containsKey("tipo") && body.get("tipo") instanceof String) {
                try {
                    m.setTipo(com.project.project.model.Material.Tipo.valueOf(((String) body.get("tipo")).trim()));
                } catch (Exception ex) {
                    // ignore invalid tipo
                }
            }
            if (body.containsKey("curso_id")) {
                Integer id = (Integer) body.get("curso_id");
                cursoRepo.findById(id).ifPresent(c -> m.setCurso(c));
            }
            materialRepo.save(m);
            return ResponseEntity.ok(Map.of("id_material", m.getId_material()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
