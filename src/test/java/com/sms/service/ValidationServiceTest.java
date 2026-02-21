package com.sms.service;

import com.sms.domain.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationServiceTest {

    private ValidationService validator;
    private Student validStudent;

    @BeforeEach
    void setUp() {
        validator = new ValidationService();
        validStudent = new Student("STU001", "John Mensah", "Computer Science",
                100, 3.5, "john@email.com", "0241234567", LocalDate.now(), "Active");
    }

    @Test
    void testValidStudentPassesValidation() {
        List<String> errors = validator.validate(validStudent);
        assertTrue(errors.isEmpty(), "Valid student should have no errors");
    }

    @Test
    void testBlankStudentIdFails() {
        validStudent.setStudentId("");
        List<String> errors = validator.validate(validStudent);
        assertFalse(errors.isEmpty());
    }

    @Test
    void testStudentIdTooShortFails() {
        validStudent.setStudentId("AB");
        List<String> errors = validator.validate(validStudent);
        assertFalse(errors.isEmpty());
    }

    @Test
    void testStudentIdWithSpecialCharsFails() {
        validStudent.setStudentId("STU@001");
        List<String> errors = validator.validate(validStudent);
        assertFalse(errors.isEmpty());
    }

    @Test
    void testFullNameWithDigitsFails() {
        validStudent.setFullName("John123");
        List<String> errors = validator.validate(validStudent);
        assertFalse(errors.isEmpty());
    }

    @Test
    void testBlankFullNameFails() {
        validStudent.setFullName("");
        List<String> errors = validator.validate(validStudent);
        assertFalse(errors.isEmpty());
    }

    @Test
    void testInvalidLevelFails() {
        validStudent.setLevel(150);
        List<String> errors = validator.validate(validStudent);
        assertFalse(errors.isEmpty());
    }

    @Test
    void testGpaAbove4Fails() {
        validStudent.setGpa(4.5);
        List<String> errors = validator.validate(validStudent);
        assertFalse(errors.isEmpty());
    }

    @Test
    void testNegativeGpaFails() {
        validStudent.setGpa(-1.0);
        List<String> errors = validator.validate(validStudent);
        assertFalse(errors.isEmpty());
    }

    @Test
    void testInvalidEmailFails() {
        validStudent.setEmail("notanemail");
        List<String> errors = validator.validate(validStudent);
        assertFalse(errors.isEmpty());
    }

    @Test
    void testPhoneTooShortFails() {
        validStudent.setPhoneNumber("12345");
        List<String> errors = validator.validate(validStudent);
        assertFalse(errors.isEmpty());
    }

    @Test
    void testPhoneWithLettersFails() {
        validStudent.setPhoneNumber("024ABC4567");
        List<String> errors = validator.validate(validStudent);
        assertFalse(errors.isEmpty());
    }

    @Test
    void testBlankProgrammeFails() {
        validStudent.setProgramme("");
        List<String> errors = validator.validate(validStudent);
        assertFalse(errors.isEmpty());
    }

    @Test
    void testAllValidLevelsPass() {
        for (int level : new int[]{100,200,300,400,500,600,700}) {
            validStudent.setLevel(level);
            assertTrue(validator.validate(validStudent).isEmpty(),
                    "Level " + level + " should be valid");
        }
    }
}
