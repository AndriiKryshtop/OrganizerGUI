package ua.sumdu.j2se.kryshtop.tasks;

import java.io.File;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.j2se.kryshtop.tasks.model.ArrayTaskList;
import ua.sumdu.j2se.kryshtop.tasks.model.Task;
import ua.sumdu.j2se.kryshtop.tasks.model.TaskList;
import ua.sumdu.j2se.kryshtop.tasks.view.Alerts;
import ua.sumdu.j2se.kryshtop.tasks.util.TaskIO;

public class MainApp extends Application {

    private final File binFile = new File(System.getProperty("user.dir") + "/src/main/resources/files/tasks.bin");

    private final File textFile = new File(System.getProperty("user.dir") + "/src/main/resources/files/tasks.txt");

    private static final ObservableList<Task> taskData = FXCollections.observableArrayList();

    private static Stage primaryStage;

    private static String readFilesErrorMassage;
    
    private static int createFilesIndicator;

    @Override
    public void start(Stage stage) throws Exception {
        showLoadFilesMassage();

        Logger logger = LoggerFactory.getLogger(MainApp.class);
        logger.info("Hello World");

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
        stage.setTitle("Organizer");
        stage.setMinWidth(880);
        stage.setMinHeight(418);
        stage.setScene(new Scene(root));

        stage.setOnCloseRequest((WindowEvent event) -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("");
            alert.setContentText("Are you really want to exit?");

            Optional<ButtonType> result = alert.showAndWait();
            //noinspection OptionalGetWithoutIsPresent
            if (result.get() != ButtonType.OK) {
                event.consume();
            }
        });

        primaryStage = stage;
        primaryStage.show();
    }

    private void showLoadFilesMassage() {
        if (createFilesIndicator == 2) {
            Alerts.showWarningAlert(readFilesErrorMassage + "Attention! Your data won't be saved!");
        } else if (createFilesIndicator == 1) {
            Alerts.showWarningAlert(readFilesErrorMassage);
        } else {
            Alerts.showWarningAlert("Data is loaded successfully.");
        }
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
            writeBinException.printStackTrace(); //TODO: print to LOG
        }

        try {
            TaskIO.writeText(arrayTaskList, textFile);
        } catch (IOException writeTxtException) {
            writeTxtException.printStackTrace(); //TODO: print to LOG
        }
    }

    @Override
    public void init() {
        TaskList arrayTaskList = new ArrayTaskList();

        if (!binFile.exists()) {
            //TODO: print to log
            try {
                if (!binFile.createNewFile()) {
                    readFilesErrorMassage += "Can't to create tasks.bin!\n";
                    createFilesIndicator++;
                }
            } catch (IOException createBinException) {
                createBinException.printStackTrace(); //TODO: print to log
                readFilesErrorMassage += "Can't to create tasks.bin!\n";
                createFilesIndicator++;
            }
        } else {
            try {
                TaskIO.readBinary(arrayTaskList, binFile);
            } catch (Exception readBinException) {
                readBinException.printStackTrace(); //TODO: print to log
                readFilesErrorMassage += "Can't to read tasks.bin!\n";
                createFilesIndicator++;
            }
        }

        if (arrayTaskList.equals(new ArrayTaskList())) {
            if(!textFile.exists()){
                //TODO: print to log
                try {
                    if (!textFile.createNewFile()) {
                        readFilesErrorMassage += "Can't to create tasks.txt!\n";
                        createFilesIndicator++;
                    }
                } catch (IOException txtReadException) {
                    txtReadException.printStackTrace(); //TODO: print to log
                    readFilesErrorMassage += "Can't to create tasks.txt!\n";
                    createFilesIndicator++;
                }
            } else {
                try {
                    TaskIO.readText(arrayTaskList, textFile);
                } catch (Exception readTxtException) {
                    readTxtException.printStackTrace(); //TODO: print to log
                    readFilesErrorMassage += "Can't read tasks.txt!\n";
                    createFilesIndicator++;
                }
            }
        }

        for (Task task : arrayTaskList) {
            getTaskData().add(task);
        }
    }

    public static ObservableList<Task> getTaskData() {
        return taskData;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}