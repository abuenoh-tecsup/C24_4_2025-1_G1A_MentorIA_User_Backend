package com.tecsup.demo.academic.controller;

import com.tecsup.demo.academic.dto.AcademicPeriodDTO;
import com.tecsup.demo.academic.model.AcademicPeriod;
import com.tecsup.demo.academic.repository.AcademicPeriodRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/academic-periods")
public class AcademicPeriodController {

    @Autowired
    private AcademicPeriodRepository academicPeriodRepository;

    @PostMapping
    public String crearAcademicPeriod(@RequestBody AcademicPeriodDTO dto) {
        // Verificar si ya existe un período con el mismo año y término
        Optional<AcademicPeriod> existingPeriod = academicPeriodRepository
                .findByYearAndTerm(dto.getYear(), dto.getTerm());

        if (existingPeriod.isPresent()) {
            return "Ya existe un período académico para el año " + dto.getYear() +
                    " y término " + dto.getTerm();
        }

        AcademicPeriod academicPeriod = new AcademicPeriod();
        academicPeriod.setName(dto.getName());
        academicPeriod.setYear(dto.getYear());
        academicPeriod.setTerm(dto.getTerm());
        academicPeriod.setStartDate(dto.getStartDate());
        academicPeriod.setEndDate(dto.getEndDate());

        academicPeriodRepository.save(academicPeriod);
        return "Período académico creado correctamente";
    }

    @GetMapping
    public List<AcademicPeriod> listarAcademicPeriods() {
        return academicPeriodRepository.findAll();
    }

    @GetMapping("/{id}")
    public AcademicPeriod obtenerAcademicPeriod(@PathVariable Long id) {
        return academicPeriodRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public String actualizarAcademicPeriod(@PathVariable Long id, @RequestBody AcademicPeriodDTO dto) {
        Optional<AcademicPeriod> academicPeriodOpt = academicPeriodRepository.findById(id);
        if (academicPeriodOpt.isEmpty()) {
            return "Período académico no encontrado";
        }

        AcademicPeriod academicPeriod = academicPeriodOpt.get();

        // Verificar si el cambio de año/término entra en conflicto con otro período
        if (!academicPeriod.getYear().equals(dto.getYear()) ||
                !academicPeriod.getTerm().equals(dto.getTerm())) {

            Optional<AcademicPeriod> existingPeriod = academicPeriodRepository
                    .findByYearAndTerm(dto.getYear(), dto.getTerm());

            if (existingPeriod.isPresent()) {
                return "Ya existe un período académico para el año " + dto.getYear() +
                        " y término " + dto.getTerm();
            }
        }

        academicPeriod.setName(dto.getName());
        academicPeriod.setYear(dto.getYear());
        academicPeriod.setTerm(dto.getTerm());
        academicPeriod.setStartDate(dto.getStartDate());
        academicPeriod.setEndDate(dto.getEndDate());

        academicPeriodRepository.save(academicPeriod);
        return "Período académico actualizado correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarAcademicPeriod(@PathVariable Long id) {
        if (academicPeriodRepository.existsById(id)) {
            academicPeriodRepository.deleteById(id);
            return "Período académico eliminado correctamente";
        } else {
            return "Período académico no encontrado";
        }
    }
}