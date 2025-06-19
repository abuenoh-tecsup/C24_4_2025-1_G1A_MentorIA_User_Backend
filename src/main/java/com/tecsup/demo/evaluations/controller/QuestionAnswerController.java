package com.tecsup.demo.evaluations.controller;

import com.tecsup.demo.evaluations.dto.QuestionAnswerDTO;
import com.tecsup.demo.evaluations.model.QuestionAnswer;
import com.tecsup.demo.evaluations.model.EvaluationAttempt;
import com.tecsup.demo.evaluations.model.Question;
import com.tecsup.demo.evaluations.repository.QuestionAnswerRepository;
import com.tecsup.demo.evaluations.repository.EvaluationAttemptRepository;
import com.tecsup.demo.evaluations.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/question-answers")
public class QuestionAnswerController {

    @Autowired
    private QuestionAnswerRepository answerRepository;

    @Autowired
    private EvaluationAttemptRepository attemptRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @PostMapping
    public ResponseEntity<?> crearAnswer(@RequestBody QuestionAnswerDTO dto) {
        Optional<EvaluationAttempt> attemptOpt = attemptRepository.findById(dto.getAttemptId());
        if (attemptOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Intento de evaluación no encontrado"));
        }

        Optional<Question> questionOpt = questionRepository.findById(dto.getQuestionId());
        if (questionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Pregunta no encontrada"));
        }

        boolean exists = answerRepository.existsByAttemptIdAndQuestionId(dto.getAttemptId(), dto.getQuestionId());
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe una respuesta para esta pregunta en este intento"));
        }

        QuestionAnswer answer = new QuestionAnswer();
        answer.setAnswer(dto.getAnswer());
        answer.setCorrect(dto.getCorrect());
        answer.setAttempt(attemptOpt.get());
        answer.setQuestion(questionOpt.get());

        answerRepository.save(answer);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Respuesta creada correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<QuestionAnswer>> listarAnswers() {
        return ResponseEntity.ok(answerRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerAnswer(@PathVariable Long id) {
        Optional<QuestionAnswer> answerOpt = answerRepository.findById(id);
        if (answerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Respuesta no encontrada"));
        }
        return ResponseEntity.ok(answerOpt.get());
    }

    @GetMapping("/attempt/{attemptId}")
    public ResponseEntity<List<QuestionAnswer>> listarAnswersPorIntento(@PathVariable Long attemptId) {
        List<QuestionAnswer> answers = answerRepository.findByAttemptId(attemptId);
        return ResponseEntity.ok(answers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAnswer(@PathVariable Long id, @RequestBody QuestionAnswerDTO dto) {
        Optional<QuestionAnswer> answerOpt = answerRepository.findById(id);
        if (answerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Respuesta no encontrada"));
        }

        QuestionAnswer answer = answerOpt.get();

        if (!answer.getAttempt().getId().equals(dto.getAttemptId())) {
            Optional<EvaluationAttempt> attemptOpt = attemptRepository.findById(dto.getAttemptId());
            if (attemptOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Intento de evaluación no encontrado"));
            }
            answer.setAttempt(attemptOpt.get());
        }

        if (!answer.getQuestion().getId().equals(dto.getQuestionId())) {
            Optional<Question> questionOpt = questionRepository.findById(dto.getQuestionId());
            if (questionOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Pregunta no encontrada"));
            }
            answer.setQuestion(questionOpt.get());
        }

        answer.setAnswer(dto.getAnswer());
        answer.setCorrect(dto.getCorrect());

        answerRepository.save(answer);

        return ResponseEntity.ok(Map.of("message", "Respuesta actualizada correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAnswer(@PathVariable Long id) {
        if (!answerRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Respuesta no encontrada"));
        }

        answerRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Respuesta eliminada correctamente"));
    }
}
