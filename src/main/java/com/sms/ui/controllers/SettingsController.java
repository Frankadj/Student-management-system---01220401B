package com.sms.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SettingsController {

    @FXML private TextField thresholdField;
    @FXML private Label statusLabel;

    private static double atRiskThreshold = 2.0;

    public static double getAtRiskThreshold() {
        return atRiskThreshold;
    }

    @FXML
    public void initialize() {
        thresholdField.setText(String.valueOf(atRiskThreshold));
    }

    @FXML
    private void onSave() {
        try {
            double val = Double.parseDouble(thresholdField.getText().trim());
            if (val < 0.0 || val > 4.0) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Threshold must be between 0.0 and 4.0.");
                return;
            }
            atRiskThreshold = val;
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Settings saved.");
        } catch (NumberFormatException e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please enter a valid number.");
        }
    }
}
