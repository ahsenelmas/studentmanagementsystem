package com.examples.studentmanagementsystem.controller;

import com.examples.studentmanagementsystem.dto.request.StudentRequest;
import com.examples.studentmanagementsystem.dto.response.StudentResponse;
import com.examples.studentmanagementsystem.mapper.StudentMapper;
import com.examples.studentmanagementsystem.entity.Student;
import com.examples.studentmanagementsystem.service.StudentService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@Tag(name = "Student API", description = "Operations related to students")
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(summary = "Create a new student")
    @PostMapping
    public StudentResponse createStudent(@RequestBody @Valid StudentRequest request) {
        Student student = StudentMapper.toEntity(request);
        Student saved = studentService.createStudent(student);
        return StudentMapper.toResponse(saved);
    }

    @Operation(summary = "Get student by ID")
    @GetMapping("/{id}")
    public StudentResponse getStudentById(@PathVariable Long id) {
        return StudentMapper.toResponse(studentService.getStudentById(id));
    }

    @Operation(summary = "Get all students")
    @GetMapping
    public List<StudentResponse> getAllStudents() {
        return studentService.getAllStudents()
                .stream()
                .map(StudentMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Update student by ID")
    @PutMapping("/{id}")
    public StudentResponse updateStudent(@PathVariable Long id, @RequestBody @Valid StudentRequest request) {
        Student student = StudentMapper.toEntity(request);
        return StudentMapper.toResponse(studentService.updateStudent(id, student));
    }

    @Operation(summary = "Delete student by ID")
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "Student deleted successfully with id: " + id;
    }

    @Operation(summary = "Get students by department")
    @GetMapping("/department/{department}")
    public List<StudentResponse> getStudentsByDepartment(@PathVariable String department) {
        return studentService.getStudentsByDepartment(department)
                .stream()
                .map(StudentMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Search students by name")
    @GetMapping("/search")
    public List<StudentResponse> searchStudentsByName(@RequestParam String keyword) {
        return studentService.searchStudentsByName(keyword)
                .stream()
                .map(StudentMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Get students with pagination and sorting")
    @GetMapping("/paged")
    public Page<StudentResponse> getStudentsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return studentService.getStudentsPaginated(page, size, sortBy, direction)
                .map(StudentMapper::toResponse);
    }
}