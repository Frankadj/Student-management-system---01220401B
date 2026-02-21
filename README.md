# Student Management System Plus
**Index Number:** 01220401B

## Description
A desktop application for managing student records with JavaFX, SQLite, and JDBC.

## How to Run on Windows
1. Install JDK 17 or later
2. Download JavaFX SDK 21 from https://openjfx.io
3. Unzip JavaFX SDK to C:\javafx\javafx-sdk-21
4. See RUN_VM_OPTIONS.txt for VM options
5. Run: java --module-path "C:\javafx\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml -jar StudentManagementSystemPlus.jar

## How to Build
```
mvn compile
mvn test
mvn package
```

## Features
- Add, edit, delete, search students
- Filter by programme, level, status
- Sort by GPA or name
- Reports: Top Performers, At-Risk, GPA Distribution, Programme Summary
- Import/Export CSV
- SQLite database with validation
- 21 unit tests

## Project Structure
- src/main/java/com/sms/domain - Student model
- src/main/java/com/sms/service - Business logic
- src/main/java/com/sms/repository - Database layer
- src/main/java/com/sms/ui/controllers - JavaFX controllers
- src/main/resources/com/sms/ui - FXML screens
- data/ - Database and exports
- evidence/ - Test output
