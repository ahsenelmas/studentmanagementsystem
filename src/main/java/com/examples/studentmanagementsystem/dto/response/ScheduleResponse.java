package com.examples.studentmanagementsystem.dto.response;

import com.examples.studentmanagementsystem.entity.DayOfWeekEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class ScheduleResponse {

    private Long id;
    private DayOfWeekEnum dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
    private String semester;
    private Long courseId;
    private String courseCode;
    private String courseName;
}