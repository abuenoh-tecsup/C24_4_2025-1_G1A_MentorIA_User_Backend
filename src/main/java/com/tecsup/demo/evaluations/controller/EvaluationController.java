package com.tecsup.demo.evaluations.controller;

import com.tecsup.demo.evaluations.dto.EvaluationDTO;
import com.tecsup.demo.evaluations.model.Evaluation;
import com.tecsup.demo.evaluations.repository.EvaluationRepository;
import com.tecsup.demo.courses.model.Module;
import com.tecsup.demo.courses.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @PostMapping
    public String crearEvaluation(@RequestBody EvaluationDTO dto) {
        // Verificar si el módulo existe
        Optional<Module> moduleOpt = moduleRepository.findById(dto.getModuleId());
        if (moduleOpt.isEmpty()) {
            return "Módulo no encontrado";
        }

        // Verificar si ya existe una evaluación con el mismo título en el módulo
        if (evaluationRepository.existsByModuleIdAndTitle(dto.getModuleId(), dto.getTitle())) {
            return "Ya existe una evaluación con el título '" + dto.getTitle() + "' en este módulo";
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setTitle(dto.getTitle());
        evaluation.setDescription(dto.getDescription());
        evaluation.setStartDate(dto.getStartDate());
        evaluation.setEndDate(dto.getEndDate());
        evaluation.setTimeLimit(dto.getTimeLimit());
        evaluation.setModule(moduleOpt.get());

        evaluationRepository.save(evaluation);
        return "Evaluación creada correctamente";
    }

    @GetMapping
    public List<Evaluation> listarEvaluations() {
        return evaluationRepository.findAll();
    }

    @GetMapping("/{id}")
    public Evaluation obtenerEvaluation(@PathVariable Long id) {
        return evaluationRepository.findById(id).orElse(null);
    }

    @GetMapping("/module/{moduleId}")
    public List<Evaluation> listarEvaluationsPorModulo(@PathVariable Long moduleId) {
        return evaluationRepository.findByModuleIdOrderByStartDate(moduleId);
    }

    @PutMapping("/{id}")
    public String actualizarEvaluation(@PathVariable Long id, @RequestBody EvaluationDTO dto) {
        Optional<Evaluation> evaluationOpt = evaluationRepository.findById(id);
        if (evaluationOpt.isEmpty()) {
            return "Evaluación no encontrada";
        }

        Evaluation evaluation = evaluationOpt.get();

        // Si se cambia el módulo, verificar que exista
        if (!evaluation.getModule().getId().equals(dto.getModuleId())) {
            Optional<Module> moduleOpt = moduleRepository.findById(dto.getModuleId());
            if (moduleOpt.isEmpty()) {
                return "Módulo no encontrado";
            }
            evaluation.setModule(moduleOpt.get());
        }

        // Verificar si el nuevo título entra en conflicto con otra evaluación del módulo
        if (!evaluation.getTitle().equals(dto.getTitle()) &&
                evaluationRepository.existsByModuleIdAndTitle(dto.getModuleId(), dto.getTitle())) {
            return "Ya existe una evaluación con el título '" + dto.getTitle() + "' en este módulo";
        }

        evaluation.setTitle(dto.getTitle());
        evaluation.setDescription(dto.getDescription());
        evaluation.setStartDate(dto.getStartDate());
        evaluation.setEndDate(dto.getEndDate());
        evaluation.setTimeLimit(dto.getTimeLimit());

        evaluationRepository.save(evaluation);
        return "Evaluación actualizada correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarEvaluation(@PathVariable Long id) {
        if (evaluationRepository.existsById(id)) {
            evaluationRepository.deleteById(id);
            return "Evaluación eliminada correctamente";
        } else {
            return "Evaluación no encontrada";
        }
    }
}