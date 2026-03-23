package com.examples.studentmanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing   // 🔥 THIS LINE IS CRITICAL
public class StudentmanagementsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentmanagementsystemApplication.class, args);
    }
}