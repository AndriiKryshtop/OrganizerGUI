package ua.sumdu.j2se.kryshtop.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import ua.sumdu.j2se.kryshtop.tasks.model.ArrayTaskList;
import ua.sumdu.j2se.kryshtop.tasks.model.Task;
import ua.sumdu.j2se.kryshtop.tasks.model.TaskList;
import ua.sumdu.j2se.kryshtop.tasks.util.TaskIO;

public class MainApp extends Application {
    private File binFile = new File("/files/tasks.bin");

    private File txtFile = new File("/files/tasks.txt");

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
        TaskList arrayTaskList = new ArrayTaskList();

        taskData.forEach(arrayTaskList::add);

        try {
            TaskIO.writeBinary(arrayTaskList, binFile);
        } catch (IOException writeBinException) {
            System.out.println("File not found!");
            writeBinException.printStackTrace(); //TODO: print to LOG
            try {
                binFile.createNewFile();
            } catch (IOException createBinException) {
                createBinException.printStackTrace(); //TODO: print to LOG

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Can't to create new file!");
                alert.setContentText("Can't to create new file. You can see a details in log.");
                alert.showAndWait();
            }

            try {
                TaskIO.writeBinary(arrayTaskList, binFile);
            } catch (IOException writeBinException2) {
                writeBinException2.printStackTrace(); //TODO: print to LOG
                Alert binAlert = new Alert(Alert.AlertType.ERROR);
                binAlert.setTitle("Error Dialog");
                binAlert.setHeaderText("Can't write to tasks.bin!");
                binAlert.setContentText("You can see a details in log.");
                binAlert.showAndWait();
            }
        }

        try {
            TaskIO.writeText(arrayTaskList, txtFile);
        } catch (IOException writeTxtException) {
            writeTxtException.printStackTrace(); //TODO: print to LOG
            try {
                txtFile.createNewFile();
            } catch (IOException createTxtException) {
                createTxtException.printStackTrace(); //TODO: print it LOG
                Alert txtAlert = new Alert(Alert.AlertType.ERROR);
                txtAlert.setTitle("Error Dialog");
                txtAlert.setHeaderText("Can't create tasks.txt!");
                txtAlert.setContentText("You can see a details in log.");
                txtAlert.showAndWait();
            }
            try {
                TaskIO.writeText(arrayTaskList, txtFile);
            } catch (IOException writeTxtException2) {
                writeTxtException2.printStackTrace(); //TODO: print to LOG
                Alert txtAlert = new Alert(Alert.AlertType.ERROR);
                txtAlert.setTitle("Error Dialog");
                txtAlert.setHeaderText("Can't write to tasks.txt!");
                txtAlert.setContentText("You can see a details in log.");
                txtAlert.showAndWait();
            }
        }
    }

    @Override
    public void init() {
        TaskList arrayTaskList = new ArrayTaskList();

        try {
            TaskIO.readBinary(arrayTaskList, binFile);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace(); //TODO: print to LOG
            try {
                binFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace(); //TODO: print to log
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("Can't to create tasks.bin!");
                alert.setContentText("Attention! Data gone be saved only in tasks.txt file.");
                alert.showAndWait();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace(); //TODO: print to LOG
            Alert binAlert = new Alert(Alert.AlertType.WARNING);
            binAlert.setTitle("Warning Dialog");
            binAlert.setHeaderText("Can't read from tasks.bin!");
            binAlert.setContentText("You can see a details in log.");
            binAlert.showAndWait();

            if (arrayTaskList.equals(new ArrayTaskList())) {
                try {
                    TaskIO.readText(arrayTaskList, txtFile);
                } catch (FileNotFoundException e) {
                    try {
                        txtFile.createNewFile();
                    } catch (IOException e1) {
                        e1.printStackTrace(); //TODO: print to log
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Warning Dialog");
                        alert.setHeaderText("Can't to create tasks.txt!");
                        alert.setContentText("Attention! Probably data would'n be saved after work!.");
                        alert.showAndWait();
                    }
                } catch (Exception writeTxtException) {
                    writeTxtException.printStackTrace(); //TODO: print to LOG
                    Alert txtAlert = new Alert(Alert.AlertType.WARNING);
                    txtAlert.setTitle("Warning Dialog");
                    txtAlert.setHeaderText("Can't read from tasks.txt!");
                    txtAlert.setContentText("You can see a details in log.");
                    txtAlert.showAndWait();
                }
            }

            taskData.forEach(arrayTaskList::add);
        }
    }

    public static ObservableList<Task> getTaskData() {
        return taskData;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}