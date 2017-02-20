package ua.sumdu.j2se.kryshtop.tasks;

import java.util.Optional;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import javafx.stage.WindowEvent;
import ua.sumdu.j2se.kryshtop.tasks.model.Task;

public class MainApp extends Application {

    private static ObservableList<Task> taskData = FXCollections.observableArrayList();

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
        Scene scene = new Scene(root, 880, 418);
        stage.setTitle("Organizer");
        stage.setScene(scene);
        stage.setResizable(false);

        stage.setOnCloseRequest((WindowEvent event) -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("");
            alert.setContentText("Are you really wont to exit?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() != ButtonType.OK) {
                event.consume();
            }
        });

        primaryStage = stage;
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        //TODO: save data from collection into file
    }

    @Override
    public void init() {
        //TODO: read from file to collection
    }

    public static ObservableList<Task> getTaskData() {
        return taskData;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}