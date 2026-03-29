package com.examples.studentmanagementsystem.service.impl;

import com.examples.studentmanagementsystem.entity.DayOfWeekEnum;
import com.examples.studentmanagementsystem.entity.Schedule;
import com.examples.studentmanagementsystem.exception.ResourceNotFoundException;
import com.examples.studentmanagementsystem.repository.ScheduleRepository;
import com.examples.studentmanagementsystem.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public Schedule createSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));
    }

    @Override
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    @Override
    public Schedule updateSchedule(Long id, Schedule schedule) {
        Schedule existingSchedule = getScheduleById(id);

        existingSchedule.setDayOfWeek(schedule.getDayOfWeek());
        existingSchedule.setStartTime(schedule.getStartTime());
        existingSchedule.setEndTime(schedule.getEndTime());
        existingSchedule.setRoom(schedule.getRoom());
        existingSchedule.setSemester(schedule.getSemester());
        existingSchedule.setCourse(schedule.getCourse());

        return scheduleRepository.save(existingSchedule);
    }

    @Override
    public void deleteSchedule(Long id) {
        Schedule schedule = getScheduleById(id);
        scheduleRepository.delete(schedule);
    }

    @Override
    public List<Schedule> getSchedulesByDay(DayOfWeekEnum dayOfWeek) {
        return scheduleRepository.findByDayOfWeek(dayOfWeek);
    }

    @Override
    public List<Schedule> getSchedulesByCourseId(Long courseId) {
        return scheduleRepository.findByCourseId(courseId);
    }
}