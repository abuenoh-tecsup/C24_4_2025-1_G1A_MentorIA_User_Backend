package com.tecsup.demo.academic.controller;

import com.tecsup.demo.academic.dto.CareerDTO;
import com.tecsup.demo.academic.model.Career;
import com.tecsup.demo.academic.repository.CareerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/careers")
public class CareerController {

    @Autowired
    private CareerRepository careerRepository;

    @PostMapping
    public String crearCareer(@RequestBody CareerDTO dto) {
        Career career = new Career();
        career.setCode(dto.getCode());
        career.setName(dto.getName());

        careerRepository.save(career);
        return "Carrera creada correctamente";
    }

    @GetMapping
    public List<Career> listarCareers() {
        return careerRepository.findAll();
    }

    @GetMapping("/{id}")
    public Career obtenerCareer(@PathVariable Long id) {
        return careerRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public String actualizarCareer(@PathVariable Long id, @RequestBody CareerDTO dto) {
        Optional<Career> careerOpt = careerRepository.findById(id);
        if (careerOpt.isEmpty()) {
            return "Carrera no encontrada";
        }

        Career career = careerOpt.get();
        career.setCode(dto.getCode());
        career.setName(dto.getName());

        careerRepository.save(career);
        return "Carrera actualizada correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarCareer(@PathVariable Long id) {
        if (careerRepository.existsById(id)) {
            careerRepository.deleteById(id);
            return "Carrera eliminada correctamente";
        } else {
            return "Carrera no encontrada";
        }
    }
}