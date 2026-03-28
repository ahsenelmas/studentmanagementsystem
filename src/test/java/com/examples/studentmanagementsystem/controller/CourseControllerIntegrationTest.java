package com.examples.studentmanagementsystem.controller;

import com.examples.studentmanagementsystem.dto.request.CourseRequest;
import com.examples.studentmanagementsystem.entity.Course;
import com.examples.studentmanagementsystem.repository.CourseRepository;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CourseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
    }

    private Course createSampleCourse() {
        Course course = new Course();
        course.setCourseCode("CSE101");
        course.setCourseName("Introduction to Programming");
        course.setCredit(4);
        course.setDescription("Basic programming course");
        return courseRepository.save(course);
    }

    private CourseRequest createCourseRequest() {
        CourseRequest request = new CourseRequest();
        request.setCourseCode("CSE101");
        request.setCourseName("Introduction to Programming");
        request.setCredit(4);
        request.setDescription("Basic programming course");
        return request;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourse_ShouldReturnCreatedCourse_WhenAdmin() throws Exception {
        CourseRequest request = createCourseRequest();

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseCode", is("CSE101")))
                .andExpect(jsonPath("$.courseName", is("Introduction to Programming")))
                .andExpect(jsonPath("$.credit", is(4)))
                .andExpect(jsonPath("$.description", is("Basic programming course")));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void createCourse_ShouldReturnForbidden_WhenStudent() throws Exception {
        CourseRequest request = createCourseRequest();

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "STUDENT"})
    void getAllCourses_ShouldReturnCourseList() throws Exception {
        createSampleCourse();

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].courseCode", is("CSE101")))
                .andExpect(jsonPath("$[0].courseName", is("Introduction to Programming")));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "STUDENT"})
    void getCourseById_ShouldReturnCourse_WhenExists() throws Exception {
        Course savedCourse = createSampleCourse();

        mockMvc.perform(get("/api/courses/{id}", savedCourse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedCourse.getId().intValue())))
                .andExpect(jsonPath("$.courseCode", is("CSE101")))
                .andExpect(jsonPath("$.courseName", is("Introduction to Programming")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCourse_ShouldReturnUpdatedCourse_WhenAdmin() throws Exception {
        Course savedCourse = createSampleCourse();

        CourseRequest updateRequest = new CourseRequest();
        updateRequest.setCourseCode("CSE202");
        updateRequest.setCourseName("Data Structures");
        updateRequest.setCredit(5);
        updateRequest.setDescription("Updated course");

        mockMvc.perform(put("/api/courses/{id}", savedCourse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseCode", is("CSE202")))
                .andExpect(jsonPath("$.courseName", is("Data Structures")))
                .andExpect(jsonPath("$.credit", is(5)))
                .andExpect(jsonPath("$.description", is("Updated course")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCourse_ShouldDeleteCourse_WhenAdmin() throws Exception {
        Course savedCourse = createSampleCourse();

        mockMvc.perform(delete("/api/courses/{id}", savedCourse.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Course deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourse_ShouldFail_WhenDuplicateCourseCode() throws Exception {
        createSampleCourse();
        CourseRequest request = createCourseRequest();

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Course code already exists")));
    }
}