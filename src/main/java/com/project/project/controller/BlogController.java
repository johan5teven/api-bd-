package com.project.project.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.project.model.Blog;
import com.project.project.service.BlogService;
import com.project.project.service.LogMongoService;

@RestController
@RequestMapping("/api/blogs")
@CrossOrigin(origins = "*")
public class BlogController {

    private final BlogService blogService;
    private final LogMongoService logService;

    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

    public BlogController(BlogService blogService, LogMongoService logService) {
        this.blogService = blogService;
        this.logService = logService;
    }

    @GetMapping
    public List<Blog> listar() {
        return blogService.listarTodos();
    }

    @GetMapping("/{id}")
    public Blog buscarPorId(@PathVariable Integer id) {
        return blogService.buscarPorId(id).orElse(null);
    }

    @PostMapping
    public Blog crear(@RequestBody Blog blog) {
        Blog saved = blogService.guardar(blog);
        try {
            logService.registrarEvento("blogs", "create", "Creación de blog id=" + saved.getId_post());
        } catch (Exception ex) {
            logger.error("No se pudo persistir log de creación de blog: {}", ex.getMessage(), ex);
        }
        return saved;
    }

    @PutMapping("/{id}")
    public Blog actualizar(@PathVariable Integer id, @RequestBody Blog blog) {
        blog.setId_post(id);
        Blog updated = blogService.guardar(blog);
        try {
            logService.registrarEvento("blogs", "update", "Actualización de blog id=" + updated.getId_post());
        } catch (Exception ex) {
            logger.error("No se pudo persistir log de actualización de blog: {}", ex.getMessage(), ex);
        }
        return updated;
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        blogService.eliminar(id);
        try {
            logService.registrarEvento("blogs", "delete", "Eliminación de blog id=" + id);
        } catch (Exception ex) {
            logger.error("No se pudo persistir log de eliminación de blog: {}", ex.getMessage(), ex);
        }
    }
}
