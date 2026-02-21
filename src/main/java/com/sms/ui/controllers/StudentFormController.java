package com.sms.ui.controllers;

import com.sms.domain.Student;
import com.sms.repository.SQLiteStudentRepository;
import com.sms.service.StudentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class StudentFormController {

    @FXML private TextField fieldId, fieldName, fieldProgramme, fieldGpa, fieldEmail, fieldPhone;
    @FXML private ComboBox<Integer> fieldLevel;
    @FXML private ComboBox<String> fieldStatus;
    @FXML private Label errorLabel;

    private final StudentService service = new StudentService(new SQLiteStudentRepository());
    private Student existingStudent;
    private Runnable onSaved;

    @FXML
    public void initialize() {
        fieldLevel.setItems(FXCollections.observableArrayList(100,200,300,400,500,600,700));
        fieldStatus.setItems(FXCollections.observableArrayList("Active","Inactive"));
        fieldStatus.setValue("Active");
    }

    public void setStudent(Student s) {
        if (s == null) return;
        this.existingStudent = s;
        fieldId.setText(s.getStudentId());
        fieldId.setDisable(true);
        fieldName.setText(s.getFullName());
        fieldProgramme.setText(s.getProgramme());
        fieldLevel.setValue(s.getLevel());
        fieldGpa.setText(String.valueOf(s.getGpa()));
        fieldEmail.setText(s.getEmail());
        fieldPhone.setText(s.getPhoneNumber());
        fieldStatus.setValue(s.getStatus());
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    @FXML
    private void onSave() {
        errorLabel.setText("");
        try {
            Student s = new Student();
            s.setStudentId(fieldId.getText().trim());
            s.setFullName(fieldName.getText().trim());
            s.setProgramme(fieldProgramme.getText().trim());
            s.setLevel(fieldLevel.getValue() == null ? 0 : fieldLevel.getValue());
            s.setGpa(Double.parseDouble(fieldGpa.getText().trim()));
            s.setEmail(fieldEmail.getText().trim());
            s.setPhoneNumber(fieldPhone.getText().trim());
            s.setStatus(fieldStatus.getValue() == null ? "Active" : fieldStatus.getValue());
            s.setDateAdded(existingStudent != null ? existingStudent.getDateAdded() : LocalDate.now());

            if (existingStudent == null) {
                service.addStudent(s);
            } else {
                service.updateStudent(s);
            }

            if (onSaved != null) onSaved.run();
            closeWindow();

        } catch (NumberFormatException e) {
            errorLabel.setText("GPA must be a number.");
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) fieldId.getScene().getWindow()).close();
    }
}
