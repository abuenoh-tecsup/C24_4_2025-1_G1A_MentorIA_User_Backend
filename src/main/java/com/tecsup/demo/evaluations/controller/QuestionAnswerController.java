package com.tecsup.demo.evaluations.controller;

import com.tecsup.demo.evaluations.dto.QuestionAnswerDTO;
import com.tecsup.demo.evaluations.model.QuestionAnswer;
import com.tecsup.demo.evaluations.model.EvaluationAttempt;
import com.tecsup.demo.evaluations.model.Question;
import com.tecsup.demo.evaluations.repository.QuestionAnswerRepository;
import com.tecsup.demo.evaluations.repository.EvaluationAttemptRepository;
import com.tecsup.demo.evaluations.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

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
    public String crearAnswer(@RequestBody QuestionAnswerDTO dto) {
        // Verificar si el intento existe
        Optional<EvaluationAttempt> attemptOpt = attemptRepository.findById(dto.getAttemptId());
        if (attemptOpt.isEmpty()) {
            return "Intento de evaluación no encontrado";
        }

        // Verificar si la pregunta existe
        Optional<Question> questionOpt = questionRepository.findById(dto.getQuestionId());
        if (questionOpt.isEmpty()) {
            return "Pregunta no encontrada";
        }

        // Verificar si ya existe una respuesta para esta pregunta en este intento
        if (answerRepository.existsByAttemptIdAndQuestionId(dto.getAttemptId(), dto.getQuestionId())) {
            return "Ya existe una respuesta para esta pregunta en este intento";
        }

        QuestionAnswer answer = new QuestionAnswer();
        answer.setAnswer(dto.getAnswer());
        answer.setCorrect(dto.getCorrect());
        answer.setAttempt(attemptOpt.get());
        answer.setQuestion(questionOpt.get());

        answerRepository.save(answer);
        return "Respuesta creada correctamente";
    }

    @GetMapping
    public List<QuestionAnswer> listarAnswers() {
        return answerRepository.findAll();
    }

    @GetMapping("/{id}")
    public QuestionAnswer obtenerAnswer(@PathVariable Long id) {
        return answerRepository.findById(id).orElse(null);
    }

    @GetMapping("/attempt/{attemptId}")
    public List<QuestionAnswer> listarAnswersPorIntento(@PathVariable Long attemptId) {
        return answerRepository.findByAttemptId(attemptId);
    }

    @PutMapping("/{id}")
    public String actualizarAnswer(@PathVariable Long id, @RequestBody QuestionAnswerDTO dto) {
        Optional<QuestionAnswer> answerOpt = answerRepository.findById(id);
        if (answerOpt.isEmpty()) {
            return "Respuesta no encontrada";
        }

        QuestionAnswer answer = answerOpt.get();

        // Si se cambia el intento, verificar que exista
        if (!answer.getAttempt().getId().equals(dto.getAttemptId())) {
            Optional<EvaluationAttempt> attemptOpt = attemptRepository.findById(dto.getAttemptId());
            if (attemptOpt.isEmpty()) {
                return "Intento de evaluación no encontrado";
            }
            answer.setAttempt(attemptOpt.get());
        }

        // Si se cambia la pregunta, verificar que exista
        if (!answer.getQuestion().getId().equals(dto.getQuestionId())) {
            Optional<Question> questionOpt = questionRepository.findById(dto.getQuestionId());
            if (questionOpt.isEmpty()) {
                return "Pregunta no encontrada";
            }
            answer.setQuestion(questionOpt.get());
        }

        answer.setAnswer(dto.getAnswer());
        answer.setCorrect(dto.getCorrect());

        answerRepository.save(answer);
        return "Respuesta actualizada correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarAnswer(@PathVariable Long id) {
        if (answerRepository.existsById(id)) {
            answerRepository.deleteById(id);
            return "Respuesta eliminada correctamente";
        } else {
            return "Respuesta no encontrada";
        }
    }
}