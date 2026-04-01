package com.examples.studentmanagementsystem.controller;

import com.examples.studentmanagementsystem.dto.request.ScheduleRequest;
import com.examples.studentmanagementsystem.dto.response.ScheduleResponse;
import com.examples.studentmanagementsystem.entity.Course;
import com.examples.studentmanagementsystem.entity.DayOfWeekEnum;
import com.examples.studentmanagementsystem.entity.Schedule;
import com.examples.studentmanagementsystem.mapper.ScheduleMapper;
import com.examples.studentmanagementsystem.service.CourseService;
import com.examples.studentmanagementsystem.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Schedule API", description = "Operations related to schedules")
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final CourseService courseService;

    public ScheduleController(ScheduleService scheduleService, CourseService courseService) {
        this.scheduleService = scheduleService;
        this.courseService = courseService;
    }

    @Operation(summary = "Create a new schedule")
    @PostMapping
    public ScheduleResponse createSchedule(@RequestBody @Valid ScheduleRequest request) {
        Course course = courseService.getCourseById(request.getCourseId());

        Schedule schedule = new Schedule();
        schedule.setDayOfWeek(request.getDayOfWeek());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setRoom(request.getRoom());
        schedule.setSemester(request.getSemester());
        schedule.setCourse(course);

        return ScheduleMapper.toResponse(scheduleService.createSchedule(schedule));
    }

    @Operation(summary = "Get current student's schedules")
    @GetMapping("/my")
    public List<ScheduleResponse> getMySchedules() {
        return scheduleService.getMySchedules()
                .stream()
                .map(ScheduleMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Get schedule by ID")
    @GetMapping("/{id}")
    public ScheduleResponse getScheduleById(@PathVariable Long id) {
        return ScheduleMapper.toResponse(scheduleService.getScheduleById(id));
    }

    @Operation(summary = "Get all schedules")
    @GetMapping
    public List<ScheduleResponse> getAllSchedules() {
        return scheduleService.getAllSchedules()
                .stream()
                .map(ScheduleMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Update schedule")
    @PutMapping("/{id}")
    public ScheduleResponse updateSchedule(@PathVariable Long id, @RequestBody @Valid ScheduleRequest request) {
        Course course = courseService.getCourseById(request.getCourseId());

        Schedule schedule = new Schedule();
        schedule.setDayOfWeek(request.getDayOfWeek());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setRoom(request.getRoom());
        schedule.setSemester(request.getSemester());
        schedule.setCourse(course);

        return ScheduleMapper.toResponse(scheduleService.updateSchedule(id, schedule));
    }

    @Operation(summary = "Delete schedule")
    @DeleteMapping("/{id}")
    public String deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return "Schedule deleted successfully";
    }

    @Operation(summary = "Get schedules by day")
    @GetMapping("/day/{dayOfWeek}")
    public List<ScheduleResponse> getSchedulesByDay(@PathVariable DayOfWeekEnum dayOfWeek) {
        return scheduleService.getSchedulesByDay(dayOfWeek)
                .stream()
                .map(ScheduleMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Get schedules by course ID")
    @GetMapping("/course/{courseId}")
    public List<ScheduleResponse> getSchedulesByCourseId(@PathVariable Long courseId) {
        return scheduleService.getSchedulesByCourseId(courseId)
                .stream()
                .map(ScheduleMapper::toResponse)
                .toList();
    }
}