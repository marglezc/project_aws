package com.example.projectaws.repository;

import com.example.projectaws.model.Profesor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryProfesorRepository {

    private final Map<Integer, Profesor> teachers = new ConcurrentHashMap<>();

    public List<Profesor> findAll() {
        return new ArrayList<>(teachers.values());
    }

    public Optional<Profesor> findById(int id) {
        return Optional.ofNullable(teachers.get(id));
    }

    public Profesor save(Profesor profesor) {
        teachers.put(profesor.getId(), profesor);
        return profesor;
    }

    public boolean deleteById(int id) {
        if (teachers.containsKey(id)) {
            teachers.remove(id);
            return true;
        }
        return false;
    }

    public boolean existsById(int id) {
        return teachers.containsKey(id);
    }
}