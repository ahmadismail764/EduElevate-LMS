package com.eduelevate.lms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Integer enrollmentId;
    
    // Many-to-One relationship with Student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    // Many-to-One relationship with Course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;
    
    @CreationTimestamp
    @Column(name = "enrollment_date", nullable = false, updatable = false)
    private LocalDateTime enrollmentDate;
    
    public enum EnrollmentStatus {
        ACTIVE, COMPLETED, DROPPED
    }
    
    // Helper methods
    public void markAsCompleted() {
        this.status = EnrollmentStatus.COMPLETED;
    }
    
    public void markAsDropped() {
        this.status = EnrollmentStatus.DROPPED;
    }
    
    public boolean isActive() {
        return this.status == EnrollmentStatus.ACTIVE;
    }
}
