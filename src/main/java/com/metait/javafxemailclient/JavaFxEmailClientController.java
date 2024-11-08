package com.metait.javafxemailclient;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class JavaFxEmailClientController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}