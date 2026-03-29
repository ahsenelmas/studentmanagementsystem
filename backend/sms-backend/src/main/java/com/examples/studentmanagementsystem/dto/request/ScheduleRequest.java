package com.examples.studentmanagementsystem.dto.request;

import com.examples.studentmanagementsystem.entity.DayOfWeekEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class ScheduleRequest {

    @NotNull
    private DayOfWeekEnum dayOfWeek;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotBlank
    private String room;

    @NotBlank
    private String semester;

    @NotNull
    private Long courseId;
}