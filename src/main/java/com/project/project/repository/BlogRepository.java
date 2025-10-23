package com.project.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.project.model.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {
}
