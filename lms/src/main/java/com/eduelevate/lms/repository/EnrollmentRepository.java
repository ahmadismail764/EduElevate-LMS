package com.eduelevate.lms.repository;

import com.eduelevate.lms.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
      // Find enrollment by student and course
    Optional<Enrollment> findByStudentStudentIdAndCourseCourseId(Integer studentId, Integer courseId);
    
    // Find all enrollments for a student
    List<Enrollment> findByStudentStudentId(Integer studentId);
    
    // Find all enrollments for a course
    List<Enrollment> findByCourseCourseId(Integer courseId);
    
    // Find active enrollments for a student
    List<Enrollment> findByStudentStudentIdAndStatus(Integer studentId, Enrollment.EnrollmentStatus status);
    
    // Find active enrollments for a course
    List<Enrollment> findByCourseCourseIdAndStatus(Integer courseId, Enrollment.EnrollmentStatus status);
    
    // Count active enrollments for a course
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.courseId = :courseId AND e.status = 'ACTIVE'")
    Long countActiveByCourseId(@Param("courseId") Integer courseId);
    
    // Check if enrollment exists
    boolean existsByStudentStudentIdAndCourseCourseId(Integer studentId, Integer courseId);
}
