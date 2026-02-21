package com.sms.service;

import com.sms.domain.Student;
import com.sms.repository.StudentRepository;
import com.sms.util.AppLogger;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StudentService {

    private final StudentRepository repo;
    private final ValidationService validator;

    public StudentService(StudentRepository repo) {
        this.repo = repo;
        this.validator = new ValidationService();
    }

    public void addStudent(Student s) {
        List<String> errors = validator.validate(s);
        if (!errors.isEmpty()) throw new IllegalArgumentException(String.join("\n", errors));
        if (repo.existsById(s.getStudentId()))
            throw new IllegalArgumentException("Student ID already exists: " + s.getStudentId());
        if (s.getDateAdded() == null) s.setDateAdded(LocalDate.now());
        repo.add(s);
    }

    public void updateStudent(Student s) {
        List<String> errors = validator.validate(s);
        if (!errors.isEmpty()) throw new IllegalArgumentException(String.join("\n", errors));
        repo.update(s);
    }

    public void deleteStudent(String studentId) {
        repo.delete(studentId);
    }

    public Optional<Student> findById(String studentId) {
        return repo.findById(studentId);
    }

    public List<Student> getAllStudents() {
        return repo.findAll();
    }

    public List<Student> search(String keyword) {
        return repo.search(keyword);
    }

    public List<Student> filter(String programme, Integer level, String status) {
        return repo.filter(programme, level, status);
    }

    public List<Student> sortByGpa(List<Student> students, boolean ascending) {
        Comparator<Student> cmp = Comparator.comparingDouble(Student::getGpa);
        if (!ascending) cmp = cmp.reversed();
        return students.stream().sorted(cmp).collect(Collectors.toList());
    }

    public List<Student> sortByName(List<Student> students) {
        return students.stream()
                .sorted(Comparator.comparing(Student::getFullName))
                .collect(Collectors.toList());
    }

    public long countActive() {
        return repo.findAll().stream().filter(s -> "Active".equals(s.getStatus())).count();
    }

    public long countInactive() {
        return repo.findAll().stream().filter(s -> "Inactive".equals(s.getStatus())).count();
    }

    public double averageGpa() {
        return repo.findAll().stream().mapToDouble(Student::getGpa).average().orElse(0.0);
    }
}
