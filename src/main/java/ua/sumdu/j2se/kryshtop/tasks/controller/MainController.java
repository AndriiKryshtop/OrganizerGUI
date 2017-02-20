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

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

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
        initData();

        activityColumn.setCellValueFactory(callback -> new SimpleBooleanProperty(callback.getValue().isActive()));
        activityColumn.setCellFactory(tableCell -> new CheckBoxTableCell<>());

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));

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
                } catch (IOException e) {
                    e.printStackTrace();
                    //TODO: add some logic - giving an error massage and print to log
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
                    exception.printStackTrace();
                    //TODO: add some logic - giving an error massage and print to log
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
                    } catch (IOException e) {
                        e.printStackTrace();
                        //TODO: add some logic - giving an error massage and print to log
                    }

                    MainApp.getPrimaryStage().close();
                } else {
                    chooseTaskDialog("edit");
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
                if (result.get() == ButtonType.OK) {
                    MainApp.getTaskData().remove(mainTable.getSelectionModel().getSelectedIndex());
                }
            } else {
                chooseTaskDialog("delete");
            }
        });
    }

    private void initData() {
        //for test
        Task task = new Task("Title", new Date());
        task.setActive(true);

        MainApp.getTaskData().add(new Task("Task that gone be deleted", new Date()));
        MainApp.getTaskData().add(new Task("Title2", new Date()));
        MainApp.getTaskData().add(task);
        MainApp.getTaskData().add(new Task("Title3", new Date(), new Date(), 100));
        //for test
    }

    private void chooseTaskDialog(String button) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Choose task before pressing \"" + button + "\" button!");

        alert.showAndWait();
    }
}
