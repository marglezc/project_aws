package com.example.projectaws.controller;

import com.example.projectaws.model.Profesor;
import com.example.projectaws.repository.ProfesorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/profesores")
public class ProfesorController {

    @Autowired
    private ProfesorRepository repository;

    @GetMapping
    public List<Profesor> getAllProfesores() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfesorById(@PathVariable int id) {
        Optional<Profesor> profesor = repository.findById(id);

        if (profesor.isPresent()) {
            return ResponseEntity.ok(profesor.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Profesor no encontrado"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createProfesor(@Valid @RequestBody Profesor profesor) {
        Profesor nuevo = repository.save(profesor);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfesor(@PathVariable int id, @Valid @RequestBody Profesor det) {
        Optional<Profesor> opt = repository.findById(id);

        if (opt.isPresent()) {
            Profesor p = opt.get();
            p.setNombres(det.getNombres());
            p.setApellidos(det.getApellidos());
            p.setNumeroEmpleado(det.getNumeroEmpleado());
            p.setHorasClase(det.getHorasClase());
            repository.save(p);
            return ResponseEntity.ok(p);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Profesor no encontrado"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfesor(@PathVariable int id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Profesor eliminado exitosamente"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Profesor no encontrado"));
        }
    }
}