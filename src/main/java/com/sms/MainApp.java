package com.sms;

import com.sms.util.AppLogger;
import com.sms.util.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        AppLogger.info("Application started.");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sms/ui/Dashboard.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 700);
        primaryStage.setTitle("Student Management System Plus");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        DatabaseManager.getInstance().close();
        AppLogger.info("Application closed.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
