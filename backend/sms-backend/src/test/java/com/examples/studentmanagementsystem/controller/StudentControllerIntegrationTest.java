package com.examples.studentmanagementsystem.controller;

import com.examples.studentmanagementsystem.dto.request.StudentRequest;
import com.examples.studentmanagementsystem.entity.Student;
import com.examples.studentmanagementsystem.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.examples.studentmanagementsystem.repository.UserRepository;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StudentControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        studentRepository.deleteAll();
    }

    private Student createSampleStudent() {
        Student student = new Student();
        student.setStudentNumber("S12345");
        student.setFirstName("Ahsen");
        student.setLastName("Elmas");
        student.setBirthDate(LocalDate.of(2003, 5, 10));
        student.setPhone("123456789");
        student.setDepartment("Computer Engineering");
        return studentRepository.save(student);
    }

    private StudentRequest createStudentRequest() {
        StudentRequest request = new StudentRequest();
        request.setStudentNumber("S12345");
        request.setFirstName("Ahsen");
        request.setLastName("Elmas");
        request.setBirthDate(LocalDate.of(2003, 5, 10));
        request.setPhone("123456789");
        request.setDepartment("Computer Engineering");
        return request;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createStudent_ShouldReturnCreatedStudent_WhenAdmin() throws Exception {
        StudentRequest request = createStudentRequest();

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentNumber", is("S12345")))
                .andExpect(jsonPath("$.firstName", is("Ahsen")))
                .andExpect(jsonPath("$.lastName", is("Elmas")))
                .andExpect(jsonPath("$.department", is("Computer Engineering")));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void createStudent_ShouldReturnForbidden_WhenStudentRole() throws Exception {
        StudentRequest request = createStudentRequest();

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "STUDENT"})
    void getAllStudents_ShouldReturnStudentList() throws Exception {
        createSampleStudent();

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].studentNumber", is("S12345")))
                .andExpect(jsonPath("$[0].firstName", is("Ahsen")));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "STUDENT"})
    void getStudentById_ShouldReturnStudent_WhenExists() throws Exception {
        Student savedStudent = createSampleStudent();

        mockMvc.perform(get("/api/students/{id}", savedStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedStudent.getId().intValue())))
                .andExpect(jsonPath("$.studentNumber", is("S12345")))
                .andExpect(jsonPath("$.firstName", is("Ahsen")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteStudent_ShouldDeleteStudent_WhenAdmin() throws Exception {
        Student savedStudent = createSampleStudent();

        mockMvc.perform(delete("/api/students/{id}", savedStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("deleted")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createStudent_ShouldFail_WhenDuplicateStudentNumber() throws Exception {
        createSampleStudent();
        StudentRequest request = createStudentRequest();

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Student number already exists")));
    }
}