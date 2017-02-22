package ua.sumdu.j2se.kryshtop.tasks.view;

import javafx.scene.control.Alert;

public class Alerts {

    public static void showWarningAlert(String contentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
