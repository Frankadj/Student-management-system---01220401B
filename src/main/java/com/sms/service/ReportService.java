package com.sms.service;

import com.sms.domain.Student;
import com.sms.repository.StudentRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {

    private final StudentRepository repo;

    public ReportService(StudentRepository repo) {
        this.repo = repo;
    }

    public List<Student> getTopPerformers(int limit, String programme, Integer level) {
        return repo.findAll().stream()
                .filter(s -> programme == null || programme.isEmpty() || s.getProgramme().equals(programme))
                .filter(s -> level == null || s.getLevel() == level)
                .sorted((a, b) -> Double.compare(b.getGpa(), a.getGpa()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Student> getAtRiskStudents(double threshold) {
        return repo.findAll().stream()
                .filter(s -> s.getGpa() < threshold)
                .sorted((a, b) -> Double.compare(a.getGpa(), b.getGpa()))
                .collect(Collectors.toList());
    }

    public Map<String, Long> getGpaDistribution() {
        Map<String, Long> bands = new LinkedHashMap<>();
        bands.put("0.0 - 1.0", 0L);
        bands.put("1.0 - 2.0", 0L);
        bands.put("2.0 - 3.0", 0L);
        bands.put("3.0 - 4.0", 0L);

        for (Student s : repo.findAll()) {
            double gpa = s.getGpa();
            if (gpa < 1.0) {
                bands.put("0.0 - 1.0", bands.get("0.0 - 1.0") + 1);
            } else if (gpa < 2.0) {
                bands.put("1.0 - 2.0", bands.get("1.0 - 2.0") + 1);
            } else if (gpa < 3.0) {
                bands.put("2.0 - 3.0", bands.get("2.0 - 3.0") + 1);
            } else {
                bands.put("3.0 - 4.0", bands.get("3.0 - 4.0") + 1);
            }
        }
        return bands;
    }

    public Map<String, double[]> getProgrammeSummary() {
        Map<String, List<Double>> map = new LinkedHashMap<>();
        for (Student s : repo.findAll()) {
            if (!map.containsKey(s.getProgramme())) {
                map.put(s.getProgramme(), new ArrayList<>());
            }
            map.get(s.getProgramme()).add(s.getGpa());
        }
        Map<String, double[]> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<Double>> e : map.entrySet()) {
            double avg = e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            result.put(e.getKey(), new double[]{e.getValue().size(), avg});
        }
        return result;
    }
}
