
package ua.sumdu.j2se.kryshtop.tasks.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import ua.sumdu.j2se.kryshtop.tasks.MainApp;
import ua.sumdu.j2se.kryshtop.tasks.model.Task;
import ua.sumdu.j2se.kryshtop.tasks.model.util.Tasks;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Controller class for Calendar dialog
 */
@SuppressWarnings({"CanBeFinal", "unused"})
public class CalendarController {
    private final ObservableList<Task> calendarTaskList = FXCollections.observableArrayList();

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

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
    public void initialize() {
        fromDatePicker.setValue(LocalDate.now());
        toDatePicker.setValue(fromDatePicker.getValue().plusDays(1));

        final Callback<DatePicker, DateCell> dayCellFactory =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item.isBefore(
                                        fromDatePicker.getValue().plusDays(1))
                                        ) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                };
        toDatePicker.setDayCellFactory(dayCellFactory);
        fromDatePicker.valueProperty().addListener((observableValue, oldValue, newValue) ->
                toDatePicker.setValue(newValue.plusDays(1)));

        formCalendarList(Date.from(fromDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(toDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        calendarTable.setItems(calendarTaskList);

        //showTaskDetails(null);

        calendarTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showTaskDetails(newValue));

        showButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent ->
                formCalendarList(Date.from(fromDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        Date.from(toDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())));

        backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        });
    }

    private void formCalendarList(Date fromDate, Date toDate) {
        calendarTaskList.clear();
        SortedMap<Date, Set<Task>> sortedMap = Tasks.calendar(MainApp.getTaskData(), fromDate, toDate);
        for (int i = 0; i < sortedMap.values().size(); i++) {
            for (Map.Entry<Date, Set<Task>> entry : sortedMap.entrySet()) {
                for (Object object : entry.getValue().toArray()) {
                    calendarTaskList.add((Task) object);
                }
            }
        }
    }

    private void showTaskDetails(Task task) {
        if (task == null) {
            titleLabel.setText("");
            startTimeLabel.setText("");
            endTimeLabel.setText("");
            intervalLabel.setText("");
        } else {
            titleLabel.setText(task.getTitle());
            startTimeLabel.setText(task.getStartTime().toString());
            endTimeLabel.setText(task.getEndTime().toString());
            intervalLabel.setText(Integer.toString(task.getRepeatInterval()));

        }
    }
}
