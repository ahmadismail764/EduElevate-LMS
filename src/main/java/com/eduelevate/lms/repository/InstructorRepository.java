package com.eduelevate.lms.repository;

import com.eduelevate.lms.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
    
    /*
     * JPA Repository provides these default methods:
     * - save(entity), saveAll(entities)
     * - findById(id), findAll(), findAllById(ids)
     * - existsById(id), count()
     * - deleteById(id), delete(entity), deleteAll()
     * - flush(), saveAndFlush(entity)
     */    // Custom Query Methods
    Optional<Instructor> findByUsername(String username);
    Optional<Instructor> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
