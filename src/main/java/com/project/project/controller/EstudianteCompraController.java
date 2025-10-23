package com.project.project.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.model.Agendamiento;
import com.project.project.model.DetalleOrden;
import com.project.project.model.Orden;
import com.project.project.model.Pago;
import com.project.project.repository.AgendamientoRepository;
import com.project.project.repository.DetalleOrdenRepository;
import com.project.project.repository.OrdenRepository;
import com.project.project.repository.PagoRepository;
import com.project.project.repository.TutorRepository;
import com.project.project.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/estudiante")
public class EstudianteCompraController {

    @Autowired private TutorRepository tutorRepo;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private OrdenRepository ordenRepo;
    @Autowired private DetalleOrdenRepository detalleRepo;
    @Autowired private PagoRepository pagoRepo;
    @Autowired private AgendamientoRepository agendRepo;
    @Autowired private com.project.project.repository.EstudianteRepository estudianteRepo;
    @Autowired private com.project.project.repository.CursoRepository cursoRepo;
    @Autowired private com.project.project.repository.InscripcionRepository inscripcionRepo;

    @GetMapping("/tutores")
    public List<Map<String,Object>> listarTutores() {
        return tutorRepo.findAll().stream().map(t -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id", t.getId_tutor());
            m.put("nombre", t.getUsuario() != null ? t.getUsuario().getNombre() : null);
            m.put("correo", t.getUsuario() != null ? t.getUsuario().getCorreo() : null);
            m.put("especialidad", t.getEspecialidad());
            return m;
        }).collect(Collectors.toList());
    }

    @PostMapping("/comprar-agendamiento")
    public ResponseEntity<?> comprarAgendamiento(@RequestBody Map<String,Object> body) {
        try {
            Integer estudianteId = (Integer) body.get("estudianteId");
            Integer tutorId = (Integer) body.get("tutorId");
            String tier = (String) body.getOrDefault("tier", "basico");
            if (estudianteId == null || tutorId == null) return ResponseEntity.badRequest().body(Map.of("error","estudianteId and tutorId required"));
            double cost = switch (tier) {
                case "pro" -> 120.0;
                case "mid" -> 60.0;
                default -> 30.0;
            };

            var usuario = usuarioRepo.findById(estudianteId).orElse(null);
            var tutor = tutorRepo.findById(tutorId).orElse(null);
            if (usuario == null) return ResponseEntity.badRequest().body(Map.of("error","estudiante not found"));
            if (tutor == null) return ResponseEntity.badRequest().body(Map.of("error","tutor not found"));

            Orden orden = new Orden();
            orden.setUsuario(usuario);
            orden.setFecha_orden(LocalDate.now());
            orden.setTotal(cost);
            orden.setEstado(Orden.Estado.pagado);
            orden = ordenRepo.save(orden);

            DetalleOrden det = new DetalleOrden();
            det.setOrden(orden);
            det.setProducto(null);
            det.setCantidad(1);
            det.setPrecio_unitario(cost);
            detalleRepo.save(det);

            Pago pago = new Pago();
            pago.setOrden(orden);
            pago.setMetodo(Pago.Metodo.tarjeta);
            pago.setFecha_pago(LocalDate.now());
            pago.setMonto(cost);
            pago.setEstado(Pago.Estado.exitoso);
            pagoRepo.save(pago);

            Agendamiento ag = new Agendamiento();
            var estudiante = estudianteRepo.findById(estudianteId).orElse(null);
            // If not found by estudiante id, try to find Estudiante by its Usuario id (some clients send usuarioId)
            if (estudiante == null) {
                estudiante = estudianteRepo.findAll().stream()
                        .filter(e -> e.getUsuario() != null && e.getUsuario().getId_usuario().equals(estudianteId))
                        .findFirst().orElse(null);
            }
            ag.setUsuario(estudiante);
            ag.setTutor(tutor);
            ag.setEstado(Agendamiento.Estado.pendiente);
            agendRepo.save(ag);

            return ResponseEntity.ok(Map.of("ok",true, "id_orden", orden.getId_orden()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/comprar-curso")
    public ResponseEntity<?> comprarCurso(@RequestBody Map<String,Object> body) {
        try {
            // parse estudianteId robustly (may come as Integer, Long, Double, or String)
            Integer estudianteId = null;
            Object rawEst = body.get("estudianteId");
            if (rawEst == null) rawEst = body.get("usuarioId"); // accept usuarioId as alternative
            if (rawEst instanceof Number) estudianteId = ((Number) rawEst).intValue();
            else if (rawEst instanceof String) {
                try { estudianteId = Integer.valueOf((String) rawEst); } catch (NumberFormatException ex) { estudianteId = null; }
            }

            Integer cursoId = null;
            Object rawCur = body.get("cursoId");
            if (rawCur instanceof Number) cursoId = ((Number) rawCur).intValue();
            else if (rawCur instanceof String) {
                try { cursoId = Integer.valueOf((String) rawCur); } catch (NumberFormatException ex) { cursoId = null; }
            }

            if (estudianteId == null || cursoId == null) return ResponseEntity.badRequest().body(Map.of("error","estudianteId and cursoId required"));

            var estudiante = estudianteRepo.findById(estudianteId).orElse(null);
            var curso = cursoRepo.findById(cursoId).orElse(null);
            if (estudiante == null) return ResponseEntity.badRequest().body(Map.of("error","estudiante not found"));
            if (curso == null) return ResponseEntity.badRequest().body(Map.of("error","curso not found"));

            double cost = curso.getPrecio() == null ? 0.0 : curso.getPrecio();

            Orden orden = new Orden();
            orden.setUsuario(estudiante.getUsuario());
            orden.setFecha_orden(LocalDate.now());
            orden.setTotal(cost);
            orden.setEstado(Orden.Estado.pagado);
            orden = ordenRepo.save(orden);

            DetalleOrden det = new DetalleOrden();
            det.setOrden(orden);
            det.setProducto(null); // producto entity not used for cursos; keep null or extend model
            det.setCantidad(1);
            det.setPrecio_unitario(cost);
            detalleRepo.save(det);

            Pago pago = new Pago();
            pago.setOrden(orden);
            pago.setMetodo(Pago.Metodo.tarjeta);
            pago.setFecha_pago(LocalDate.now());
            pago.setMonto(cost);
            pago.setEstado(Pago.Estado.exitoso);
            pagoRepo.save(pago);

            // create Inscripcion
            com.project.project.model.Inscripcion ins = new com.project.project.model.Inscripcion();
            ins.setCurso(curso);
            ins.setEstudiante(estudiante);
            ins.setFecha_inscripcion(LocalDate.now());
            ins.setEstado(com.project.project.model.Inscripcion.Estado.activo);
            inscripcionRepo.save(ins);

            return ResponseEntity.ok(Map.of("ok",true, "id_orden", orden.getId_orden()));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/adquiridos")
    public Map<String,Object> adquiridos(@RequestParam("estudianteId") Integer estudianteId) {
        Map<String,Object> out = new HashMap<>();
        var ordenes = ordenRepo.findAll().stream().filter(o -> o.getUsuario() != null && o.getUsuario().getId_usuario().equals(estudianteId)).map(o -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id", o.getId_orden());
            m.put("fecha", o.getFecha_orden());
            m.put("total", o.getTotal());
            m.put("estado", o.getEstado());
            return m;
        }).collect(Collectors.toList());

        var agendas = agendRepo.findAll().stream().filter(a -> a.getUsuario() != null && a.getUsuario().getId_estudiante().equals(estudianteId)).map(a -> {
            Map<String,Object> m = new HashMap<>();
            m.put("id", a.getId_agenda());
            m.put("tutor", a.getTutor() != null && a.getTutor().getUsuario() != null ? a.getTutor().getUsuario().getCorreo() : null);
            m.put("fecha", a.getFecha());
            m.put("hora", a.getHora());
            m.put("estado", a.getEstado());
            return m;
        }).collect(Collectors.toList());

        out.put("ordenes", ordenes);
        out.put("agendamientos", agendas);
        return out;
    }
}
