package com.eduelevate.lms.service;
import com.eduelevate.lms.repository;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;
    List<Student> getAllStudents(){
        return studentRepository.findAll();
    }
}