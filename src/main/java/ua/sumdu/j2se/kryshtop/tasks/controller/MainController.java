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
import javafx.stage.Stage;
import ua.sumdu.j2se.kryshtop.tasks.MainApp;
import ua.sumdu.j2se.kryshtop.tasks.model.Task;
import ua.sumdu.j2se.kryshtop.tasks.view.Alerts;

import java.io.IOException;
import java.util.Optional;

@SuppressWarnings({"CanBeFinal", "unused"})
public class MainController {
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
                    Scene scene = new Scene(root, 712, 405);

                    Stage stage = new Stage();

                    stage.setOnCloseRequest(we -> {
                        stage.close();
                        MainApp.getPrimaryStage().show();
                    });

                    stage.setTitle("Calendar");
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.show();
                } catch (IOException exception) {
                    exception.printStackTrace(); //TODO: print to log
                    Alerts.showErrorAlert("Could not find file Calendar.fxml! \n You can watch details in log.");
                    MainApp.getPrimaryStage().close();
                }

                MainApp.getPrimaryStage().close();
            }
        });

        addNewTaskButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                AddEditController.taskId = -1;
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/AddEdit.fxml"));
                    Parent root = fxmlLoader.load();
                    Scene scene = new Scene(root, 480, 334);

                    Stage stage = new Stage();

                    stage.setOnCloseRequest(windowEvent -> {
                        stage.close();
                        MainApp.getPrimaryStage().show();
                    });

                    stage.setTitle("Add new task");
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.show();
                } catch (IOException exception) {
                    exception.printStackTrace(); //TODO: print to log

                    Alerts.showErrorAlert("Could not find file AddEdit.fxml! \n You can watch details in log.");

                    MainApp.getPrimaryStage().close();
                }

                MainApp.getPrimaryStage().close();
            }
        });

        editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mainTable.getSelectionModel().getSelectedIndex() != -1) {
                    AddEditController.taskId = mainTable.getSelectionModel().getSelectedIndex();
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/AddEdit.fxml"));
                        Parent root = fxmlLoader.load();
                        Scene scene = new Scene(root, 480, 334);

                        Stage stage = new Stage();

                        AddEditController.taskId = mainTable.getSelectionModel().getSelectedIndex();

                        stage.setOnCloseRequest(we -> {
                            stage.close();
                            MainApp.getPrimaryStage().show();
                        });

                        stage.setTitle("Edit task");
                        stage.setScene(scene);
                        stage.setResizable(false);
                        stage.show();
                    } catch (IOException exception) {
                        exception.printStackTrace(); //TODO: print to log

                        Alerts.showErrorAlert("Could not find file AddEdit.fxml! \n You can watch details in log.");

                        MainApp.getPrimaryStage().close();
                    }

                    MainApp.getPrimaryStage().close();
                } else {
                    Alerts.showInformationAlert("Choose task before pressing \"edit\" button!");
                }

            }
        });

        deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mainTable.getSelectionModel().getSelectedIndex() != -1) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete task");
                alert.setHeaderText("Are you really wont to delete this task?");
                alert.setContentText(
                        MainApp.getTaskData().get(
                                mainTable.getSelectionModel().getSelectedIndex()
                        ).toString()
                );

                Optional<ButtonType> result = alert.showAndWait();
                //noinspection OptionalGetWithoutIsPresent
                if (result.get() == ButtonType.OK) {
                    MainApp.getTaskData().remove(mainTable.getSelectionModel().getSelectedIndex());
                }
            } else {
                Alerts.showInformationAlert("Choose task before pressing \"delete\" button!");
            }
        });
    }
}
