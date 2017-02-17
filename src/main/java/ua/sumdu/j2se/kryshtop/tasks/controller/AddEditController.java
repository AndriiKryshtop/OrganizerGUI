package ua.sumdu.j2se.kryshtop.tasks.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import ua.sumdu.j2se.kryshtop.tasks.MainApp;
import ua.sumdu.j2se.kryshtop.tasks.model.Task;

import java.time.ZoneId;
import java.util.Date;

public class AddEditController {
    public static int taskId = -1;

    @FXML
    private TextField title;

    @FXML
    private ToggleGroup repeat;

    @FXML
    private ToggleGroup activity;

    @FXML
    private RadioButton activeRadioButton;

    @FXML
    private RadioButton inActiveRadioButton;

    @FXML
    private RadioButton repeatebleRadioButton;

    @FXML
    private RadioButton unRepeatebleRadioButton;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private DatePicker startTimePicker; //TODO: change to TimePicker

    @FXML
    private DatePicker endTimePicker; //TODO: change to TimePicker

    @FXML
    private DatePicker timeDatePicker;

    @FXML
    private DatePicker timeTimePicker; //TODO: change to TimePicker

    @FXML
    private TextField intervalDays;

    @FXML
    private TextField intervalHours;

    @FXML
    private TextField intervalMinutes;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @FXML
    public void initialize(){
        if(taskId != -1){
            substituteData(taskId);
        }

        repeat.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            if (repeat.getSelectedToggle() == repeatebleRadioButton) {
                timeDatePicker.setEditable(false);
                timeTimePicker.setEditable(false);
            }
            else {
                startTimePicker.setEditable(false);
                endTimePicker.setEditable(false);
                intervalDays.setEditable(false);
                intervalHours.setEditable(false);
                intervalMinutes.setEditable(false);
            }
        });

        okButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            Task task;

            if(taskId != -1){
                MainApp.getTaskData().remove(taskId);
            }

            if(repeat.getSelectedToggle() == repeatebleRadioButton){
                int interval;
                interval = Integer.parseInt(intervalDays.getText() + "") * 86400
                        + Integer.parseInt(intervalHours.getText() + "") * 3600
                        + Integer.parseInt(intervalMinutes.getText() + "") * 60;

                task = new Task(
                        title.getText(),
                        Date.from(startDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        Date.from(endDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        interval
                        ); //TODO: add TimePickers
            } else {
                task = new Task(title.getText(),
                        Date.from(timeDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                        );  //TODO: add TimePicker
            }

            if(activity.getSelectedToggle() == activeRadioButton) {
                task.setActive(true);
            }

            MainApp.getTaskData().add(task);

            Stage stage = (Stage) okButton.getScene().getWindow();
            stage.close();
            MainApp.getPrimaryStage().show();
        });

        cancelButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
           Stage stage = (Stage) cancelButton.getScene().getWindow();
           stage.close();
           MainApp.getPrimaryStage().show();
        });
    }

    public void substituteData(int taskId){
        Task task = MainApp.getTaskData().get(taskId);

        title.setText(task.getTitle());
        if(task.isActive()){
            activeRadioButton.setSelected(true);
        }
        if(task.isRepeated()){
            repeatebleRadioButton.setSelected(true);
            startDatePicker.setValue(
                    task.getStartTime().toInstant().atZone(
                            ZoneId.systemDefault()
                    ).toLocalDate()
            );
            //TODO: set time picker value
            endDatePicker.setValue(
                    task.getEndTime().toInstant().atZone(
                            ZoneId.systemDefault()
                    ).toLocalDate()
            );
            //TODO: set time picker value

            int interval = task.getRepeatInterval();

            int day = 86400;
            if((interval / day) >= 1){
                intervalDays.setText(interval / day + "");
                interval = interval % day;
            }

            int hour = 3600;
            if((interval / hour) >= 1){
                intervalHours.setText(interval / hour + "");
                interval = interval % hour;
            }

            int minute = 60;
            if((interval / minute) >= 1){
                 intervalMinutes.setText(interval / minute + "");
            }
        } else {
            unRepeatebleRadioButton.setSelected(true);
            timeDatePicker.setValue(
                    task.getTime().toInstant().atZone(
                            ZoneId.systemDefault()
                    ).toLocalDate()
            );
            //TODO: set time picker value
        }
    }
}
