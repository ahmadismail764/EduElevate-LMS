package com.eduelevate.lms.repository;
import com.eduelevate.lms.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

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
     * - count() - Count total students
     * - existsById(Integer id) - Check if student exists
     */
    Student findByEmail(String email);
    List<Student> findByRole(String role);
    boolean existsByEmail(String email);
}