package com.tecsup.demo.academic.controller;

import com.tecsup.demo.academic.dto.CareerDTO;
import com.tecsup.demo.academic.model.Career;
import com.tecsup.demo.academic.repository.CareerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/careers")
public class CareerController {

    @Autowired
    private CareerRepository careerRepository;

    @PostMapping
    public ResponseEntity<?> crearCareer(@RequestBody CareerDTO dto) {
        Career career = new Career();
        career.setCode(dto.getCode());
        career.setName(dto.getName());

        careerRepository.save(career);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Carrera creada correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<Career>> listarCareers() {
        return ResponseEntity.ok(careerRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCareer(@PathVariable Long id) {
        Optional<Career> careerOpt = careerRepository.findById(id);
        if (careerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Carrera no encontrada"));
        }
        return ResponseEntity.ok(careerOpt.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCareer(@PathVariable Long id, @RequestBody CareerDTO dto) {
        Optional<Career> careerOpt = careerRepository.findById(id);
        if (careerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Carrera no encontrada"));
        }

        Career career = careerOpt.get();
        career.setCode(dto.getCode());
        career.setName(dto.getName());

        careerRepository.save(career);
        return ResponseEntity.ok(Map.of("message", "Carrera actualizada correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCareer(@PathVariable Long id) {
        if (!careerRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Carrera no encontrada"));
        }

        careerRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Carrera eliminada correctamente"));
    }
}
