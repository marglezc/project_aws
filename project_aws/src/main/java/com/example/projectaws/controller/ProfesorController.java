package com.example.projectaws.controller;


import com.example.projectaws.model.Profesor;
import com.example.projectaws.repository.InMemoryProfesorRepository;
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
    private InMemoryProfesorRepository repository;

    // GET /profesores
    @GetMapping
    public List<Profesor> getAllProfesores() {
        return repository.findAll(); // 200 OK
    }

    // GET /profesores/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfesorById(@PathVariable int id) {
        Optional<Profesor> profesor = repository.findById(id);
        if (profesor.isPresent()) {
            return ResponseEntity.ok(profesor.get()); // 200 OK
        } else {
            // 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Profesor no encontrado"));
        }
    }

    // POST /profesores
    @PostMapping
    public ResponseEntity<?> createProfesor(@Valid @RequestBody Profesor profesor) {
        if (repository.existsById(profesor.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400
                    .body(Map.of("error", "El ID " + profesor.getId() + " ya existe."));
        }

        Profesor nuevoProfesor = repository.save(profesor);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProfesor); // 201 Created
    }

    // PUT /profesores/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfesor(@PathVariable int id, @Valid @RequestBody Profesor profesorDetails) {
        if (profesorDetails.getId() != id) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400
                    .body(Map.of("error", "El ID de la URL (" + id + ") no coincide con el ID del cuerpo (" + profesorDetails.getId() + ")."));
        }

        // Validar que exista para actualizar
        if (!repository.existsById(id)) {
            // 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Profesor no encontrado")); //
        }

        Profesor updatedProfesor = repository.save(profesorDetails);
        return ResponseEntity.ok(updatedProfesor); // 200 OK
    }

    // DELETE /profesores/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfesor(@PathVariable int id) {
        if (repository.deleteById(id)) {
            // 200 OK
            return ResponseEntity.ok(Map.of("message", "Profesor eliminado exitosamente"));
        } else {
            // 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Profesor no encontrado"));
        }
    }
}