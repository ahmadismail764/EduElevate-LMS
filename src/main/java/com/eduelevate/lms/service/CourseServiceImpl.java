package com.eduelevate.lms.service;

import com.eduelevate.lms.dto.course.*;
import com.eduelevate.lms.entity.*;
import com.eduelevate.lms.exception.DuplicateResourceException;
import com.eduelevate.lms.exception.ResourceNotFoundException;
import com.eduelevate.lms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseServiceImpl implements CourseService {
    
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;
    
    @Override
    public CourseResponseDto createCourse(CourseCreateDto createDto) {
        log.info("Creating new course: {}", createDto.getTitle());
        
        // Verify instructor exists
        Instructor instructor = instructorRepository.findById(createDto.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with ID: " + createDto.getInstructorId()));
        
        Course course = new Course();
        course.setTitle(createDto.getTitle());
        course.setDescription(createDto.getDescription());
        course.setDurationWeeks(createDto.getDurationWeeks());
        course.setMaxStudents(createDto.getMaxStudents());
        course.setInstructor(instructor);
        
        Course savedCourse = courseRepository.save(course);
        log.info("Course created successfully with ID: {}", savedCourse.getCourseId());
        
        return mapToCourseResponseDto(savedCourse, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CourseResponseDto getCourseById(Integer courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        
        Long enrollmentCount = enrollmentRepository.countActiveByCourseId(courseId);
        return mapToCourseResponseDto(course, enrollmentCount.intValue());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(course -> {
                    Long enrollmentCount = enrollmentRepository.countActiveByCourseId(course.getCourseId());
                    return mapToCourseResponseDto(course, enrollmentCount.intValue());
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponseDto> getAllCourses(Pageable pageable) {
        Page<Course> coursePage = courseRepository.findAll(pageable);
        return coursePage.map(course -> {
            Long enrollmentCount = enrollmentRepository.countActiveByCourseId(course.getCourseId());
            return mapToCourseResponseDto(course, enrollmentCount.intValue());
        });
    }
    
    @Override
    public CourseResponseDto updateCourse(Integer courseId, CourseUpdateDto updateDto) {
        log.info("Updating course with ID: {}", courseId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        
        // Update only non-null fields
        if (updateDto.getTitle() != null) {
            course.setTitle(updateDto.getTitle());
        }
        if (updateDto.getDescription() != null) {
            course.setDescription(updateDto.getDescription());
        }
        if (updateDto.getDurationWeeks() != null) {
            course.setDurationWeeks(updateDto.getDurationWeeks());
        }
        if (updateDto.getMaxStudents() != null) {
            // Ensure max students is not less than current enrollments
            Long currentEnrollments = enrollmentRepository.countActiveByCourseId(courseId);
            if (updateDto.getMaxStudents() < currentEnrollments) {
                throw new IllegalArgumentException("Cannot reduce max students below current enrollment count: " + currentEnrollments);
            }
            course.setMaxStudents(updateDto.getMaxStudents());
        }
        
        Course updatedCourse = courseRepository.save(course);
        Long enrollmentCount = enrollmentRepository.countActiveByCourseId(courseId);
        
        log.info("Course updated successfully: {}", courseId);
        return mapToCourseResponseDto(updatedCourse, enrollmentCount.intValue());
    }
    
    @Override
    public void deleteCourse(Integer courseId) {
        log.info("Deleting course with ID: {}", courseId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        
        courseRepository.delete(course);
        log.info("Course deleted successfully: {}", courseId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDto> getCoursesByInstructor(Integer instructorId) {
        List<Course> courses = courseRepository.findByInstructorInstructorId(instructorId);
        return courses.stream()
                .map(course -> {
                    Long enrollmentCount = enrollmentRepository.countActiveByCourseId(course.getCourseId());
                    return mapToCourseResponseDto(course, enrollmentCount.intValue());
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDto> searchCoursesByTitle(String title) {
        List<Course> courses = courseRepository.findByTitleContainingIgnoreCase(title);
        return courses.stream()
                .map(course -> {
                    Long enrollmentCount = enrollmentRepository.countActiveByCourseId(course.getCourseId());
                    return mapToCourseResponseDto(course, enrollmentCount.intValue());
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDto> getCoursesWithAvailableSpots() {
        List<Course> courses = courseRepository.findCoursesWithAvailableSpots();
        return courses.stream()
                .map(course -> {
                    Long enrollmentCount = enrollmentRepository.countActiveByCourseId(course.getCourseId());
                    return mapToCourseResponseDto(course, enrollmentCount.intValue());
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDto> getCoursesByDuration(Integer minWeeks, Integer maxWeeks) {
        List<Course> courses = courseRepository.findByDurationWeeksBetween(minWeeks, maxWeeks);
        return courses.stream()
                .map(course -> {
                    Long enrollmentCount = enrollmentRepository.countActiveByCourseId(course.getCourseId());
                    return mapToCourseResponseDto(course, enrollmentCount.intValue());
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public EnrollmentResponseDto enrollStudent(Integer courseId, Integer studentId) {
        log.info("Enrolling student {} in course {}", studentId, courseId);
        
        // Verify course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        
        // Verify student exists
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        
        // Check if already enrolled
        if (enrollmentRepository.existsByStudentStudentIdAndCourseCourseId(studentId, courseId)) {
            throw new DuplicateResourceException("Student is already enrolled in this course");
        }
        
        // Check if course has available spots
        Long currentEnrollments = enrollmentRepository.countActiveByCourseId(courseId);
        if (currentEnrollments >= course.getMaxStudents()) {
            throw new IllegalArgumentException("Course is full. No available spots.");
        }
        
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(Enrollment.EnrollmentStatus.ACTIVE);
        
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        log.info("Student enrolled successfully: {} in course {}", studentId, courseId);
        
        return mapToEnrollmentResponseDto(savedEnrollment);
    }
    
    @Override
    public void unenrollStudent(Integer courseId, Integer studentId) {
        log.info("Unenrolling student {} from course {}", studentId, courseId);
        
        Enrollment enrollment = enrollmentRepository.findByStudentStudentIdAndCourseCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for student " + studentId + " in course " + courseId));
        
        enrollment.markAsDropped();
        enrollmentRepository.save(enrollment);
        
        log.info("Student unenrolled successfully: {} from course {}", studentId, courseId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponseDto> getCourseEnrollments(Integer courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseCourseIdAndStatus(courseId, Enrollment.EnrollmentStatus.ACTIVE);
        return enrollments.stream()
                .map(this::mapToEnrollmentResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDto> getStudentCourses(Integer studentId) {
        List<Course> courses = courseRepository.findCoursesByStudentId(studentId);
        return courses.stream()
                .map(course -> {
                    Long enrollmentCount = enrollmentRepository.countActiveByCourseId(course.getCourseId());
                    return mapToCourseResponseDto(course, enrollmentCount.intValue());
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isStudentEnrolled(Integer courseId, Integer studentId) {
        return courseRepository.isStudentEnrolledInCourse(studentId, courseId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Integer getCourseEnrollmentCount(Integer courseId) {
        return enrollmentRepository.countActiveByCourseId(courseId).intValue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Integer getCourseAvailableSpots(Integer courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        
        Long currentEnrollments = enrollmentRepository.countActiveByCourseId(courseId);
        return Math.max(0, course.getMaxStudents() - currentEnrollments.intValue());
    }
    
    // Helper mapping methods
    private CourseResponseDto mapToCourseResponseDto(Course course, Integer currentEnrollments) {
        CourseResponseDto dto = new CourseResponseDto();
        dto.setCourseId(course.getCourseId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setDurationWeeks(course.getDurationWeeks());
        dto.setMaxStudents(course.getMaxStudents());
        dto.setCurrentEnrollments(currentEnrollments);
        dto.setInstructorId(course.getInstructor().getInstructorId());
        dto.setInstructorName(course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName());
        dto.setInstructorEmail(course.getInstructor().getEmail());
        dto.setInstructorDepartment(course.getInstructor().getDepartment());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());
        return dto;
    }
    
    private EnrollmentResponseDto mapToEnrollmentResponseDto(Enrollment enrollment) {
        EnrollmentResponseDto dto = new EnrollmentResponseDto();
        dto.setEnrollmentId(enrollment.getEnrollmentId());
        dto.setStudentId(enrollment.getStudent().getStudentId());
        dto.setStudentName(enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName());
        dto.setStudentEmail(enrollment.getStudent().getEmail());
        dto.setCourseId(enrollment.getCourse().getCourseId());
        dto.setCourseTitle(enrollment.getCourse().getTitle());
        dto.setStatus(enrollment.getStatus());
        dto.setEnrollmentDate(enrollment.getEnrollmentDate());
        return dto;
    }
}
