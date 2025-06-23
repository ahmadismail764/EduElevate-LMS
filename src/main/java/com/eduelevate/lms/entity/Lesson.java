package com.eduelevate.lms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "lesson")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    private Integer lessonId;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "lesson_order", nullable = false)
    private Integer lessonOrder;
    
    @Column(name = "otp", length = 6)
    private String otp;
    
    @Column(name = "otp_expires_at")
    private LocalDateTime otpExpiresAt;
    
    // Many-to-One relationship with Course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Helper method to check if OTP is valid
    public boolean isOtpValid(String providedOtp) {
        return otp != null && 
               otp.equals(providedOtp) && 
               otpExpiresAt != null && 
               otpExpiresAt.isAfter(LocalDateTime.now());
    }
    
    // Helper method to generate new OTP
    public void generateOtp() {
        this.otp = String.format("%06d", (int) (Math.random() * 1000000));
        this.otpExpiresAt = LocalDateTime.now().plusHours(24); // OTP valid for 24 hours
    }
}
