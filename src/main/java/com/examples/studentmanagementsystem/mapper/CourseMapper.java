package com.examples.studentmanagementsystem.mapper;

import com.examples.studentmanagementsystem.dto.request.CourseRequest;
import com.examples.studentmanagementsystem.dto.response.CourseResponse;
import com.examples.studentmanagementsystem.entity.Course;

public class CourseMapper {

    public static Course toEntity(CourseRequest request) {
        Course course = new Course();
        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setCredit(request.getCredit());
        course.setDescription(request.getDescription());
        return course;
    }

    public static CourseResponse toResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setCourseCode(course.getCourseCode());
        response.setCourseName(course.getCourseName());
        response.setCredit(course.getCredit());
        response.setDescription(course.getDescription());
        return response;
    }
}