package ua.sumdu.j2se.kryshtop.tasks.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.sumdu.j2se.kryshtop.tasks.MainApp;
import ua.sumdu.j2se.kryshtop.tasks.model.Task;
import ua.sumdu.j2se.kryshtop.tasks.view.Alerts;

import java.io.IOException;
import java.util.*;

@SuppressWarnings({"CanBeFinal", "unused"})
public class MainController extends Observable {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    private static List<Observer> observers = new ArrayList<>();

    @FXML
    private Button calendarButton;

    @FXML
    private Button addNewTaskButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableView<Task> mainTable;

    @FXML
    private TableColumn<Task, Boolean> activityColumn;

    @FXML
    private TableColumn<Task, String> titleColumn;

    @FXML
    private TableColumn<Task, String> startTimeColumn;

    @FXML
    private TableColumn<Task, String> endTimeColumn;

    @FXML
    private TableColumn<Task, String> intervalColumn;

    @FXML
    public void initialize() {
        activityColumn.setCellValueFactory(callback -> new SimpleBooleanProperty(callback.getValue().isActive()));
        activityColumn.setCellFactory(tableCell -> new CheckBoxTableCell<>());

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        endTimeColumn.setCellValueFactory(callback -> {
            if (callback.getValue().isRepeated()) {
                return new SimpleStringProperty(callback.getValue().getEndTime().toString());
            } else {
                return new SimpleStringProperty("");
            }
        });

        intervalColumn.setCellValueFactory(callback -> new SimpleStringProperty(callback.getValue().getRepeatIntervalString()));

        mainTable.setItems(MainApp.getTaskData());

        calendarButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Calendar.fxml"));
                    Parent root = fxmlLoader.load();

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root, 712, 405));

                    stage.setTitle("Calendar");
                    stage.setResizable(false);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.show();
                } catch (IOException ioException) {
                    logger.error("Can't load Calendar.fxml. Get an exception" + ioException.toString());
                    Alerts.showErrorAlert("Can't load Calendar.fxml! \n You can watch details in log.");
                }
            }
        });

        addNewTaskButton.addEventHandler(
                MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        AddEditController.taskId = -1;
                        try {
                            FXMLLoader fxmlLoader =
                                    new FXMLLoader(getClass().getResource("/fxml/AddEdit.fxml"));

                            Parent root = fxmlLoader.load();

                            Stage stage = new Stage();
                            stage.setScene(new Scene(root, 494, 332));

                            stage.setTitle("Add new task");
                            stage.setResizable(false);
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.show();
                        } catch (IOException ioException) {
                            logger.error("Can't load AddEdit.fxml" + ioException.toString());

                            Alerts.showErrorAlert("Can't load AddEdit.fxml! " +
                                    "\n You can watch details in log.");
                        }
                    }
                });

        editButton.addEventHandler(
                MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mainTable.getSelectionModel().getSelectedIndex() != -1) {
                            AddEditController.taskId = mainTable.getSelectionModel().getSelectedIndex();
                            try {
                                FXMLLoader fxmlLoader =
                                        new FXMLLoader(getClass().getResource("/fxml/AddEdit.fxml"));

                                Parent root = fxmlLoader.load();

                                Stage stage = new Stage();
                                stage.setScene(new Scene(root, 494, 332));

                                AddEditController.taskId = mainTable.getSelectionModel().getSelectedIndex();

                                stage.setTitle("Edit task");
                                stage.setResizable(false);
                                stage.initModality(Modality.APPLICATION_MODAL);
                                stage.show();
                            } catch (IOException ioException) {
                                logger.error("Can't load AddEdit.fxml. Get an exception:\n"
                                        + ioException.toString());

                                Alerts.showErrorAlert("Could not find file AddEdit.fxml! " +
                                        "\n You can watch details in log.");
                            }
                        } else {
                            Alerts.showInformationAlert("Choose task before pressing \"edit\" button!");
                        }

                    }
                });

        deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            int selectedIndex = mainTable.getSelectionModel().getSelectedIndex();

            if (selectedIndex != -1) {
                if (Alerts.showConfirmationDialog("Are you really wont to delete this task?",
                        MainApp.getTaskData().get(selectedIndex).toString())
                        ) {
                    MainApp.getTaskData().remove(selectedIndex);

                    notifyObservers();
                }
            } else {
                Alerts.showInformationAlert("Choose task before pressing \"delete\" button!");
            }
        });
    }

    static void addObserverStatic(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this, true);
        }
    }
}
