package com.tecsup.demo.courses.controller;

import com.tecsup.demo.courses.dto.AnnouncementDTO;
import com.tecsup.demo.courses.model.Announcement;
import com.tecsup.demo.courses.model.Course;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.courses.repository.AnnouncementRepository;
import com.tecsup.demo.courses.repository.CourseRepository;
import com.tecsup.demo.authentication.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public String crearAnnouncement(@RequestBody AnnouncementDTO dto) {
        // Verificar si el autor (usuario) existe
        Optional<User> authorOpt = userRepository.findById(dto.getAuthorId());
        if (authorOpt.isEmpty()) {
            return "Autor no encontrado";
        }

        // Verificar si el curso existe
        Optional<Course> courseOpt = courseRepository.findById(dto.getCourseId());
        if (courseOpt.isEmpty()) {
            return "Curso no encontrado";
        }

        // Verificar si el autor es el profesor del curso o un admin
        User author = authorOpt.get();
        Course course = courseOpt.get();

        if (!author.getRole().equals(User.UserRole.admin) &&
                !course.getProfessor().getId().equals(author.getId())) {
            return "Solo el profesor del curso o un administrador pueden crear anuncios";
        }

        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setCreationDate(LocalDateTime.now());
        announcement.setPublicationDate(dto.getPublicationDate());
        announcement.setAuthor(author);
        announcement.setCourse(course);

        announcementRepository.save(announcement);
        return "Anuncio creado correctamente";
    }

    @GetMapping
    public List<Announcement> listarAnnouncements() {
        return announcementRepository.findAll();
    }

    @GetMapping("/{id}")
    public Announcement obtenerAnnouncement(@PathVariable Long id) {
        return announcementRepository.findById(id).orElse(null);
    }

    @GetMapping("/course/{courseId}")
    public List<Announcement> listarAnnouncementsPorCurso(@PathVariable Long courseId) {
        return announcementRepository.findByCourseIdOrderByPublicationDateDesc(courseId);
    }

    @GetMapping("/author/{authorId}")
    public List<Announcement> listarAnnouncementsPorAutor(@PathVariable Long authorId) {
        return announcementRepository.findByAuthorIdOrderByCreationDateDesc(authorId);
    }

    @PutMapping("/{id}")
    public String actualizarAnnouncement(@PathVariable Long id, @RequestBody AnnouncementDTO dto) {
        Optional<Announcement> announcementOpt = announcementRepository.findById(id);
        if (announcementOpt.isEmpty()) {
            return "Anuncio no encontrado";
        }

        Announcement announcement = announcementOpt.get();

        // Verificar si se cambia el autor
        if (!announcement.getAuthor().getId().equals(dto.getAuthorId())) {
            Optional<User> authorOpt = userRepository.findById(dto.getAuthorId());
            if (authorOpt.isEmpty()) {
                return "Autor no encontrado";
            }
            announcement.setAuthor(authorOpt.get());
        }

        // Verificar si se cambia el curso
        if (!announcement.getCourse().getId().equals(dto.getCourseId())) {
            Optional<Course> courseOpt = courseRepository.findById(dto.getCourseId());
            if (courseOpt.isEmpty()) {
                return "Curso no encontrado";
            }

            // Verificar permisos para el nuevo curso
            User author = announcement.getAuthor();
            Course newCourse = courseOpt.get();

            if (!author.getRole().equals(User.UserRole.admin) &&
                    !newCourse.getProfessor().getId().equals(author.getId())) {
                return "Solo el profesor del curso o un administrador pueden crear anuncios";
            }

            announcement.setCourse(newCourse);
        }

        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setPublicationDate(dto.getPublicationDate());

        announcementRepository.save(announcement);
        return "Anuncio actualizado correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarAnnouncement(@PathVariable Long id) {
        if (announcementRepository.existsById(id)) {
            announcementRepository.deleteById(id);
            return "Anuncio eliminado correctamente";
        } else {
            return "Anuncio no encontrado";
        }
    }
}