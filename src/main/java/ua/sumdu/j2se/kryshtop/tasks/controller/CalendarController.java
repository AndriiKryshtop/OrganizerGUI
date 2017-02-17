package ua.sumdu.j2se.kryshtop.tasks.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ua.sumdu.j2se.kryshtop.tasks.MainApp;
import ua.sumdu.j2se.kryshtop.tasks.model.Task;

import java.util.Date;

public class CalendarController {
    private ObservableList<Task> taskData = FXCollections.observableArrayList();

    @FXML
    private Button showButton;

    @FXML
    private Button backButton;

    @FXML
    private Label titleLabel;

    @FXML
    private Label startTimeLabel;

    @FXML
    private Label endTimeLabel;

    @FXML
    private Label intervalLabel;


    @FXML
    private TableView<Task> calendarTable;

    @FXML
    private TableColumn<Task, String> titleColumn;

    @FXML
    private TableColumn<Task, String> timeColumn;

    @FXML
    public void initialize(){
        initData();

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        calendarTable.setItems(taskData);

        //showTaskDetails(null);

        calendarTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showTaskDetails(newValue));

        showButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            //TODO: refresh table
        });

        backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
            MainApp.getPrimaryStage().show();
       });
    }

    private void initData() {
        Task task = new Task("Title", new Date());
        task.setActive(true);

        taskData.add(new Task("Title", new Date()));
        taskData.add(new Task("Title2", new Date()));
        taskData.add(task);
        taskData.add(new Task("Title3", new Date(), new Date(), 100));
    }

    private void showTaskDetails(Task task) {
        if (task != null) {
            // Заполняем метки информацией из объекта task.
            titleLabel.setText(task.getTitle());
            startTimeLabel.setText(task.getStartTime().toString());
            endTimeLabel.setText(task.getEndTime().toString());
            intervalLabel.setText(Integer.toString(task.getRepeatInterval()));

        } else {
            titleLabel.setText("");
            startTimeLabel.setText("");
            endTimeLabel.setText("");
            intervalLabel.setText("");
        }
    }
}
