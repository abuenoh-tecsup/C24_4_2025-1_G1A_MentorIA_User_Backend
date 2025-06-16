package com.tecsup.demo.evaluations.controller;

import com.tecsup.demo.evaluations.dto.QuestionDTO;
import com.tecsup.demo.evaluations.model.Question;
import com.tecsup.demo.evaluations.model.Evaluation;
import com.tecsup.demo.evaluations.repository.QuestionRepository;
import com.tecsup.demo.evaluations.repository.EvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @PostMapping
    public String crearQuestion(@RequestBody QuestionDTO dto) {
        // Verificar si la evaluación existe
        Optional<Evaluation> evaluationOpt = evaluationRepository.findById(dto.getEvaluationId());
        if (evaluationOpt.isEmpty()) {
            return "Evaluación no encontrada";
        }

        Question question = new Question();
        question.setStatement(dto.getStatement());
        question.setType(Question.QuestionType.valueOf(dto.getType()));
        question.setOptions(dto.getOptions());
        question.setCorrectAnswer(dto.getCorrectAnswer());
        question.setEvaluation(evaluationOpt.get());

        questionRepository.save(question);
        return "Pregunta creada correctamente";
    }

    @GetMapping
    public List<Question> listarQuestions() {
        return questionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Question obtenerQuestion(@PathVariable Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    @GetMapping("/evaluation/{evaluationId}")
    public List<Question> listarQuestionsPorEvaluacion(@PathVariable Long evaluationId) {
        return questionRepository.findByEvaluationIdOrderById(evaluationId);
    }

    @PutMapping("/{id}")
    public String actualizarQuestion(@PathVariable Long id, @RequestBody QuestionDTO dto) {
        Optional<Question> questionOpt = questionRepository.findById(id);
        if (questionOpt.isEmpty()) {
            return "Pregunta no encontrada";
        }

        Question question = questionOpt.get();

        // Si se cambia la evaluación, verificar que exista
        if (!question.getEvaluation().getId().equals(dto.getEvaluationId())) {
            Optional<Evaluation> evaluationOpt = evaluationRepository.findById(dto.getEvaluationId());
            if (evaluationOpt.isEmpty()) {
                return "Evaluación no encontrada";
            }
            question.setEvaluation(evaluationOpt.get());
        }

        question.setStatement(dto.getStatement());
        question.setType(Question.QuestionType.valueOf(dto.getType()));
        question.setOptions(dto.getOptions());
        question.setCorrectAnswer(dto.getCorrectAnswer());

        questionRepository.save(question);
        return "Pregunta actualizada correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarQuestion(@PathVariable Long id) {
        if (questionRepository.existsById(id)) {
            questionRepository.deleteById(id);
            return "Pregunta eliminada correctamente";
        } else {
            return "Pregunta no encontrada";
        }
    }
}