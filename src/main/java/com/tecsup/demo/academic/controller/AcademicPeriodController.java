package com.tecsup.demo.academic.controller;

import com.tecsup.demo.academic.dto.AcademicPeriodDTO;
import com.tecsup.demo.academic.model.AcademicPeriod;
import com.tecsup.demo.academic.repository.AcademicPeriodRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/academic-periods")
public class AcademicPeriodController {

    @Autowired
    private AcademicPeriodRepository academicPeriodRepository;

    @PostMapping
    public ResponseEntity<?> crearAcademicPeriod(@RequestBody AcademicPeriodDTO dto) {
        Optional<AcademicPeriod> existingPeriod = academicPeriodRepository
                .findByYearAndTerm(dto.getYear(), dto.getTerm());

        if (existingPeriod.isPresent()) {
            String mensaje = "Ya existe un período académico para el año " + dto.getYear() +
                    " y término " + dto.getTerm();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", mensaje));
        }

        AcademicPeriod academicPeriod = new AcademicPeriod();
        academicPeriod.setName(dto.getName());
        academicPeriod.setYear(dto.getYear());
        academicPeriod.setTerm(dto.getTerm());
        academicPeriod.setStartDate(dto.getStartDate());
        academicPeriod.setEndDate(dto.getEndDate());

        academicPeriodRepository.save(academicPeriod);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Período académico creado correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<AcademicPeriod>> listarAcademicPeriods() {
        List<AcademicPeriod> periods = academicPeriodRepository.findAll();
        return ResponseEntity.ok(periods);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerAcademicPeriod(@PathVariable Long id) {
        Optional<AcademicPeriod> academicPeriodOpt = academicPeriodRepository.findById(id);

        if (academicPeriodOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Período académico no encontrado"));
        }

        return ResponseEntity.ok(academicPeriodOpt.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAcademicPeriod(@PathVariable Long id,
                                                      @RequestBody AcademicPeriodDTO dto) {

        Optional<AcademicPeriod> academicPeriodOpt = academicPeriodRepository.findById(id);

        if (academicPeriodOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Período académico no encontrado"));
        }

        AcademicPeriod academicPeriod = academicPeriodOpt.get();

        if (!academicPeriod.getYear().equals(dto.getYear()) ||
                !academicPeriod.getTerm().equals(dto.getTerm())) {

            Optional<AcademicPeriod> existingPeriod = academicPeriodRepository
                    .findByYearAndTerm(dto.getYear(), dto.getTerm());

            if (existingPeriod.isPresent()) {
                String mensaje = "Ya existe un período académico para el año " + dto.getYear() +
                        " y término " + dto.getTerm();
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", mensaje));
            }
        }

        academicPeriod.setName(dto.getName());
        academicPeriod.setYear(dto.getYear());
        academicPeriod.setTerm(dto.getTerm());
        academicPeriod.setStartDate(dto.getStartDate());
        academicPeriod.setEndDate(dto.getEndDate());

        academicPeriodRepository.save(academicPeriod);

        return ResponseEntity.ok(Map.of("message", "Período académico actualizado correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAcademicPeriod(@PathVariable Long id) {
        if (!academicPeriodRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Período académico no encontrado"));
        }

        academicPeriodRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Período académico eliminado correctamente"));
    }
}
