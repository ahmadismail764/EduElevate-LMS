package com.eduelevate.lms.repository;

import com.eduelevate.lms.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    
    // Find courses by instructor ID
    List<Course> findByInstructorInstructorId(Integer instructorId);
    
    // Find courses by title containing (case-insensitive search)
    List<Course> findByTitleContainingIgnoreCase(String title);
    
    // Find courses with available spots (enrolled students < max_students)
    @Query("SELECT c FROM Course c WHERE " +
           "(SELECT COUNT(e) FROM Enrollment e WHERE e.course = c AND e.status = 'ACTIVE') < c.maxStudents")
    List<Course> findCoursesWithAvailableSpots();
    
    // Find courses by student enrollment
    @Query("SELECT c FROM Course c JOIN c.enrollments e WHERE e.student.studentId = :studentId AND e.status = 'ACTIVE'")
    List<Course> findCoursesByStudentId(@Param("studentId") Integer studentId);
    
    // Check if student is enrolled in course
    @Query("SELECT COUNT(e) > 0 FROM Enrollment e WHERE e.student.studentId = :studentId AND e.course.courseId = :courseId AND e.status = 'ACTIVE'")
    boolean isStudentEnrolledInCourse(@Param("studentId") Integer studentId, @Param("courseId") Integer courseId);
    
    // Get course with enrollment count
    @Query("SELECT c, COUNT(e) as enrollmentCount FROM Course c LEFT JOIN c.enrollments e WHERE c.courseId = :courseId AND (e.status = 'ACTIVE' OR e.status IS NULL) GROUP BY c")
    Optional<Object[]> findCourseWithEnrollmentCount(@Param("courseId") Integer courseId);
    
    // Find courses by duration range
    List<Course> findByDurationWeeksBetween(Integer minWeeks, Integer maxWeeks);
}
