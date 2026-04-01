package com.examples.studentmanagementsystem.service;

import com.examples.studentmanagementsystem.entity.DayOfWeekEnum;
import com.examples.studentmanagementsystem.entity.Schedule;

import java.util.List;

public interface ScheduleService {

    Schedule createSchedule(Schedule schedule);

    Schedule getScheduleById(Long id);

    List<Schedule> getAllSchedules();

    Schedule updateSchedule(Long id, Schedule schedule);

    void deleteSchedule(Long id);

    List<Schedule> getSchedulesByDay(DayOfWeekEnum dayOfWeek);

    List<Schedule> getSchedulesByCourseId(Long courseId);

    List<Schedule> getMySchedules();
}