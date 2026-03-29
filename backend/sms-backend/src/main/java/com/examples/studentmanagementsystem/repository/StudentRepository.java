package com.examples.studentmanagementsystem.repository;

import com.examples.studentmanagementsystem.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentNumber(String studentNumber);

    boolean existsByStudentNumber(String studentNumber);

    List<Student> findByDepartment(String department);

    List<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}