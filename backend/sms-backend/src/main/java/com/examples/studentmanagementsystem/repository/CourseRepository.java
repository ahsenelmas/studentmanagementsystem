package com.examples.studentmanagementsystem.repository;

import com.examples.studentmanagementsystem.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseCode(String courseCode);

    boolean existsByCourseCode(String courseCode);

    List<Course> findByCourseNameContainingIgnoreCase(String courseName);

    @Query("""
           select distinct e.course
           from Enrollment e
           where e.student.user.username = :username
           """)
    List<Course> findCoursesByStudentUsername(String username);
}