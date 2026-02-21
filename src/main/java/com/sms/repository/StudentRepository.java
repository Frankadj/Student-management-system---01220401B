package com.sms.repository;

import com.sms.domain.Student;
import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    void add(Student student);
    void update(Student student);
    void delete(String studentId);
    Optional<Student> findById(String studentId);
    List<Student> findAll();
    List<Student> search(String keyword);
    List<Student> filter(String programme, Integer level, String status);
    boolean existsById(String studentId);
}
