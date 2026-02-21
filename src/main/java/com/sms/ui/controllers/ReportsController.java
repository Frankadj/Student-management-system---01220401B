package com.sms.ui.controllers;

import com.sms.domain.Student;
import com.sms.repository.SQLiteStudentRepository;
import com.sms.service.CsvService;
import com.sms.service.ReportService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Map;

public class ReportsController {

    @FXML private ComboBox<String> reportType;
    @FXML private TextField filterProgramme, thresholdField;
    @FXML private ComboBox<Integer> filterLevel;
    @FXML private TableView<String[]> reportTable;
    @FXML private TableColumn<String[],String> col1, col2, col3, col4;
    @FXML private Label statusLabel;

    private final ReportService reportService = new ReportService(new SQLiteStudentRepository());
    private final CsvService csvService = new CsvService();
    private List<Student> lastStudentResult;

    @FXML
    public void initialize() {
        reportType.setItems(FXCollections.observableArrayList(
                "Top Performers", "At-Risk Students", "GPA Distribution", "Programme Summary"));
        filterLevel.setItems(FXCollections.observableArrayList(100,200,300,400,500,600,700));
        thresholdField.setText("2.0");
        setupColumns();
    }

    private void setupColumns() {
        col1.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        col2.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
        col3.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().length > 2 ? data.getValue()[2] : ""));
        col4.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().length > 3 ? data.getValue()[3] : ""));
    }

    @FXML
    private void onReportTypeChanged() {
        reportTable.getItems().clear();
    }

    @FXML
    private void onRunReport() {
        String type = reportType.getValue();
        if (type == null) { statusLabel.setText("Please select a report type."); return; }
        reportTable.getItems().clear();
        lastStudentResult = null;
        if (type.equals("Top Performers")) {
            runTopPerformers();
        } else if (type.equals("At-Risk Students")) {
            runAtRisk();
        } else if (type.equals("GPA Distribution")) {
            runGpaDistribution();
        } else if (type.equals("Programme Summary")) {
            runProgrammeSummary();
        }
    }

    private void runTopPerformers() {
        col1.setText("ID"); col2.setText("Name"); col3.setText("Programme"); col4.setText("GPA");
        String prog = filterProgramme.getText().trim();
        Integer level = filterLevel.getValue();
        List<Student> results = reportService.getTopPerformers(10, prog, level);
        lastStudentResult = results;
        for (Student s : results) {
            reportTable.getItems().add(new String[]{s.getStudentId(), s.getFullName(),
                    s.getProgramme(), String.valueOf(s.getGpa())});
        }
        statusLabel.setText("Top " + results.size() + " performers shown.");
    }

    private void runAtRisk() {
        col1.setText("ID"); col2.setText("Name"); col3.setText("Programme"); col4.setText("GPA");
        double threshold = 2.0;
        try { threshold = Double.parseDouble(thresholdField.getText().trim()); } catch (NumberFormatException ignored) {}
        List<Student> results = reportService.getAtRiskStudents(threshold);
        lastStudentResult = results;
        for (Student s : results) {
            reportTable.getItems().add(new String[]{s.getStudentId(), s.getFullName(),
                    s.getProgramme(), String.valueOf(s.getGpa())});
        }
        statusLabel.setText(results.size() + " at-risk students found.");
    }

    private void runGpaDistribution() {
        col1.setText("GPA Band"); col2.setText("Count"); col3.setText(""); col4.setText("");
        Map<String,Long> dist = reportService.getGpaDistribution();
        for (Map.Entry<String,Long> e : dist.entrySet()) {
            reportTable.getItems().add(new String[]{e.getKey(), String.valueOf(e.getValue()), "", ""});
        }
        statusLabel.setText("GPA distribution loaded.");
    }

    private void runProgrammeSummary() {
        col1.setText("Programme"); col2.setText("Total Students"); col3.setText("Avg GPA"); col4.setText("");
        Map<String,double[]> summary = reportService.getProgrammeSummary();
        for (Map.Entry<String,double[]> e : summary.entrySet()) {
            reportTable.getItems().add(new String[]{e.getKey(),
                    String.valueOf((int)e.getValue()[0]),
                    String.format("%.2f", e.getValue()[1]), ""});
        }
        statusLabel.setText("Programme summary loaded.");
    }

    @FXML
    private void onExport() {
        if (lastStudentResult == null) {
            statusLabel.setText("Run a student report first before exporting.");
            return;
        }
        String type = reportType.getValue().replace(" ","_").toLowerCase();
        String path = "data/" + type + "_export.csv";
        csvService.exportStudents(lastStudentResult, path);
        statusLabel.setText("Exported to " + path);
    }
}
