package com.sms.service;

import com.sms.domain.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ValidationService {

    private static final Set<Integer> VALID_LEVELS = Set.of(100,200,300,400,500,600,700);

    public List<String> validate(Student s) {
        List<String> errors = new ArrayList<>();

        // Student ID
        if (s.getStudentId() == null || s.getStudentId().isBlank()) {
            errors.add("Student ID is required.");
        } else if (s.getStudentId().length() < 4 || s.getStudentId().length() > 20) {
            errors.add("Student ID must be 4 to 20 characters.");
        } else if (!s.getStudentId().matches("[a-zA-Z0-9]+")) {
            errors.add("Student ID must contain letters and digits only.");
        }

        // Full name
        if (s.getFullName() == null || s.getFullName().isBlank()) {
            errors.add("Full name is required.");
        } else if (s.getFullName().length() < 2 || s.getFullName().length() > 60) {
            errors.add("Full name must be 2 to 60 characters.");
        } else if (s.getFullName().matches(".*\\d.*")) {
            errors.add("Full name must not contain digits.");
        }

        // Programme
        if (s.getProgramme() == null || s.getProgramme().isBlank()) {
            errors.add("Programme is required.");
        }

        // Level
        if (!VALID_LEVELS.contains(s.getLevel())) {
            errors.add("Level must be one of: 100, 200, 300, 400, 500, 600, 700.");
        }

        // GPA
        if (s.getGpa() < 0.0 || s.getGpa() > 4.0) {
            errors.add("GPA must be between 0.0 and 4.0.");
        }

        // Email
        if (s.getEmail() == null || !s.getEmail().contains("@") || !s.getEmail().contains(".")) {
            errors.add("Email must contain @ and a dot.");
        }

        // Phone
        if (s.getPhoneNumber() == null || !s.getPhoneNumber().matches("\\d{10,15}")) {
            errors.add("Phone number must be 10 to 15 digits only.");
        }

        return errors;
    }

    public boolean isValid(Student s) {
        return validate(s).isEmpty();
    }
}
