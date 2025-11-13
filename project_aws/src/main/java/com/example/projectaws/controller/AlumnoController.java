package com.example.projectaws.controller;

import com.example.projectaws.model.Alumno;
import com.example.projectaws.repository.InMemoryAlumnoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/alumnos")
public class AlumnoController {

    @Autowired
    private InMemoryAlumnoRepository repository;

    // GET /alumnos
    @GetMapping
    public List<Alumno> getAllAlumnos() {
        return repository.findAll();
    }

    // GET /alumnos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getAlumnoById(@PathVariable int id) {
        Optional<Alumno> alumno = repository.findById(id);
        if (alumno.isPresent()) {
            return ResponseEntity.ok(alumno.get());
        } else {
            // 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Alumno no encontrado"));
        }
    }

    // POST /alumnos
    @PostMapping //
    public ResponseEntity<?> createAlumno(@Valid @RequestBody Alumno alumno) {
        if (repository.existsById(alumno.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400
                    .body(Map.of("error", "El ID " + alumno.getId() + " ya existe."));
        }

        Alumno nuevoAlumno = repository.save(alumno);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAlumno); // 201 Created
    }

    // PUT /alumnos/{id}
    @PutMapping("/{id}") //
    public ResponseEntity<?> updateAlumno(@PathVariable int id, @Valid @RequestBody Alumno alumnoDetails) {
        if (alumnoDetails.getId() != id) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400
                    .body(Map.of("error", "El ID de la URL (" + id + ") no coincide con el ID del cuerpo (" + alumnoDetails.getId() + ")."));
        }

        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND) // 404
                    .body(Map.of("error", "Alumno no encontrado"));
        }

        Alumno updatedAlumno = repository.save(alumnoDetails);
        return ResponseEntity.ok(updatedAlumno); // 200 OK
    }

    // DELETE /alumnos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlumno(@PathVariable int id) {
        if (repository.deleteById(id)) {
            return ResponseEntity.ok(Map.of("message", "Alumno eliminado exitosamente")); // 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND) // 404
                    .body(Map.of("error", "Alumno no encontrado"));
        }
    }
}
