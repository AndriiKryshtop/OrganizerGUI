package ua.sumdu.j2se.kryshtop.tasks.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import ua.sumdu.j2se.kryshtop.tasks.MainApp;

public class AddEditController {

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @FXML
    public void initialize(){
       okButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
           Stage stage = (Stage) okButton.getScene().getWindow();
           stage.close();

           //TODO: save to temporary collection
           //TODO: refreshing a table in main view and show it

           MainApp.primaryStage.show();

       });

       cancelButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
           Stage stage = (Stage) cancelButton.getScene().getWindow();
           stage.close();
           MainApp.primaryStage.show();
       });
    }
}
