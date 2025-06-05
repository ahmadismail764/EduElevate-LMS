package com.eduelevate.lms.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @GetMapping("/") //Tested and working
    public String home() {
        return "Welcome to EduElevate LMS! 🚀";
    }
    @GetMapping("/about") //Tested and working
    public String about() {
        return "EduElevate LMS is your go-to platform for learning and development. 🌟\n" +
               "We offer courses like " +
               "Java Programming, Data Science, and more! 📚\n" +
               "Join us to elevate your skills and career! 🚀";
    }
    @GetMapping("/courses") // Tested and working
    public String courseGetter() {
        return "Available Courses:\n" +
               "1. Java Programming\n" +
               "2. Data Science\n" +
               "3. Web Development\n" +
               "4. Machine Learning\n" +
               "5. Cloud Computing\n" +
               "Enroll now to start your learning journey! 🎓";
    }
    @PostMapping("/hello") // Tested and working
    public String helloPost() {
        return "This is another hello page!!\n You sent a request with a post method!!\n Welcome!";
    }
}