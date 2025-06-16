package com.tecsup.demo.evaluations.controller;

import com.tecsup.demo.evaluations.dto.EvaluationAttemptDTO;
import com.tecsup.demo.evaluations.model.EvaluationAttempt;
import com.tecsup.demo.evaluations.model.Evaluation;
import com.tecsup.demo.evaluations.repository.EvaluationAttemptRepository;
import com.tecsup.demo.evaluations.repository.EvaluationRepository;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

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
    public String crearAttempt(@RequestBody EvaluationAttemptDTO dto) {
        // Verificar si la evaluación existe
        Optional<Evaluation> evaluationOpt = evaluationRepository.findById(dto.getEvaluationId());
        if (evaluationOpt.isEmpty()) {
            return "Evaluación no encontrada";
        }

        // Verificar si el usuario existe
        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        if (userOpt.isEmpty()) {
            return "Usuario no encontrado";
        }

        // Verificar si ya existe un intento para esta evaluación y usuario
        if (attemptRepository.existsByEvaluationIdAndUserId(dto.getEvaluationId(), dto.getUserId())) {
            return "Ya existe un intento para esta evaluación y usuario";
        }

        EvaluationAttempt attempt = new EvaluationAttempt();
        attempt.setSubmissionDate(dto.getSubmissionDate());
        attempt.setStatus(EvaluationAttempt.AttemptStatus.valueOf(dto.getStatus()));
        attempt.setScore(dto.getScore());
        attempt.setComments(dto.getComments());
        attempt.setEvaluation(evaluationOpt.get());
        attempt.setUser(userOpt.get());

        attemptRepository.save(attempt);
        return "Intento de evaluación creado correctamente";
    }

    @GetMapping
    public List<EvaluationAttempt> listarAttempts() {
        return attemptRepository.findAll();
    }

    @GetMapping("/{id}")
    public EvaluationAttempt obtenerAttempt(@PathVariable Long id) {
        return attemptRepository.findById(id).orElse(null);
    }

    @GetMapping("/evaluation/{evaluationId}")
    public List<EvaluationAttempt> listarAttemptsPorEvaluacion(@PathVariable Long evaluationId) {
        return attemptRepository.findByEvaluationIdOrderBySubmissionDate(evaluationId);
    }

    @GetMapping("/user/{userId}")
    public List<EvaluationAttempt> listarAttemptsPorUsuario(@PathVariable Long userId) {
        return attemptRepository.findByUserIdOrderBySubmissionDate(userId);
    }

    @PutMapping("/{id}")
    public String actualizarAttempt(@PathVariable Long id, @RequestBody EvaluationAttemptDTO dto) {
        Optional<EvaluationAttempt> attemptOpt = attemptRepository.findById(id);
        if (attemptOpt.isEmpty()) {
            return "Intento de evaluación no encontrado";
        }

        EvaluationAttempt attempt = attemptOpt.get();

        // Si se cambia la evaluación, verificar que exista
        if (!attempt.getEvaluation().getId().equals(dto.getEvaluationId())) {
            Optional<Evaluation> evaluationOpt = evaluationRepository.findById(dto.getEvaluationId());
            if (evaluationOpt.isEmpty()) {
                return "Evaluación no encontrada";
            }
            attempt.setEvaluation(evaluationOpt.get());
        }

        // Si se cambia el usuario, verificar que exista
        if (!attempt.getUser().getId().equals(dto.getUserId())) {
            Optional<User> userOpt = userRepository.findById(dto.getUserId());
            if (userOpt.isEmpty()) {
                return "Usuario no encontrado";
            }
            attempt.setUser(userOpt.get());
        }

        attempt.setSubmissionDate(dto.getSubmissionDate());
        attempt.setStatus(EvaluationAttempt.AttemptStatus.valueOf(dto.getStatus()));
        attempt.setScore(dto.getScore());
        attempt.setComments(dto.getComments());

        attemptRepository.save(attempt);
        return "Intento de evaluación actualizado correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarAttempt(@PathVariable Long id) {
        if (attemptRepository.existsById(id)) {
            attemptRepository.deleteById(id);
            return "Intento de evaluación eliminado correctamente";
        } else {
            return "Intento de evaluación no encontrado";
        }
    }
}