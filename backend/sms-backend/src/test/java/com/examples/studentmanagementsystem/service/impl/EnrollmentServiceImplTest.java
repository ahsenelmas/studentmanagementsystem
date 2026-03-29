package com.examples.studentmanagementsystem.service.impl;

import com.examples.studentmanagementsystem.entity.Course;
import com.examples.studentmanagementsystem.entity.Enrollment;
import com.examples.studentmanagementsystem.entity.EnrollmentStatus;
import com.examples.studentmanagementsystem.entity.Student;
import com.examples.studentmanagementsystem.exception.DuplicateResourceException;
import com.examples.studentmanagementsystem.exception.ResourceNotFoundException;
import com.examples.studentmanagementsystem.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private Student createSampleStudent() {
        return Student.builder()
                .id(1L)
                .studentNumber("S12345")
                .firstName("Ahsen")
                .lastName("Elmas")
                .birthDate(LocalDate.of(2003, 5, 10))
                .phone("123456789")
                .department("Computer Engineering")
                .build();
    }

    private Course createSampleCourse() {
        return Course.builder()
                .id(1L)
                .courseCode("CSE101")
                .courseName("Introduction to Programming")
                .credit(4)
                .description("Basic programming course")
                .build();
    }

    private Enrollment createSampleEnrollment() {
        return Enrollment.builder()
                .id(1L)
                .enrollmentDate(LocalDate.of(2026, 3, 28))
                .status(EnrollmentStatus.ACTIVE)
                .student(createSampleStudent())
                .course(createSampleCourse())
                .build();
    }

    @Test
    public void createEnrollment_ShouldSaveEnrollment_WhenStudentNotAlreadyEnrolled() {
        Enrollment enrollment = createSampleEnrollment();

        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 1L)).thenReturn(false);
        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);

        Enrollment savedEnrollment = enrollmentService.createEnrollment(enrollment);

        assertNotNull(savedEnrollment);
        assertEquals(EnrollmentStatus.ACTIVE, savedEnrollment.getStatus());
        assertEquals(1L, savedEnrollment.getStudent().getId());
        assertEquals(1L, savedEnrollment.getCourse().getId());

        verify(enrollmentRepository, times(1)).existsByStudentIdAndCourseId(1L, 1L);
        verify(enrollmentRepository, times(1)).save(enrollment);
    }

    @Test
    public void createEnrollment_ShouldThrowDuplicateResourceException_WhenStudentAlreadyEnrolled() {
        Enrollment enrollment = createSampleEnrollment();

        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 1L)).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> enrollmentService.createEnrollment(enrollment)
        );

        assertEquals("Student is already enrolled in this course.", exception.getMessage());
        verify(enrollmentRepository, times(1)).existsByStudentIdAndCourseId(1L, 1L);
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    public void getEnrollmentById_ShouldReturnEnrollment_WhenEnrollmentExists() {
        Enrollment enrollment = createSampleEnrollment();

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        Enrollment foundEnrollment = enrollmentService.getEnrollmentById(1L);

        assertNotNull(foundEnrollment);
        assertEquals(1L, foundEnrollment.getId());
        assertEquals(EnrollmentStatus.ACTIVE, foundEnrollment.getStatus());

        verify(enrollmentRepository, times(1)).findById(1L);
    }

    @Test
    public void getEnrollmentById_ShouldThrowResourceNotFoundException_WhenEnrollmentDoesNotExist() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> enrollmentService.getEnrollmentById(1L)
        );

        assertEquals("Enrollment not found with id: 1", exception.getMessage());
        verify(enrollmentRepository, times(1)).findById(1L);
    }

    @Test
    public void getAllEnrollments_ShouldReturnEnrollmentList() {
        Enrollment enrollment = createSampleEnrollment();

        when(enrollmentRepository.findAll()).thenReturn(List.of(enrollment));

        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();

        assertEquals(1, enrollments.size());
        assertEquals(EnrollmentStatus.ACTIVE, enrollments.get(0).getStatus());

        verify(enrollmentRepository, times(1)).findAll();
    }

    @Test
    public void deleteEnrollment_ShouldDeleteEnrollment_WhenEnrollmentExists() {
        Enrollment enrollment = createSampleEnrollment();

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        doNothing().when(enrollmentRepository).delete(enrollment);

        enrollmentService.deleteEnrollment(1L);

        verify(enrollmentRepository, times(1)).findById(1L);
        verify(enrollmentRepository, times(1)).delete(enrollment);
    }

    @Test
    public void getEnrollmentsByStudentId_ShouldReturnMatchingEnrollments() {
        Enrollment enrollment = createSampleEnrollment();

        when(enrollmentRepository.findByStudentId(1L)).thenReturn(List.of(enrollment));

        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudentId(1L);

        assertEquals(1, enrollments.size());
        assertEquals(1L, enrollments.get(0).getStudent().getId());

        verify(enrollmentRepository, times(1)).findByStudentId(1L);
    }

    @Test
    public void getEnrollmentsByCourseId_ShouldReturnMatchingEnrollments() {
        Enrollment enrollment = createSampleEnrollment();

        when(enrollmentRepository.findByCourseId(1L)).thenReturn(List.of(enrollment));

        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourseId(1L);

        assertEquals(1, enrollments.size());
        assertEquals(1L, enrollments.get(0).getCourse().getId());

        verify(enrollmentRepository, times(1)).findByCourseId(1L);
    }
}