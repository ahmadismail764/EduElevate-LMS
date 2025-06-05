package com.eduelevate.lms.repository;
import com.eduelevate.lms.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    /* 
     * AUTOMATICALLY PROVIDED METHODS:
     * FIND/READ OPERATIONS:
     * - findAll() - Get all students
     * - findById(Integer id) - Find student by ID
     * - findAllById(List<Integer> ids) - Find multiple students by IDs
     * 
     * SAVE/CREATE/UPDATE OPERATIONS:
     * - save(Student student) - Create or update student
     * - saveAll(List<Student> students) - Save multiple students
     * 
     * DELETE OPERATIONS:
     * - delete(Student student) - Delete a student object
     * - deleteById(Integer id) - Delete by ID
     * - deleteAll() - Delete all students
     * - deleteAllById(List<Integer> ids) - Delete multiple by IDs
     * 
     * COUNT/CHECK OPERATIONS:
     * - count() - Count total students     * - existsById(Integer id) - Check if student exists
     */
    
    // Custom Query Methods
    Optional<Student> findByEmail(String email);
    boolean existsByEmail(String email);
}