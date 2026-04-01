package com.examples.studentmanagementsystem.controller;

import com.examples.studentmanagementsystem.dto.request.EnrollmentRequest;
import com.examples.studentmanagementsystem.dto.response.EnrollmentResponse;
import com.examples.studentmanagementsystem.entity.Course;
import com.examples.studentmanagementsystem.entity.Enrollment;
import com.examples.studentmanagementsystem.entity.Student;
import com.examples.studentmanagementsystem.mapper.EnrollmentMapper;
import com.examples.studentmanagementsystem.service.CourseService;
import com.examples.studentmanagementsystem.service.EnrollmentService;
import com.examples.studentmanagementsystem.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Enrollment API", description = "Operations related to enrollments")
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final CourseService courseService;

    public EnrollmentController(EnrollmentService enrollmentService,
                                StudentService studentService,
                                CourseService courseService) {
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    @Operation(summary = "Create enrollment")
    @PostMapping
    public EnrollmentResponse createEnrollment(@RequestBody @Valid EnrollmentRequest request) {

        Student student = studentService.getStudentById(request.getStudentId());
        Course course = courseService.getCourseById(request.getCourseId());

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentDate(request.getEnrollmentDate());
        enrollment.setStatus(request.getStatus());
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        return EnrollmentMapper.toResponse(enrollmentService.createEnrollment(enrollment));
    }

    @Operation(summary = "Get current student's enrollments")
    @GetMapping("/my")
    public List<EnrollmentResponse> getMyEnrollments() {
        return enrollmentService.getMyEnrollments()
                .stream()
                .map(EnrollmentMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Update enrollment")
    @PutMapping("/{id}")
    public EnrollmentResponse updateEnrollment(@PathVariable Long id,
                                               @RequestBody @Valid EnrollmentRequest request) {

        Student student = studentService.getStudentById(request.getStudentId());
        Course course = courseService.getCourseById(request.getCourseId());

        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentDate(request.getEnrollmentDate());
        enrollment.setStatus(request.getStatus());
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        return EnrollmentMapper.toResponse(enrollmentService.updateEnrollment(id, enrollment));
    }

    @Operation(summary = "Get enrollment by ID")
    @GetMapping("/{id}")
    public EnrollmentResponse getEnrollmentById(@PathVariable Long id) {
        return EnrollmentMapper.toResponse(enrollmentService.getEnrollmentById(id));
    }

    @Operation(summary = "Get all enrollments")
    @GetMapping
    public List<EnrollmentResponse> getAllEnrollments() {
        return enrollmentService.getAllEnrollments()
                .stream()
                .map(EnrollmentMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Delete enrollment")
    @DeleteMapping("/{id}")
    public String deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return "Enrollment deleted successfully";
    }

    @Operation(summary = "Get enrollments by student ID")
    @GetMapping("/student/{studentId}")
    public List<EnrollmentResponse> getEnrollmentsByStudentId(@PathVariable Long studentId) {
        return enrollmentService.getEnrollmentsByStudentId(studentId)
                .stream()
                .map(EnrollmentMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Get enrollments by course ID")
    @GetMapping("/course/{courseId}")
    public List<EnrollmentResponse> getEnrollmentsByCourseId(@PathVariable Long courseId) {
        return enrollmentService.getEnrollmentsByCourseId(courseId)
                .stream()
                .map(EnrollmentMapper::toResponse)
                .toList();
    }
}