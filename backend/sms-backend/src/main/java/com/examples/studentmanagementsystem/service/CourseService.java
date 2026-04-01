package com.examples.studentmanagementsystem.service;

import com.examples.studentmanagementsystem.entity.Course;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CourseService {

    Course createCourse(Course course);

    Course getCourseById(Long id);

    List<Course> getAllCourses();

    Course updateCourse(Long id, Course course);

    void deleteCourse(Long id);

    Page<Course> getCoursesPaginated(int page, int size, String sortBy, String direction);

    List<Course> searchCoursesByName(String keyword);

    List<Course> getMyCourses();
}