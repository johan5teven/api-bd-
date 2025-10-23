package com.project.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.project.model.Blog;
import com.project.project.repository.BlogRepository;

@Service
public class BlogService {
    private final BlogRepository blogRepository;

    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    public List<Blog> listarTodos() {
        return blogRepository.findAll();
    }

    public Optional<Blog> buscarPorId(Integer id) {
        return blogRepository.findById(id);
    }

    public Blog guardar(Blog blog) {
        return blogRepository.save(blog);
    }

    public void eliminar(Integer id) {
        blogRepository.deleteById(id);
    }
}
