package ua.sumdu.j2se.kryshtop.tasks;

import java.util.Date;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import ua.sumdu.j2se.kryshtop.tasks.model.Task;

public class MainApp extends Application {

    public static Stage primaryStage;

    private ObservableList<Task> taskList = FXCollections.observableArrayList();

    public MainApp() {
        //for test
        taskList.add(new Task("Title", new Date()));
        taskList.add(new Task("Title2", new Date()));
        //for test

        //TODO: reading from file and adding to observableTaskList
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
        Scene scene = new Scene(root, 760, 418);
        stage.setTitle("Organizer");
        stage.setScene(scene);
        stage.setResizable(false);

        primaryStage = stage;
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}