package com.tecsup.demo.courses.controller;

import com.tecsup.demo.courses.dto.AnnouncementDTO;
import com.tecsup.demo.courses.model.Announcement;
import com.tecsup.demo.courses.model.Course;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.courses.repository.AnnouncementRepository;
import com.tecsup.demo.courses.repository.CourseRepository;
import com.tecsup.demo.authentication.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<?> crearAnnouncement(@RequestBody AnnouncementDTO dto) {
        Optional<User> authorOpt = userRepository.findById(dto.getAuthorId());
        if (authorOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Autor no encontrado"));
        }

        Optional<Course> courseOpt = courseRepository.findById(dto.getCourseId());
        if (courseOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Curso no encontrado"));
        }

        User author = authorOpt.get();
        Course course = courseOpt.get();

        if (!author.getRole().equals(User.UserRole.admin) &&
                !course.getProfessor().getId().equals(author.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Solo el profesor del curso o un administrador pueden crear anuncios"));
        }

        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setCreationDate(LocalDateTime.now());
        announcement.setPublicationDate(dto.getPublicationDate());
        announcement.setAuthor(author);
        announcement.setCourse(course);

        announcementRepository.save(announcement);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Anuncio creado correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<Announcement>> listarAnnouncements() {
        return ResponseEntity.ok(announcementRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerAnnouncement(@PathVariable Long id) {
        Optional<Announcement> announcementOpt = announcementRepository.findById(id);
        if (announcementOpt.isPresent()) {
            return ResponseEntity.ok(announcementOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Anuncio no encontrado"));
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Announcement>> listarAnnouncementsPorCurso(@PathVariable Long courseId) {
        return ResponseEntity.ok(announcementRepository.findByCourseIdOrderByPublicationDateDesc(courseId));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Announcement>> listarAnnouncementsPorAutor(@PathVariable Long authorId) {
        return ResponseEntity.ok(announcementRepository.findByAuthorIdOrderByCreationDateDesc(authorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAnnouncement(@PathVariable Long id, @RequestBody AnnouncementDTO dto) {
        Optional<Announcement> announcementOpt = announcementRepository.findById(id);
        if (announcementOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Anuncio no encontrado"));
        }

        Announcement announcement = announcementOpt.get();

        // Cambiar autor si es necesario
        if (!announcement.getAuthor().getId().equals(dto.getAuthorId())) {
            Optional<User> authorOpt = userRepository.findById(dto.getAuthorId());
            if (authorOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Autor no encontrado"));
            }
            announcement.setAuthor(authorOpt.get());
        }

        // Cambiar curso si es necesario
        if (!announcement.getCourse().getId().equals(dto.getCourseId())) {
            Optional<Course> courseOpt = courseRepository.findById(dto.getCourseId());
            if (courseOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Curso no encontrado"));
            }

            Course newCourse = courseOpt.get();
            User author = announcement.getAuthor();

            if (!author.getRole().equals(User.UserRole.admin) &&
                    !newCourse.getProfessor().getId().equals(author.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Solo el profesor del curso o un administrador pueden modificar el curso del anuncio"));
            }

            announcement.setCourse(newCourse);
        }

        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setPublicationDate(dto.getPublicationDate());

        announcementRepository.save(announcement);
        return ResponseEntity.ok(Map.of("message", "Anuncio actualizado correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAnnouncement(@PathVariable Long id) {
        if (announcementRepository.existsById(id)) {
            announcementRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Anuncio eliminado correctamente"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Anuncio no encontrado"));
        }
    }
}
