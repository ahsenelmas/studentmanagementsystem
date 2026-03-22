package com.examples.studentmanagementsystem.mapper;

import com.examples.studentmanagementsystem.dto.request.StudentRequest;
import com.examples.studentmanagementsystem.dto.response.StudentResponse;
import com.examples.studentmanagementsystem.entity.Student;

public class StudentMapper {

    public static Student toEntity(StudentRequest request) {
        Student student = new Student();
        student.setStudentNumber(request.getStudentNumber());
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setBirthDate(request.getBirthDate());
        student.setPhone(request.getPhone());
        student.setDepartment(request.getDepartment());
        return student;
    }

    public static StudentResponse toResponse(Student student) {
        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setStudentNumber(student.getStudentNumber());
        response.setFirstName(student.getFirstName());
        response.setLastName(student.getLastName());
        response.setBirthDate(student.getBirthDate());
        response.setPhone(student.getPhone());
        response.setDepartment(student.getDepartment());
        return response;
    }
}