package com.tecsup.demo.academic.controller;

import com.tecsup.demo.academic.dto.SubjectDTO;
import com.tecsup.demo.academic.model.Subject;
import com.tecsup.demo.academic.repository.SubjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectRepository subjectRepository;

    @PostMapping
    public ResponseEntity<?> crearSubject(@RequestBody SubjectDTO dto) {
        Subject subject = new Subject();
        subject.setName(dto.getName());

        subjectRepository.save(subject);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Materia creada correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<Subject>> listarSubjects() {
        return ResponseEntity.ok(subjectRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerSubject(@PathVariable Long id) {
        Optional<Subject> subjectOpt = subjectRepository.findById(id);
        if (subjectOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Materia no encontrada"));
        }
        return ResponseEntity.ok(subjectOpt.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSubject(@PathVariable Long id, @RequestBody SubjectDTO dto) {
        Optional<Subject> subjectOpt = subjectRepository.findById(id);
        if (subjectOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Materia no encontrada"));
        }

        Subject subject = subjectOpt.get();
        subject.setName(dto.getName());

        subjectRepository.save(subject);
        return ResponseEntity.ok(Map.of("message", "Materia actualizada correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSubject(@PathVariable Long id) {
        if (!subjectRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Materia no encontrada"));
        }

        subjectRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Materia eliminada correctamente"));
    }
}
