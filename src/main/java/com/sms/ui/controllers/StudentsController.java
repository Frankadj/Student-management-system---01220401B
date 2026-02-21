package com.sms.ui.controllers;

import com.sms.domain.Student;
import com.sms.repository.SQLiteStudentRepository;
import com.sms.service.StudentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class StudentsController {

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student,String> colId, colName, colProg, colEmail, colPhone, colStatus, colDate;
    @FXML private TableColumn<Student,Integer> colLevel;
    @FXML private TableColumn<Student,Double> colGpa;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterProgramme, filterStatus;
    @FXML private ComboBox<Integer> filterLevel;

    private final StudentService service = new StudentService(new SQLiteStudentRepository());

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colProg.setCellValueFactory(new PropertyValueFactory<>("programme"));
        colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        colGpa.setCellValueFactory(new PropertyValueFactory<>("gpa"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));
        filterLevel.setItems(FXCollections.observableArrayList(100,200,300,400,500,600,700));
        filterStatus.setItems(FXCollections.observableArrayList("Active","Inactive"));
        loadProgrammes();
        loadTable(service.getAllStudents());
    }

    private void loadProgrammes() {
        List<String> progs = service.getAllStudents().stream()
                .map(Student::getProgramme).distinct().sorted().toList();
        filterProgramme.setItems(FXCollections.observableArrayList(progs));
    }

    private void loadTable(List<Student> students) {
        studentTable.setItems(FXCollections.observableArrayList(students));
    }

    @FXML
    private void onSearch() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) {
            loadTable(service.getAllStudents());
        } else {
            loadTable(service.search(kw));
        }
    }

    @FXML
    private void onFilter() {
        String prog = filterProgramme.getValue();
        Integer level = filterLevel.getValue();
        String status = filterStatus.getValue();
        loadTable(service.filter(prog, level, status));
    }

    @FXML
    private void onClearFilter() {
        filterProgramme.setValue(null);
        filterLevel.setValue(null);
        filterStatus.setValue(null);
        searchField.clear();
        loadTable(service.getAllStudents());
    }

    @FXML
    private void onSortGpa() {
        loadTable(service.sortByGpa(service.getAllStudents(), false));
    }

    @FXML
    private void onSortName() {
        loadTable(service.sortByName(service.getAllStudents()));
    }

    @FXML
    private void onRefresh() {
        loadTable(service.getAllStudents());
        loadProgrammes();
    }

    @FXML
    private void onAdd() {
        openForm(null);
    }

    @FXML
    private void onEdit() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a student to edit.");
            return;
        }
        openForm(selected);
    }

    @FXML
    private void onDelete() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a student to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete " + selected.getFullName() + "?");
        confirm.setContentText("This action cannot be undone.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            service.deleteStudent(selected.getStudentId());
            onRefresh();
        }
    }

    private void openForm(Student student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sms/ui/StudentForm.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(student == null ? "Add Student" : "Edit Student");
            StudentFormController ctrl = loader.getController();
            ctrl.setStudent(student);
            ctrl.setOnSaved(this::onRefresh);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Warning");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
