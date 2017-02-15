package ua.sumdu.j2se.kryshtop.tasks.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import ua.sumdu.j2se.kryshtop.tasks.MainApp;

public class CalendarController {
    @FXML
    private Button showButton;

    @FXML
    private Button backButton;

    @FXML
    public void initialize(){
        showButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            //TODO: refresh table
        });

        backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
           Stage stage = (Stage) backButton.getScene().getWindow();
           stage.close();
           MainApp.primaryStage.show();
       });
    }
}
