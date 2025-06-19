package com.tecsup.demo.evaluations.controller;

import com.tecsup.demo.evaluations.dto.EvaluationDTO;
import com.tecsup.demo.evaluations.model.Evaluation;
import com.tecsup.demo.evaluations.repository.EvaluationRepository;
import com.tecsup.demo.courses.model.Module;
import com.tecsup.demo.courses.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @PostMapping
    public ResponseEntity<?> crearEvaluation(@RequestBody EvaluationDTO dto) {
        Optional<Module> moduleOpt = moduleRepository.findById(dto.getModuleId());
        if (moduleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Módulo no encontrado"));
        }

        boolean exists = evaluationRepository.existsByModuleIdAndTitle(dto.getModuleId(), dto.getTitle());
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe una evaluación con el título '" + dto.getTitle() + "' en este módulo"));
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setTitle(dto.getTitle());
        evaluation.setDescription(dto.getDescription());
        evaluation.setStartDate(dto.getStartDate());
        evaluation.setEndDate(dto.getEndDate());
        evaluation.setTimeLimit(dto.getTimeLimit());
        evaluation.setModule(moduleOpt.get());

        evaluationRepository.save(evaluation);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Evaluación creada correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<Evaluation>> listarEvaluations() {
        return ResponseEntity.ok(evaluationRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEvaluation(@PathVariable Long id) {
        Optional<Evaluation> evaluationOpt = evaluationRepository.findById(id);
        if (evaluationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Evaluación no encontrada"));
        }
        return ResponseEntity.ok(evaluationOpt.get());
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<Evaluation>> listarEvaluationsPorModulo(@PathVariable Long moduleId) {
        List<Evaluation> evaluations = evaluationRepository.findByModuleIdOrderByStartDate(moduleId);
        return ResponseEntity.ok(evaluations);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEvaluation(@PathVariable Long id, @RequestBody EvaluationDTO dto) {
        Optional<Evaluation> evaluationOpt = evaluationRepository.findById(id);
        if (evaluationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Evaluación no encontrada"));
        }

        Evaluation evaluation = evaluationOpt.get();

        if (!evaluation.getModule().getId().equals(dto.getModuleId())) {
            Optional<Module> moduleOpt = moduleRepository.findById(dto.getModuleId());
            if (moduleOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Módulo no encontrado"));
            }
            evaluation.setModule(moduleOpt.get());
        }

        if (!evaluation.getTitle().equals(dto.getTitle()) &&
                evaluationRepository.existsByModuleIdAndTitle(dto.getModuleId(), dto.getTitle())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe una evaluación con el título '" + dto.getTitle() + "' en este módulo"));
        }

        evaluation.setTitle(dto.getTitle());
        evaluation.setDescription(dto.getDescription());
        evaluation.setStartDate(dto.getStartDate());
        evaluation.setEndDate(dto.getEndDate());
        evaluation.setTimeLimit(dto.getTimeLimit());

        evaluationRepository.save(evaluation);

        return ResponseEntity.ok(Map.of("message", "Evaluación actualizada correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEvaluation(@PathVariable Long id) {
        if (!evaluationRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Evaluación no encontrada"));
        }

        evaluationRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Evaluación eliminada correctamente"));
    }
}
