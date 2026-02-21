package com.sms.ui.controllers;

import com.sms.repository.SQLiteStudentRepository;
import com.sms.service.StudentService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private Label totalLabel;
    @FXML private Label activeLabel;
    @FXML private Label inactiveLabel;
    @FXML private Label avgGpaLabel;

    private final StudentService studentService =
            new StudentService(new SQLiteStudentRepository());

    @FXML
    public void initialize() {
        refreshStats();
    }

    public void refreshStats() {
        long total    = studentService.getAllStudents().size();
        long active   = studentService.countActive();
        long inactive = studentService.countInactive();
        double avg    = studentService.averageGpa();
        totalLabel.setText(String.valueOf(total));
        activeLabel.setText(String.valueOf(active));
        inactiveLabel.setText(String.valueOf(inactive));
        avgGpaLabel.setText(String.format("%.2f", avg));
    }

    @FXML
    private void openStudents() {
        openWindow("/com/sms/ui/Students.fxml", "Students");
    }

    @FXML
    private void openReports() {
        openWindow("/com/sms/ui/Reports.fxml", "Reports");
    }

    @FXML
    private void openImportExport() {
        openWindow("/com/sms/ui/ImportExport.fxml", "Import / Export");
    }

    @FXML
    private void openSettings() {
        openWindow("/com/sms/ui/Settings.fxml", "Settings");
    }

    private void openWindow(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(loader.load()));
            stage.show();
            stage.setOnHidden(e -> refreshStats());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
