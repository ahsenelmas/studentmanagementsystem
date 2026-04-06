package com.examples.studentmanagementsystem.service.impl;

import com.examples.studentmanagementsystem.entity.Course;
import com.examples.studentmanagementsystem.exception.DuplicateResourceException;
import com.examples.studentmanagementsystem.exception.ResourceNotFoundException;
import com.examples.studentmanagementsystem.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course createSampleCourse() {
        return Course.builder()
                .id(1L)
                .courseCode("CSE101")
                .courseName("Introduction to Programming")
                .credit(4)
                .description("Basic programming course")
                .build();
    }

    @Test
    public void createCourse_ShouldSaveCourse_WhenCourseCodeIsUnique() {
        Course course = createSampleCourse();

        when(courseRepository.existsByCourseCode(course.getCourseCode())).thenReturn(false);
        when(courseRepository.save(course)).thenReturn(course);

        Course savedCourse = courseService.createCourse(course);

        assertNotNull(savedCourse);
        assertEquals("CSE101", savedCourse.getCourseCode());
        assertEquals("Introduction to Programming", savedCourse.getCourseName());
        verify(courseRepository, times(1)).existsByCourseCode(course.getCourseCode());
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    public void createCourse_ShouldThrowDuplicateResourceException_WhenCourseCodeAlreadyExists() {
        Course course = createSampleCourse();

        when(courseRepository.existsByCourseCode(course.getCourseCode())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> courseService.createCourse(course)
        );

        assertEquals("Course code already exists: CSE101", exception.getMessage());
        verify(courseRepository, times(1)).existsByCourseCode(course.getCourseCode());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    public void getCourseById_ShouldReturnCourse_WhenCourseExists() {
        Course course = createSampleCourse();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Course foundCourse = courseService.getCourseById(1L);

        assertNotNull(foundCourse);
        assertEquals(1L, foundCourse.getId());
        assertEquals("CSE101", foundCourse.getCourseCode());
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    public void getCourseById_ShouldThrowResourceNotFoundException_WhenCourseDoesNotExist() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> courseService.getCourseById(1L)
        );

        assertEquals("Course not found with id: 1", exception.getMessage());
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    public void getAllCourses_ShouldReturnCourseList() {
        Course course = createSampleCourse();

        when(courseRepository.findAll()).thenReturn(List.of(course));

        List<Course> courses = courseService.getAllCourses();

        assertEquals(1, courses.size());
        assertEquals("Introduction to Programming", courses.get(0).getCourseName());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    public void updateCourse_ShouldUpdateAndReturnCourse_WhenDataIsValid() {
        Course existingCourse = createSampleCourse();

        Course updatedData = Course.builder()
                .courseCode("CSE202")
                .courseName("Data Structures")
                .credit(5)
                .description("Advanced programming structures")
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        when(courseRepository.existsByCourseCode("CSE202")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Course updatedCourse = courseService.updateCourse(1L, updatedData);

        assertNotNull(updatedCourse);
        assertEquals("CSE202", updatedCourse.getCourseCode());
        assertEquals("Data Structures", updatedCourse.getCourseName());
        assertEquals(5, updatedCourse.getCredit());
        assertEquals("Advanced programming structures", updatedCourse.getDescription());

        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).existsByCourseCode("CSE202");
        verify(courseRepository, times(1)).save(existingCourse);
    }

    @Test
    public void updateCourse_ShouldKeepSameCourseCode_WhenCodeNotChanged() {
        Course existingCourse = createSampleCourse();

        Course updatedData = Course.builder()
                .courseCode("CSE101")
                .courseName("Updated Name")
                .credit(5)
                .description("Updated description")
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Course updatedCourse = courseService.updateCourse(1L, updatedData);

        assertNotNull(updatedCourse);
        assertEquals("CSE101", updatedCourse.getCourseCode());
        assertEquals("Updated Name", updatedCourse.getCourseName());
        assertEquals(5, updatedCourse.getCredit());
        assertEquals("Updated description", updatedCourse.getDescription());

        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, never()).existsByCourseCode(anyString());
        verify(courseRepository, times(1)).save(existingCourse);
    }

    @Test
    public void updateCourse_ShouldThrowDuplicateResourceException_WhenNewCourseCodeAlreadyExists() {
        Course existingCourse = createSampleCourse();

        Course updatedData = Course.builder()
                .courseCode("CSE999")
                .courseName("Networks")
                .credit(4)
                .description("Computer networks")
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        when(courseRepository.existsByCourseCode("CSE999")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> courseService.updateCourse(1L, updatedData)
        );

        assertEquals("Course code already exists: CSE999", exception.getMessage());
        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).existsByCourseCode("CSE999");
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    public void deleteCourse_ShouldDeleteCourse_WhenCourseExists() {
        Course course = createSampleCourse();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        doNothing().when(courseRepository).delete(course);

        courseService.deleteCourse(1L);

        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).delete(course);
    }
}