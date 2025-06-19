package com.tecsup.demo.evaluations.controller;

import com.tecsup.demo.evaluations.dto.EvaluationAttemptDTO;
import com.tecsup.demo.evaluations.model.EvaluationAttempt;
import com.tecsup.demo.evaluations.model.Evaluation;
import com.tecsup.demo.evaluations.repository.EvaluationAttemptRepository;
import com.tecsup.demo.evaluations.repository.EvaluationRepository;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/evaluation-attempts")
public class EvaluationAttemptController {

    @Autowired
    private EvaluationAttemptRepository attemptRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> crearAttempt(@RequestBody EvaluationAttemptDTO dto) {
        Optional<Evaluation> evaluationOpt = evaluationRepository.findById(dto.getEvaluationId());
        if (evaluationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Evaluación no encontrada"));
        }

        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario no encontrado"));
        }

        boolean exists = attemptRepository.existsByEvaluationIdAndUserId(dto.getEvaluationId(), dto.getUserId());
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe un intento para esta evaluación y usuario"));
        }

        EvaluationAttempt attempt = new EvaluationAttempt();
        attempt.setSubmissionDate(dto.getSubmissionDate());
        attempt.setStatus(EvaluationAttempt.AttemptStatus.valueOf(dto.getStatus()));
        attempt.setScore(dto.getScore());
        attempt.setComments(dto.getComments());
        attempt.setEvaluation(evaluationOpt.get());
        attempt.setUser(userOpt.get());

        attemptRepository.save(attempt);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Intento de evaluación creado correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<EvaluationAttempt>> listarAttempts() {
        return ResponseEntity.ok(attemptRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerAttempt(@PathVariable Long id) {
        Optional<EvaluationAttempt> attemptOpt = attemptRepository.findById(id);
        if (attemptOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Intento de evaluación no encontrado"));
        }
        return ResponseEntity.ok(attemptOpt.get());
    }

    @GetMapping("/evaluation/{evaluationId}")
    public ResponseEntity<List<EvaluationAttempt>> listarAttemptsPorEvaluacion(@PathVariable Long evaluationId) {
        List<EvaluationAttempt> attempts = attemptRepository.findByEvaluationIdOrderBySubmissionDate(evaluationId);
        return ResponseEntity.ok(attempts);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EvaluationAttempt>> listarAttemptsPorUsuario(@PathVariable Long userId) {
        List<EvaluationAttempt> attempts = attemptRepository.findByUserIdOrderBySubmissionDate(userId);
        return ResponseEntity.ok(attempts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAttempt(@PathVariable Long id, @RequestBody EvaluationAttemptDTO dto) {
        Optional<EvaluationAttempt> attemptOpt = attemptRepository.findById(id);
        if (attemptOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Intento de evaluación no encontrado"));
        }

        EvaluationAttempt attempt = attemptOpt.get();

        if (!attempt.getEvaluation().getId().equals(dto.getEvaluationId())) {
            Optional<Evaluation> evaluationOpt = evaluationRepository.findById(dto.getEvaluationId());
            if (evaluationOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Evaluación no encontrada"));
            }
            attempt.setEvaluation(evaluationOpt.get());
        }

        if (!attempt.getUser().getId().equals(dto.getUserId())) {
            Optional<User> userOpt = userRepository.findById(dto.getUserId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Usuario no encontrado"));
            }
            attempt.setUser(userOpt.get());
        }

        attempt.setSubmissionDate(dto.getSubmissionDate());
        attempt.setStatus(EvaluationAttempt.AttemptStatus.valueOf(dto.getStatus()));
        attempt.setScore(dto.getScore());
        attempt.setComments(dto.getComments());

        attemptRepository.save(attempt);

        return ResponseEntity.ok(Map.of("message", "Intento de evaluación actualizado correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAttempt(@PathVariable Long id) {
        if (!attemptRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Intento de evaluación no encontrado"));
        }

        attemptRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Intento de evaluación eliminado correctamente"));
    }
}
