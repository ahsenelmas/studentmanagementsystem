package com.examples.studentmanagementsystem.service.impl;

import com.examples.studentmanagementsystem.entity.Student;
import com.examples.studentmanagementsystem.exception.DuplicateResourceException;
import com.examples.studentmanagementsystem.exception.ResourceNotFoundException;
import com.examples.studentmanagementsystem.repository.StudentRepository;
import com.examples.studentmanagementsystem.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student createStudent(Student student) {
        if (studentRepository.existsByStudentNumber(student.getStudentNumber())) {
            throw new DuplicateResourceException("Student number already exists: " + student.getStudentNumber());
        }
        return studentRepository.save(student);
    }

    @Override
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student updateStudent(Long id, Student student) {
        Student existingStudent = getStudentById(id);

        existingStudent.setFirstName(student.getFirstName());
        existingStudent.setLastName(student.getLastName());
        existingStudent.setBirthDate(student.getBirthDate());
        existingStudent.setPhone(student.getPhone());
        existingStudent.setDepartment(student.getDepartment());

        if (!existingStudent.getStudentNumber().equals(student.getStudentNumber())
                && studentRepository.existsByStudentNumber(student.getStudentNumber())) {
            throw new DuplicateResourceException("Student number already exists: " + student.getStudentNumber());
        }

        existingStudent.setStudentNumber(student.getStudentNumber());

        return studentRepository.save(existingStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        studentRepository.delete(student);
    }

    @Override
    public List<Student> getStudentsByDepartment(String department) {
        return studentRepository.findByDepartment(department);
    }

    @Override
    public List<Student> searchStudentsByName(String keyword) {
        return studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword);
    }

    @Override
    public Page<Student> getStudentsPaginated(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return studentRepository.findAll(pageable);
    }
}