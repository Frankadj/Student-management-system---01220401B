package com.sms.service;

import com.sms.domain.Student;
import com.sms.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ReportServiceTest {

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        List<Student> mockData = List.of(
            new Student("S001","Alice Boateng","Computer Science",100,3.9,"a@b.com","0241000001",LocalDate.now(),"Active"),
            new Student("S002","Bob Mensah","Computer Science",200,1.5,"b@b.com","0241000002",LocalDate.now(),"Active"),
            new Student("S003","Clara Asante","Engineering",300,2.8,"c@b.com","0241000003",LocalDate.now(),"Active"),
            new Student("S004","David Ofori","Engineering",100,0.9,"d@b.com","0241000004",LocalDate.now(),"Inactive"),
            new Student("S005","Eve Darko","Business",400,3.5,"e@b.com","0241000005",LocalDate.now(),"Active")
        );

        StudentRepository mockRepo = new StudentRepository() {
            public void add(Student s) {}
            public void update(Student s) {}
            public void delete(String id) {}
            public Optional<Student> findById(String id) { return Optional.empty(); }
            public List<Student> findAll() { return mockData; }
            public List<Student> search(String k) { return mockData; }
            public List<Student> filter(String p, Integer l, String s) { return mockData; }
            public boolean existsById(String id) { return false; }
        };

        reportService = new ReportService(mockRepo);
    }

    @Test
    void testTopPerformersReturnsCorrectCount() {
        List<Student> top = reportService.getTopPerformers(3, null, null);
        assertEquals(3, top.size());
    }

    @Test
    void testTopPerformersAreOrderedByGpaDescending() {
        List<Student> top = reportService.getTopPerformers(5, null, null);
        assertTrue(top.get(0).getGpa() >= top.get(1).getGpa());
    }

    @Test
    void testAtRiskReturnsStudentsBelowThreshold() {
        List<Student> atRisk = reportService.getAtRiskStudents(2.0);
        assertTrue(atRisk.stream().allMatch(s -> s.getGpa() < 2.0));
    }

    @Test
    void testAtRiskCountIsCorrect() {
        List<Student> atRisk = reportService.getAtRiskStudents(2.0);
        assertEquals(2, atRisk.size());
    }

    @Test
    void testGpaDistributionHasFourBands() {
        Map<String, Long> dist = reportService.getGpaDistribution();
        assertEquals(4, dist.size());
    }

    @Test
    void testGpaDistributionTotalMatchesStudentCount() {
        Map<String, Long> dist = reportService.getGpaDistribution();
        long total = dist.values().stream().mapToLong(Long::longValue).sum();
        assertEquals(5, total);
    }

    @Test
    void testProgrammeSummaryHasCorrectProgrammes() {
        Map<String, double[]> summary = reportService.getProgrammeSummary();
        assertTrue(summary.containsKey("Computer Science"));
        assertTrue(summary.containsKey("Engineering"));
        assertTrue(summary.containsKey("Business"));
    }
}
