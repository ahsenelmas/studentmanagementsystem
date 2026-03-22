package com.examples.studentmanagementsystem.service;

import com.examples.studentmanagementsystem.entity.Student;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StudentService {

    Student createStudent(Student student);

    Student getStudentById(Long id);

    List<Student> getAllStudents();

    Student updateStudent(Long id, Student student);

    void deleteStudent(Long id);

    List<Student> getStudentsByDepartment(String department);

    List<Student> searchStudentsByName(String keyword);

    Page<Student> getStudentsPaginated(int page, int size, String sortBy, String direction);
}