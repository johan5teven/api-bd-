DELIMITER //

CREATE PROCEDURE DetalleCursosPorEstudiante(IN p_id_estudiante INT)
BEGIN
    SELECT 
        c.id_curso,
        c.titulo AS titulo_curso,
        c.modalidad,
        c.fecha_inicio,
        c.fecha_fin,
        c.estado AS estado_curso,
        
        -- Datos del tutor
        u.id_usuario AS id_tutor,
        u.nombre AS nombre_tutor,
        u.apellido AS apellido_tutor,
        t.especialidad,
        t.experiencia,
        
        -- Datos del material
        m.id_material,
        m.tipo AS tipo_material,
        m.titulo AS titulo_material,
        m.url_archivo,
        m.fecha_carga
    FROM Inscripcion i
    INNER JOIN Curso c ON i.id_curso = c.id_curso
    LEFT JOIN Tutor t ON c.id_tutor = t.id_tutor
    LEFT JOIN Usuario u ON t.id_tutor = u.id_usuario
    LEFT JOIN Material m ON c.id_curso = m.id_curso
    WHERE i.id_estudiante = p_id_estudiante;
END //

DELIMITER ;
