package com.examples.studentmanagementsystem.service.impl;

import com.examples.studentmanagementsystem.entity.Course;
import com.examples.studentmanagementsystem.entity.DayOfWeekEnum;
import com.examples.studentmanagementsystem.entity.Schedule;
import com.examples.studentmanagementsystem.entity.User;
import com.examples.studentmanagementsystem.exception.ResourceNotFoundException;
import com.examples.studentmanagementsystem.repository.ScheduleRepository;
import com.examples.studentmanagementsystem.service.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceImplTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    private Course createSampleCourse() {
        return Course.builder()
                .id(1L)
                .courseCode("CSE101")
                .courseName("Introduction to Programming")
                .credit(4)
                .description("Basic programming course")
                .build();
    }

    private Schedule createSampleSchedule() {
        return Schedule.builder()
                .id(1L)
                .dayOfWeek(DayOfWeekEnum.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 30))
                .room("B201")
                .semester("Spring")
                .course(createSampleCourse())
                .build();
    }

    private User createSampleUser() {
        return User.builder()
                .id(1L)
                .username("student1")
                .email("student1@test.com")
                .build();
    }

    @Test
    void createSchedule_ShouldSaveScheduleSuccessfully() {
        Schedule schedule = createSampleSchedule();

        when(scheduleRepository.save(schedule)).thenReturn(schedule);

        Schedule savedSchedule = scheduleService.createSchedule(schedule);

        assertNotNull(savedSchedule);
        assertEquals(DayOfWeekEnum.MONDAY, savedSchedule.getDayOfWeek());
        assertEquals(LocalTime.of(9, 0), savedSchedule.getStartTime());
        assertEquals(LocalTime.of(10, 30), savedSchedule.getEndTime());
        assertEquals("B201", savedSchedule.getRoom());
        assertEquals("Spring", savedSchedule.getSemester());

        verify(scheduleRepository, times(1)).save(schedule);
    }

    @Test
    void getScheduleById_ShouldReturnSchedule_WhenExists() {
        Schedule schedule = createSampleSchedule();

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        Schedule foundSchedule = scheduleService.getScheduleById(1L);

        assertNotNull(foundSchedule);
        assertEquals(1L, foundSchedule.getId());
        assertEquals("B201", foundSchedule.getRoom());

        verify(scheduleRepository, times(1)).findById(1L);
    }

    @Test
    void getScheduleById_ShouldThrowResourceNotFoundException_WhenNotExists() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> scheduleService.getScheduleById(1L)
        );

        assertEquals("Schedule not found with id: 1", exception.getMessage());
        verify(scheduleRepository, times(1)).findById(1L);
    }

    @Test
    void getAllSchedules_ShouldReturnScheduleList() {
        Schedule schedule = createSampleSchedule();

        when(scheduleRepository.findAll()).thenReturn(List.of(schedule));

        List<Schedule> schedules = scheduleService.getAllSchedules();

        assertEquals(1, schedules.size());
        assertEquals(DayOfWeekEnum.MONDAY, schedules.get(0).getDayOfWeek());

        verify(scheduleRepository, times(1)).findAll();
    }

    @Test
    void updateSchedule_ShouldUpdateAndReturnSchedule_WhenValid() {
        Schedule existingSchedule = createSampleSchedule();

        Schedule updatedData = Schedule.builder()
                .dayOfWeek(DayOfWeekEnum.TUESDAY)
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(12, 30))
                .room("A101")
                .semester("Fall")
                .course(createSampleCourse())
                .build();

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(existingSchedule));
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Schedule updatedSchedule = scheduleService.updateSchedule(1L, updatedData);

        assertNotNull(updatedSchedule);
        assertEquals(DayOfWeekEnum.TUESDAY, updatedSchedule.getDayOfWeek());
        assertEquals(LocalTime.of(11, 0), updatedSchedule.getStartTime());
        assertEquals(LocalTime.of(12, 30), updatedSchedule.getEndTime());
        assertEquals("A101", updatedSchedule.getRoom());
        assertEquals("Fall", updatedSchedule.getSemester());

        verify(scheduleRepository, times(1)).findById(1L);
        verify(scheduleRepository, times(1)).save(existingSchedule);
    }

    @Test
    void updateSchedule_ShouldThrowResourceNotFoundException_WhenNotExists() {
        Schedule updatedData = Schedule.builder()
                .dayOfWeek(DayOfWeekEnum.TUESDAY)
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(12, 30))
                .room("A101")
                .semester("Fall")
                .course(createSampleCourse())
                .build();

        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> scheduleService.updateSchedule(1L, updatedData)
        );

        assertEquals("Schedule not found with id: 1", exception.getMessage());
        verify(scheduleRepository, times(1)).findById(1L);
        verify(scheduleRepository, never()).save(any(Schedule.class));
    }

    @Test
    void deleteSchedule_ShouldDeleteSchedule_WhenExists() {
        Schedule schedule = createSampleSchedule();

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        doNothing().when(scheduleRepository).delete(schedule);

        scheduleService.deleteSchedule(1L);

        verify(scheduleRepository, times(1)).findById(1L);
        verify(scheduleRepository, times(1)).delete(schedule);
    }

    @Test
    void deleteSchedule_ShouldThrowResourceNotFoundException_WhenNotExists() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> scheduleService.deleteSchedule(1L)
        );

        assertEquals("Schedule not found with id: 1", exception.getMessage());
        verify(scheduleRepository, times(1)).findById(1L);
        verify(scheduleRepository, never()).delete(any(Schedule.class));
    }

    @Test
    void getSchedulesByDay_ShouldReturnMatchingSchedules() {
        Schedule schedule = createSampleSchedule();

        when(scheduleRepository.findByDayOfWeek(DayOfWeekEnum.MONDAY)).thenReturn(List.of(schedule));

        List<Schedule> schedules = scheduleService.getSchedulesByDay(DayOfWeekEnum.MONDAY);

        assertEquals(1, schedules.size());
        assertEquals(DayOfWeekEnum.MONDAY, schedules.get(0).getDayOfWeek());

        verify(scheduleRepository, times(1)).findByDayOfWeek(DayOfWeekEnum.MONDAY);
    }

    @Test
    void getSchedulesByCourseId_ShouldReturnMatchingSchedules() {
        Schedule schedule = createSampleSchedule();

        when(scheduleRepository.findByCourseId(1L)).thenReturn(List.of(schedule));

        List<Schedule> schedules = scheduleService.getSchedulesByCourseId(1L);

        assertEquals(1, schedules.size());
        assertEquals(1L, schedules.get(0).getCourse().getId());

        verify(scheduleRepository, times(1)).findByCourseId(1L);
    }

    @Test
    void getMySchedules_ShouldReturnSchedulesOfCurrentUser() {
        Schedule schedule = createSampleSchedule();
        User currentUser = createSampleUser();

        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(scheduleRepository.findSchedulesByStudentUsername("student1"))
                .thenReturn(List.of(schedule));

        List<Schedule> schedules = scheduleService.getMySchedules();

        assertEquals(1, schedules.size());
        assertEquals("B201", schedules.get(0).getRoom());

        verify(currentUserService, times(1)).getCurrentUser();
        verify(scheduleRepository, times(1)).findSchedulesByStudentUsername("student1");
    }
}