package com.sms.ui.controllers;

import com.sms.repository.SQLiteStudentRepository;
import com.sms.service.CsvService;
import com.sms.service.ReportService;
import com.sms.service.StudentService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ImportExportController {

    @FXML private Label resultLabel;

    private final StudentService studentService = new StudentService(new SQLiteStudentRepository());
    private final ReportService reportService   = new ReportService(new SQLiteStudentRepository());
    private final CsvService csvService         = new CsvService();

    @FXML
    private void onExportAll() {
        String path = "data/all_students.csv";
        csvService.exportStudents(studentService.getAllStudents(), path);
        resultLabel.setText("Exported all students to " + path);
    }

    @FXML
    private void onExportTop() {
        String path = "data/top_performers.csv";
        csvService.exportStudents(reportService.getTopPerformers(10, null, null), path);
        resultLabel.setText("Exported top performers to " + path);
    }

    @FXML
    private void onExportAtRisk() {
        String path = "data/at_risk.csv";
        csvService.exportStudents(reportService.getAtRiskStudents(2.0), path);
        resultLabel.setText("Exported at-risk students to " + path);
    }

    @FXML
    private void onImport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select CSV File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files","*.csv"));
        File file = chooser.showOpenDialog(new Stage());
        if (file == null) return;
        try {
            CsvService.ImportResult result = csvService.importStudents(file.getAbsolutePath(), studentService);
            resultLabel.setText("Import complete. Success: " + result.successCount() +
                    ", Errors: " + result.errors().size() +
                    (result.errors().isEmpty() ? "" : "\nError report saved to data/import_errors.csv"));
        } catch (Exception e) {
            resultLabel.setText("Import failed: " + e.getMessage());
        }
    }
}
