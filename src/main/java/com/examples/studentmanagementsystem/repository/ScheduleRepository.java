package com.examples.studentmanagementsystem.repository;

import com.examples.studentmanagementsystem.entity.DayOfWeekEnum;
import com.examples.studentmanagementsystem.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByDayOfWeek(DayOfWeekEnum dayOfWeek);

    List<Schedule> findBySemester(String semester);

    List<Schedule> findByRoom(String room);

    List<Schedule> findByCourseId(Long courseId);
}