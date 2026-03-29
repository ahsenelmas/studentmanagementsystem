package com.examples.studentmanagementsystem.mapper;

import com.examples.studentmanagementsystem.dto.response.ScheduleResponse;
import com.examples.studentmanagementsystem.entity.Schedule;

public class ScheduleMapper {

    public static ScheduleResponse toResponse(Schedule schedule) {
        ScheduleResponse response = new ScheduleResponse();
        response.setId(schedule.getId());
        response.setDayOfWeek(schedule.getDayOfWeek());
        response.setStartTime(schedule.getStartTime());
        response.setEndTime(schedule.getEndTime());
        response.setRoom(schedule.getRoom());
        response.setSemester(schedule.getSemester());

        if (schedule.getCourse() != null) {
            response.setCourseId(schedule.getCourse().getId());
            response.setCourseCode(schedule.getCourse().getCourseCode());
            response.setCourseName(schedule.getCourse().getCourseName());
        }

        return response;
    }
}