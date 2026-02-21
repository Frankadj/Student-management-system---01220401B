package com.sms.service;

import com.sms.domain.Student;
import com.sms.util.AppLogger;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CsvService {

    private static final String HEADER = "StudentID,FullName,Programme,Level,GPA,Email,PhoneNumber,DateAdded,Status";

    public void exportStudents(List<Student> students, String filePath) {
        new File("data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println(HEADER);
            for (Student s : students) {
                pw.println(String.join(",",
                        s.getStudentId(), s.getFullName(), s.getProgramme(),
                        String.valueOf(s.getLevel()), String.valueOf(s.getGpa()),
                        s.getEmail(), s.getPhoneNumber(),
                        s.getDateAdded().toString(), s.getStatus()));
            }
            AppLogger.info("Exported " + students.size() + " students to " + filePath);
        } catch (IOException e) {
            AppLogger.error("Export failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public ImportResult importStudents(String filePath, StudentService studentService) {
        int success = 0;
        List<String> errors = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            int row = 1;
            while ((line = br.readLine()) != null) {
                row++;
                String[] parts = line.split(",", -1);
                if (parts.length < 9) {
                    errors.add("Row " + row + ": not enough columns.");
                    continue;
                }
                try {
                    Student s = new Student();
                    s.setStudentId(parts[0].trim());
                    s.setFullName(parts[1].trim());
                    s.setProgramme(parts[2].trim());
                    s.setLevel(Integer.parseInt(parts[3].trim()));
                    s.setGpa(Double.parseDouble(parts[4].trim()));
                    s.setEmail(parts[5].trim());
                    s.setPhoneNumber(parts[6].trim());
                    s.setDateAdded(LocalDate.parse(parts[7].trim()));
                    s.setStatus(parts[8].trim());
                    studentService.addStudent(s);
                    success++;
                } catch (Exception e) {
                    errors.add("Row " + row + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            AppLogger.error("Import failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
        if (!errors.isEmpty()) saveErrorReport(errors);
        AppLogger.info("Import complete. Success: " + success + ", Errors: " + errors.size());
        return new ImportResult(success, errors);
    }

    private void saveErrorReport(List<String> errors) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("data/import_errors.csv"))) {
            pw.println("Error");
            for (String e : errors) pw.println(e);
        } catch (IOException e) {
            AppLogger.error("Could not save error report: " + e.getMessage());
        }
    }

    public record ImportResult(int successCount, List<String> errors) {}
}
