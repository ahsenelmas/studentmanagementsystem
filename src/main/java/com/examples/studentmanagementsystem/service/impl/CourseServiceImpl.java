package com.examples.studentmanagementsystem.service.impl;

import com.examples.studentmanagementsystem.entity.Course;
import com.examples.studentmanagementsystem.exception.DuplicateResourceException;
import com.examples.studentmanagementsystem.exception.ResourceNotFoundException;
import com.examples.studentmanagementsystem.repository.CourseRepository;
import com.examples.studentmanagementsystem.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public Course createCourse(Course course) {
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new DuplicateResourceException("Course code already exists: " + course.getCourseCode());
        }
        return courseRepository.save(course);
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Course updateCourse(Long id, Course course) {
        Course existingCourse = getCourseById(id);

        existingCourse.setCourseName(course.getCourseName());
        existingCourse.setCredit(course.getCredit());
        existingCourse.setDescription(course.getDescription());

        if (!existingCourse.getCourseCode().equals(course.getCourseCode())
                && courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new DuplicateResourceException("Course code already exists: " + course.getCourseCode());
        }

        existingCourse.setCourseCode(course.getCourseCode());

        return courseRepository.save(existingCourse);
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        courseRepository.delete(course);
    }

    @Override
    public List<Course> searchCoursesByName(String keyword) {
        return courseRepository.findByCourseNameContainingIgnoreCase(keyword);
    }

    @Override
    public Page<Course> getCoursesPaginated(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return courseRepository.findAll(pageable);
    }
}

