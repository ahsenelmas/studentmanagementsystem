package com.examples.studentmanagementsystem.controller;

import com.examples.studentmanagementsystem.dto.request.CourseRequest;
import com.examples.studentmanagementsystem.dto.response.CourseResponse;
import com.examples.studentmanagementsystem.entity.Course;
import com.examples.studentmanagementsystem.mapper.CourseMapper;
import com.examples.studentmanagementsystem.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@Tag(name = "Course API", description = "Operations related to courses")
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(summary = "Create a new course")
    @PostMapping
    public CourseResponse createCourse(@RequestBody @Valid CourseRequest request) {
        Course course = CourseMapper.toEntity(request);
        return CourseMapper.toResponse(courseService.createCourse(course));
    }

    @Operation(summary = "Get current student's courses")
    @GetMapping("/my")
    public List<CourseResponse> getMyCourses() {
        return courseService.getMyCourses()
                .stream()
                .map(CourseMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Get course by ID")
    @GetMapping("/{id}")
    public CourseResponse getCourseById(@PathVariable Long id) {
        return CourseMapper.toResponse(courseService.getCourseById(id));
    }

    @Operation(summary = "Get all courses")
    @GetMapping
    public List<CourseResponse> getAllCourses() {
        return courseService.getAllCourses()
                .stream()
                .map(CourseMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Update course")
    @PutMapping("/{id}")
    public CourseResponse updateCourse(@PathVariable Long id, @RequestBody @Valid CourseRequest request) {
        Course course = CourseMapper.toEntity(request);
        return CourseMapper.toResponse(courseService.updateCourse(id, course));
    }

    @Operation(summary = "Delete course")
    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "Course deleted successfully";
    }

    @Operation(summary = "Search courses")
    @GetMapping("/search")
    public List<CourseResponse> searchCourses(@RequestParam String keyword) {
        return courseService.searchCoursesByName(keyword)
                .stream()
                .map(CourseMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Get courses with pagination")
    @GetMapping("/paged")
    public Page<CourseResponse> getCoursesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return courseService.getCoursesPaginated(page, size, sortBy, direction)
                .map(CourseMapper::toResponse);
    }
}