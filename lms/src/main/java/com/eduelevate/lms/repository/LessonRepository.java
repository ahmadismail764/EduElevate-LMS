package com.eduelevate.lms.repository;

import com.eduelevate.lms.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    
    // Find lessons by course ID ordered by lesson order
    List<Lesson> findByCourseCourseIdOrderByLessonOrder(Integer courseId);
    
    // Find lesson by course ID and lesson order
    Lesson findByCourseCourseIdAndLessonOrder(Integer courseId, Integer lessonOrder);
    
    // Count lessons in a course
    Long countByCourseCourseId(Integer courseId);
    
    // Find lessons with valid OTP
    @Query("SELECT l FROM Lesson l WHERE l.otp = :otp AND l.otpExpiresAt > CURRENT_TIMESTAMP")
    List<Lesson> findByValidOtp(@Param("otp") String otp);
    
    // Find next lesson order for a course
    @Query("SELECT COALESCE(MAX(l.lessonOrder), 0) + 1 FROM Lesson l WHERE l.course.courseId = :courseId")
    Integer findNextLessonOrder(@Param("courseId") Integer courseId);
}
