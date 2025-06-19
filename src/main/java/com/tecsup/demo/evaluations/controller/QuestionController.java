package com.tecsup.demo.evaluations.controller;

import com.tecsup.demo.evaluations.dto.QuestionDTO;
import com.tecsup.demo.evaluations.model.Question;
import com.tecsup.demo.evaluations.model.Evaluation;
import com.tecsup.demo.evaluations.repository.QuestionRepository;
import com.tecsup.demo.evaluations.repository.EvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @PostMapping
    public ResponseEntity<?> crearQuestion(@RequestBody QuestionDTO dto) {
        Optional<Evaluation> evaluationOpt = evaluationRepository.findById(dto.getEvaluationId());
        if (evaluationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Evaluación no encontrada"));
        }

        Question question = new Question();
        question.setStatement(dto.getStatement());
        question.setType(Question.QuestionType.valueOf(dto.getType()));
        question.setOptions(dto.getOptions());
        question.setCorrectAnswer(dto.getCorrectAnswer());
        question.setEvaluation(evaluationOpt.get());

        questionRepository.save(question);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Pregunta creada correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<Question>> listarQuestions() {
        return ResponseEntity.ok(questionRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerQuestion(@PathVariable Long id) {
        Optional<Question> questionOpt = questionRepository.findById(id);
        if (questionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Pregunta no encontrada"));
        }
        return ResponseEntity.ok(questionOpt.get());
    }

    @GetMapping("/evaluation/{evaluationId}")
    public ResponseEntity<List<Question>> listarQuestionsPorEvaluacion(@PathVariable Long evaluationId) {
        return ResponseEntity.ok(questionRepository.findByEvaluationIdOrderById(evaluationId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarQuestion(@PathVariable Long id, @RequestBody QuestionDTO dto) {
        Optional<Question> questionOpt = questionRepository.findById(id);
        if (questionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Pregunta no encontrada"));
        }

        Question question = questionOpt.get();

        if (!question.getEvaluation().getId().equals(dto.getEvaluationId())) {
            Optional<Evaluation> evaluationOpt = evaluationRepository.findById(dto.getEvaluationId());
            if (evaluationOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Evaluación no encontrada"));
            }
            question.setEvaluation(evaluationOpt.get());
        }

        question.setStatement(dto.getStatement());
        question.setType(Question.QuestionType.valueOf(dto.getType()));
        question.setOptions(dto.getOptions());
        question.setCorrectAnswer(dto.getCorrectAnswer());

        questionRepository.save(question);

        return ResponseEntity.ok(Map.of("message", "Pregunta actualizada correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarQuestion(@PathVariable Long id) {
        if (!questionRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Pregunta no encontrada"));
        }

        questionRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Pregunta eliminada correctamente"));
    }
}
