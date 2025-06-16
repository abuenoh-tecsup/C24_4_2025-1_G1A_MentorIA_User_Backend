package com.tecsup.demo.academic.controller;

import com.tecsup.demo.academic.dto.SubjectDTO;
import com.tecsup.demo.academic.model.Subject;
import com.tecsup.demo.academic.repository.SubjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectRepository subjectRepository;

    @PostMapping
    public String crearSubject(@RequestBody SubjectDTO dto) {
        Subject subject = new Subject();
        subject.setName(dto.getName());

        subjectRepository.save(subject);
        return "Materia creada correctamente";
    }

    @GetMapping
    public List<Subject> listarSubjects() {
        return subjectRepository.findAll();
    }

    @GetMapping("/{id}")
    public Subject obtenerSubject(@PathVariable Long id) {
        return subjectRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public String actualizarSubject(@PathVariable Long id, @RequestBody SubjectDTO dto) {
        Optional<Subject> subjectOpt = subjectRepository.findById(id);
        if (subjectOpt.isEmpty()) {
            return "Materia no encontrada";
        }

        Subject subject = subjectOpt.get();
        subject.setName(dto.getName());

        subjectRepository.save(subject);
        return "Materia actualizada correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarSubject(@PathVariable Long id) {
        if (subjectRepository.existsById(id)) {
            subjectRepository.deleteById(id);
            return "Materia eliminada correctamente";
        } else {
            return "Materia no encontrada";
        }
    }
}