package ua.sumdu.j2se.kryshtop.tasks;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.j2se.kryshtop.tasks.controller.NotificationSystem;
import ua.sumdu.j2se.kryshtop.tasks.model.ArrayTaskList;
import ua.sumdu.j2se.kryshtop.tasks.model.Task;
import ua.sumdu.j2se.kryshtop.tasks.model.TaskList;
import ua.sumdu.j2se.kryshtop.tasks.view.Alerts;
import ua.sumdu.j2se.kryshtop.tasks.model.util.TaskIO;

/**
 * Main class
 */
public class MainApp extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class);

    private static final ObservableList<Task> TASK_DATA = FXCollections.observableArrayList();

    private static String readFilesErrorMassage;

    private static int createFilesIndicator = 0;

    private final File binFile = new File(System.getProperty("user.dir") + "/tasks.bin");

    private final File textFile = new File(System.getProperty("user.dir") + "/tasks.txt");

    @Override
    public void start(Stage stage) {
        showLoadFilesMassage();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
            stage.setTitle("Organizer");
            stage.setMinWidth(880);
            stage.setMinHeight(418);
            stage.setScene(new Scene(root));
        } catch (IOException ioException) {
            LOGGER.error("Can't load Main.fxml. Get an exception" + ioException.toString());
            Alerts.showErrorAlert("Can't load Main.fxml! \n You can watch details in log.");
        }

        stage.setOnCloseRequest((event) -> {
            if (!Alerts.showConfirmationDialog(null, "Are you really want to exit?")) {
                event.consume();
            }
        });

        stage.show();

        NotificationSystem notificationSystem = new NotificationSystem();
        notificationSystem.startNotificationSystem();
        LOGGER.info("Notification system started.");
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

        MainApp.TASK_DATA.forEach(arrayTaskList::add);

        try {
            TaskIO.writeBinary(arrayTaskList, binFile);
        } catch (IOException writeBinException) {
            LOGGER.warn("Can't to write into bin file. Get an exception:\n"
                    + writeBinException.toString());
        }

        try {
            TaskIO.writeText(arrayTaskList, textFile);
        } catch (IOException writeTxtException) {
            LOGGER.warn("Can't to write into bin file. Get an exception:\n"
                    + writeTxtException.toString());
        }

        LOGGER.info("Application finished.");
    }

    @Override
    public void init() {
        LOGGER.info("Application started.");

        TaskList arrayTaskList = new ArrayTaskList();

        readBinFile(arrayTaskList);

        if (createFilesIndicator > 0) {
            readTxtFile(arrayTaskList);
        }

        for (Task task : arrayTaskList) {
            MainApp.getTaskData().add(task);
        }
    }

    private void readTxtFile(TaskList arrayTaskList) {
        if (textFile.exists()) {
            try {
                TaskIO.readText(arrayTaskList, textFile);
            } catch (java.lang.Exception readTxtException) {
                LOGGER.warn("Can't read txt file. Get an exception:\n"
                        + readTxtException.toString());

                readFilesErrorMassage += "Can't read tasks.txt!\n";
                createFilesIndicator++;
            }
        } else {
            LOGGER.info("Txt file don't exist.");

            try {
                if (!textFile.createNewFile()) {
                    LOGGER.warn("Can't to create txt file.");
                    readFilesErrorMassage += "Can't to create tasks.txt!\n";
                    createFilesIndicator++;
                }
            } catch (IOException txtReadException) {
                LOGGER.warn("Can't to create txt file. Get an exception:\n"
                        + txtReadException.toString());

                readFilesErrorMassage += "Can't to create tasks.txt!\n";
                createFilesIndicator++;
            }
        }
    }

    private void readBinFile(TaskList arrayTaskList) {

        if (binFile.exists()) {
            try {
                TaskIO.readBinary(arrayTaskList, binFile);
            } catch (IOException readBinException) {
                LOGGER.warn("Can't read bin file. Get an exception:\n"
                        + readBinException.toString());

                readFilesErrorMassage += "Can't to read tasks.bin!\n";
                createFilesIndicator++;
            }
        } else {
            LOGGER.info("Bin file don't exist.");

            try {
                if (!binFile.createNewFile()) {
                    LOGGER.warn("Cant to create bin file.");
                    readFilesErrorMassage += "Can't to create tasks.bin!\n";
                    createFilesIndicator++;
                }
            } catch (IOException createBinException) {
                LOGGER.warn("Can't create bin file. Get an exception:\n"
                        + createBinException.toString());

                readFilesErrorMassage += "Can't to create tasks.bin!\n";
                createFilesIndicator++;
            }
        }

    }

    public static ObservableList<Task> getTaskData() {
        return TASK_DATA;
    }
}