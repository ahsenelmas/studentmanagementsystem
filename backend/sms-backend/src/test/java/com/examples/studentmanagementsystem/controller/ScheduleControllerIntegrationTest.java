package com.examples.studentmanagementsystem.controller;

import com.examples.studentmanagementsystem.dto.request.ScheduleRequest;
import com.examples.studentmanagementsystem.entity.Course;
import com.examples.studentmanagementsystem.entity.DayOfWeekEnum;
import com.examples.studentmanagementsystem.entity.Schedule;
import com.examples.studentmanagementsystem.repository.CourseRepository;
import com.examples.studentmanagementsystem.repository.ScheduleRepository;
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

import java.time.LocalTime;

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
class ScheduleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        scheduleRepository.deleteAll();
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

    private Schedule createSampleSchedule() {
        Course course = createSampleCourse();

        Schedule schedule = new Schedule();
        schedule.setDayOfWeek(DayOfWeekEnum.MONDAY);
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(10, 30));
        schedule.setRoom("B201");
        schedule.setSemester("Spring");
        schedule.setCourse(course);

        return scheduleRepository.save(schedule);
    }

    private ScheduleRequest createScheduleRequest(Long courseId) {
        ScheduleRequest request = new ScheduleRequest();
        request.setDayOfWeek(DayOfWeekEnum.MONDAY);
        request.setStartTime(LocalTime.of(9, 0));
        request.setEndTime(LocalTime.of(10, 30));
        request.setRoom("B201");
        request.setSemester("Spring");
        request.setCourseId(courseId);
        return request;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createSchedule_ShouldReturnCreatedSchedule_WhenAdmin() throws Exception {
        Course course = createSampleCourse();
        ScheduleRequest request = createScheduleRequest(course.getId());

        mockMvc.perform(post("/api/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayOfWeek", is("MONDAY")))
                .andExpect(jsonPath("$.startTime", is("09:00:00")))
                .andExpect(jsonPath("$.endTime", is("10:30:00")))
                .andExpect(jsonPath("$.room", is("B201")))
                .andExpect(jsonPath("$.semester", is("Spring")));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void createSchedule_ShouldReturnForbidden_WhenStudent() throws Exception {
        Course course = createSampleCourse();
        ScheduleRequest request = createScheduleRequest(course.getId());

        mockMvc.perform(post("/api/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllSchedules_ShouldReturnScheduleList() throws Exception {
        createSampleSchedule();

        mockMvc.perform(get("/api/schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].dayOfWeek", is("MONDAY")))
                .andExpect(jsonPath("$[0].room", is("B201")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getScheduleById_ShouldReturnSchedule_WhenExists() throws Exception {
        Schedule savedSchedule = createSampleSchedule();

        mockMvc.perform(get("/api/schedules/{id}", savedSchedule.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedSchedule.getId().intValue())))
                .andExpect(jsonPath("$.dayOfWeek", is("MONDAY")))
                .andExpect(jsonPath("$.room", is("B201")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateSchedule_ShouldReturnUpdatedSchedule_WhenAdmin() throws Exception {
        Schedule savedSchedule = createSampleSchedule();
        Course course = courseRepository.findAll().get(0);

        ScheduleRequest updateRequest = new ScheduleRequest();
        updateRequest.setDayOfWeek(DayOfWeekEnum.TUESDAY);
        updateRequest.setStartTime(LocalTime.of(11, 0));
        updateRequest.setEndTime(LocalTime.of(12, 30));
        updateRequest.setRoom("A101");
        updateRequest.setSemester("Fall");
        updateRequest.setCourseId(course.getId());

        mockMvc.perform(put("/api/schedules/{id}", savedSchedule.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayOfWeek", is("TUESDAY")))
                .andExpect(jsonPath("$.startTime", is("11:00:00")))
                .andExpect(jsonPath("$.endTime", is("12:30:00")))
                .andExpect(jsonPath("$.room", is("A101")))
                .andExpect(jsonPath("$.semester", is("Fall")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteSchedule_ShouldDeleteSchedule_WhenAdmin() throws Exception {
        Schedule savedSchedule = createSampleSchedule();

        mockMvc.perform(delete("/api/schedules/{id}", savedSchedule.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Schedule deleted successfully")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSchedulesByDay_ShouldReturnMatchingSchedules() throws Exception {
        createSampleSchedule();

        mockMvc.perform(get("/api/schedules/day/{dayOfWeek}", "MONDAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].dayOfWeek", is("MONDAY")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSchedulesByCourseId_ShouldReturnMatchingSchedules() throws Exception {
        Schedule schedule = createSampleSchedule();
        Long courseId = schedule.getCourse().getId();

        mockMvc.perform(get("/api/schedules/course/{courseId}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].courseId", is(courseId.intValue())));
    }
}