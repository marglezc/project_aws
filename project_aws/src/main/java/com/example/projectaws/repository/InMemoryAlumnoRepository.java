package com.example.projectaws.repository;

import com.example.projectaws.model.Alumno;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryAlumnoRepository {

    private final Map<Integer, Alumno> students = new ConcurrentHashMap<>();

    public List<Alumno> findAll() {
        return new ArrayList<>(students.values());
    }

    public Optional<Alumno> findById(int id) {
        return Optional.ofNullable(students.get(id));
    }

    public Alumno save(Alumno alumno) {
        students.put(alumno.getId(), alumno);
        return alumno;
    }

    public boolean deleteById(int id) {
        if (students.containsKey(id)) {
            students.remove(id);
            return true;
        }
        return false;
    }

    public boolean existsById(int id) {
        return students.containsKey(id);
    }
}