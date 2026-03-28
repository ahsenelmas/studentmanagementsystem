package com.examples.studentmanagementsystem.service.impl;

import com.examples.studentmanagementsystem.entity.Student;
import com.examples.studentmanagementsystem.exception.DuplicateResourceException;
import com.examples.studentmanagementsystem.exception.ResourceNotFoundException;
import com.examples.studentmanagementsystem.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

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

    @Test
    void createStudent_ShouldSaveStudent_WhenStudentNumberIsUnique() {
        Student student = createSampleStudent();

        when(studentRepository.existsByStudentNumber(student.getStudentNumber())).thenReturn(false);
        when(studentRepository.save(student)).thenReturn(student);

        Student savedStudent = studentService.createStudent(student);

        assertNotNull(savedStudent);
        assertEquals("S12345", savedStudent.getStudentNumber());
        assertEquals("Ahsen", savedStudent.getFirstName());
        verify(studentRepository, times(1)).existsByStudentNumber(student.getStudentNumber());
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void createStudent_ShouldThrowDuplicateResourceException_WhenStudentNumberAlreadyExists() {
        Student student = createSampleStudent();

        when(studentRepository.existsByStudentNumber(student.getStudentNumber())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> studentService.createStudent(student)
        );

        assertEquals("Student number already exists: S12345", exception.getMessage());
        verify(studentRepository, times(1)).existsByStudentNumber(student.getStudentNumber());
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void getStudentById_ShouldReturnStudent_WhenStudentExists() {
        Student student = createSampleStudent();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Student foundStudent = studentService.getStudentById(1L);

        assertNotNull(foundStudent);
        assertEquals(1L, foundStudent.getId());
        assertEquals("Ahsen", foundStudent.getFirstName());
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    void getStudentById_ShouldThrowResourceNotFoundException_WhenStudentDoesNotExist() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.getStudentById(1L)
        );

        assertEquals("Student not found with id: 1", exception.getMessage());
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    void getAllStudents_ShouldReturnStudentList() {
        Student student = createSampleStudent();

        when(studentRepository.findAll()).thenReturn(List.of(student));

        List<Student> students = studentService.getAllStudents();

        assertEquals(1, students.size());
        assertEquals("Ahsen", students.get(0).getFirstName());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void updateStudent_ShouldUpdateAndReturnStudent_WhenDataIsValid() {
        Student existingStudent = createSampleStudent();

        Student updatedData = Student.builder()
                .studentNumber("S54321")
                .firstName("Saadet")
                .lastName("Nur")
                .birthDate(LocalDate.of(2004, 1, 15))
                .phone("987654321")
                .department("Software Engineering")
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.existsByStudentNumber("S54321")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Student updatedStudent = studentService.updateStudent(1L, updatedData);

        assertNotNull(updatedStudent);
        assertEquals("S54321", updatedStudent.getStudentNumber());
        assertEquals("Saadet", updatedStudent.getFirstName());
        assertEquals("Nur", updatedStudent.getLastName());
        assertEquals("Software Engineering", updatedStudent.getDepartment());

        verify(studentRepository, times(1)).findById(1L);
        verify(studentRepository, times(1)).existsByStudentNumber("S54321");
        verify(studentRepository, times(1)).save(existingStudent);
    }

    @Test
    void updateStudent_ShouldThrowDuplicateResourceException_WhenNewStudentNumberAlreadyExists() {
        Student existingStudent = createSampleStudent();

        Student updatedData = Student.builder()
                .studentNumber("S99999")
                .firstName("Saadet")
                .lastName("Nur")
                .birthDate(LocalDate.of(2004, 1, 15))
                .phone("987654321")
                .department("Software Engineering")
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.existsByStudentNumber("S99999")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> studentService.updateStudent(1L, updatedData)
        );

        assertEquals("Student number already exists: S99999", exception.getMessage());
        verify(studentRepository, times(1)).findById(1L);
        verify(studentRepository, times(1)).existsByStudentNumber("S99999");
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void deleteStudent_ShouldDeleteStudent_WhenStudentExists() {
        Student student = createSampleStudent();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        doNothing().when(studentRepository).delete(student);

        studentService.deleteStudent(1L);

        verify(studentRepository, times(1)).findById(1L);
        verify(studentRepository, times(1)).delete(student);
    }

    @Test
    void getStudentsByDepartment_ShouldReturnMatchingStudents() {
        Student student = createSampleStudent();

        when(studentRepository.findByDepartment("Computer Engineering")).thenReturn(List.of(student));

        List<Student> students = studentService.getStudentsByDepartment("Computer Engineering");

        assertEquals(1, students.size());
        assertEquals("Computer Engineering", students.get(0).getDepartment());
        verify(studentRepository, times(1)).findByDepartment("Computer Engineering");
    }

    @Test
    void searchStudentsByName_ShouldReturnMatchingStudents() {
        Student student = createSampleStudent();

        when(studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("ah", "ah"))
                .thenReturn(List.of(student));

        List<Student> students = studentService.searchStudentsByName("ah");

        assertEquals(1, students.size());
        assertEquals("Ahsen", students.get(0).getFirstName());
        verify(studentRepository, times(1))
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("ah", "ah");
    }

    @Test
    void getStudentsPaginated_ShouldReturnPagedStudents_WithAscendingSort() {
        Student student = createSampleStudent();
        Pageable pageable = PageRequest.of(0, 5, Sort.by("firstName").ascending());
        Page<Student> page = new PageImpl<>(List.of(student), pageable, 1);

        when(studentRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Student> result = studentService.getStudentsPaginated(0, 5, "firstName", "asc");

        assertEquals(1, result.getTotalElements());
        assertEquals("Ahsen", result.getContent().get(0).getFirstName());
        verify(studentRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getStudentsPaginated_ShouldReturnPagedStudents_WithDescendingSort() {
        Student student = createSampleStudent();
        Pageable pageable = PageRequest.of(0, 5, Sort.by("firstName").descending());
        Page<Student> page = new PageImpl<>(List.of(student), pageable, 1);

        when(studentRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Student> result = studentService.getStudentsPaginated(0, 5, "firstName", "desc");

        assertEquals(1, result.getTotalElements());
        assertEquals("Ahsen", result.getContent().get(0).getFirstName());
        verify(studentRepository, times(1)).findAll(any(Pageable.class));
    }
}