package ua.sumdu.j2se.kryshtop.tasks.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import ua.sumdu.j2se.kryshtop.tasks.MainApp;
import ua.sumdu.j2se.kryshtop.tasks.model.Task;

import javax.swing.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class AddEditController {
    public static int taskId;

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
    private Spinner startHoursSpinner;

    @FXML
    private Spinner startMinutesSpinner;

    @FXML
    private Spinner endHoursSpinner;

    @FXML
    private Spinner endMinutesSpinner;

    @FXML
    private Spinner timeHoursSpinner;

    @FXML
    private Spinner timeMinutesSpinner;

    @FXML
    private DatePicker timeDatePicker;

    @FXML
    private Spinner daysSpinner;

    @FXML
    private Spinner hoursSpinner;

    @FXML
    private Spinner minutesSpinner;

    @FXML
    private Spinner secondsSpinner;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @FXML
    public void initialize() {
        final Callback<DatePicker, DateCell> dayCellFactory =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item.isBefore(LocalDate.now())) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                };
        startDatePicker.setDayCellFactory(dayCellFactory);
        timeDatePicker.setDayCellFactory(dayCellFactory);

        final Callback<DatePicker, DateCell> startDateCellFactory =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item.isBefore(
                                        startDatePicker.getValue())
                                        ) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                };

        endDatePicker.setDayCellFactory(startDateCellFactory);

        startDatePicker.valueProperty().addListener((observableValue, oldValue, newValue) ->
                endDatePicker.setValue(newValue.plusDays(1)));

        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());
        timeDatePicker.setValue(LocalDate.now());


        startHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        startMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 30));
        endHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        endMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 30));
        daysSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30, 0));
        hoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 1));
        minutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        secondsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        timeHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        timeMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 30));

        repeat.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            boolean switchVariable;
            switchVariable = repeat.getSelectedToggle() != repeatebleRadioButton;

            timeDatePicker.setDisable(!switchVariable);
            timeHoursSpinner.setDisable(!switchVariable);
            timeMinutesSpinner.setDisable(!switchVariable);

            startDatePicker.setDisable(switchVariable);
            endDatePicker.setDisable(switchVariable);
            startHoursSpinner.setDisable(switchVariable);
            startMinutesSpinner.setDisable(switchVariable);
            endHoursSpinner.setDisable(switchVariable);
            endMinutesSpinner.setDisable(switchVariable);
            daysSpinner.setDisable(switchVariable);
            hoursSpinner.setDisable(switchVariable);
            minutesSpinner.setDisable(switchVariable);
            secondsSpinner.setDisable(switchVariable);
        });

        repeat.selectToggle(unRepeatebleRadioButton);

        if (taskId != -1) {
            substituteData(taskId);
        }

        okButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            String string = "";
            int hoursMax = 23;
            int minutesMax = 59;

            if (repeat.getSelectedToggle() == unRepeatebleRadioButton) {
                try {
                    string = "\"time hours spinner\"";
                    if (Integer.parseInt(timeHoursSpinner.getEditor().getText() + "") < 0 ||
                            Integer.parseInt(timeHoursSpinner.getEditor().getText() + "") > hoursMax) {
                        throw new NumberFormatException();
                    }
                    string = "\"time minutes spinner\"";
                    if (Integer.parseInt(timeMinutesSpinner.getEditor().getText() + "") < 0 ||
                            Integer.parseInt(timeMinutesSpinner.getEditor().getText() + "") > minutesMax) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException exception) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("You have entered a wrong value in " + string);
                    alert.showAndWait();
                    return;
                }
            } else {
                try {
                    string = "\"start time hours spinner\"";
                    if (Integer.parseInt(startHoursSpinner.getEditor().getText() + "") < 0 ||
                            Integer.parseInt(startHoursSpinner.getEditor().getText() + "") > hoursMax) {
                        throw new NumberFormatException();
                    }
                    string = "\"start time minutes spinner\"";
                    if (Integer.parseInt(startMinutesSpinner.getEditor().getText() + "") < 0 ||
                            Integer.parseInt(startMinutesSpinner.getEditor().getText() + "") > minutesMax) {
                        throw new NumberFormatException();
                    }
                    string = "\"end time hours spinner\"";
                    if (Integer.parseInt(endHoursSpinner.getEditor().getText() + "") < 0 ||
                            Integer.parseInt(endHoursSpinner.getEditor().getText() + "") > hoursMax) {
                        throw new NumberFormatException();
                    }
                    string = "\"end time minutes spinner\"";
                    if (Integer.parseInt(endMinutesSpinner.getEditor().getText() + "") < 0 ||
                            Integer.parseInt(endMinutesSpinner.getEditor().getText() + "") > minutesMax) {
                        throw new NumberFormatException();
                    }
                    string = "\"interval days spinner\"";
                    if (Integer.parseInt(daysSpinner.getEditor().getText() + "") < 0 ||
                            Integer.parseInt(daysSpinner.getEditor().getText() + "") > 30) {
                        throw new NumberFormatException();
                    }
                    string = "\"interval hours spinner\"";
                    if (Integer.parseInt(hoursSpinner.getEditor().getText() + "") < 0 ||
                            Integer.parseInt(hoursSpinner.getEditor().getText() + "") > hoursMax) {
                        throw new NumberFormatException();
                    }
                    string = "\"interval minutes spinner\"";
                    if (Integer.parseInt(minutesSpinner.getEditor().getText() + "") < 0 ||
                            Integer.parseInt(minutesSpinner.getEditor().getText() + "") > minutesMax) {
                        throw new NumberFormatException();
                    }
                    string = "\"interval seconds spinner\"";
                    if (Integer.parseInt(secondsSpinner.getEditor().getText() + "") < 0 ||
                            Integer.parseInt(secondsSpinner.getEditor().getText() + "") > 59) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException exception) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("You have entered a wrong value in " + string);
                    alert.showAndWait();
                    return;
                }
            }
            if ((repeat.getSelectedToggle() == repeatebleRadioButton) &&
                    (startDatePicker.getValue().compareTo(endDatePicker.getValue()) == 0) &&
                    ((Integer.parseInt(startHoursSpinner.getEditor().getText() + "") >
                            Integer.parseInt(endHoursSpinner.getEditor().getText() + "")) ||
                            (Integer.parseInt(startHoursSpinner.getEditor().getText() + "") ==
                                    Integer.parseInt(endHoursSpinner.getEditor().getText() + "") &&
                                    (Integer.parseInt(startMinutesSpinner.getEditor().getText() + "") >=
                                            Integer.parseInt(endMinutesSpinner.getEditor().getText() + ""))))) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("End time can not be earlier than or equal to the start time!");
                alert.showAndWait();
                return;
            }
            if (repeat.getSelectedToggle() == repeatebleRadioButton) {
                if (new Date().compareTo(new Date(
                        Date.from(startDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()
                                + Integer.parseInt(startHoursSpinner.getEditor().getText() + "") * 3600000
                                + Integer.parseInt(startMinutesSpinner.getEditor().getText() + "") * 60000))
                        == 1) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("Start time can not be earlier than to current time!");
                    alert.showAndWait();
                    return;
                }
            } else {
                if (new Date().compareTo(new Date(
                        Date.from(timeDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()
                                + Integer.parseInt(timeHoursSpinner.getEditor().getText() + "") * 3600000
                                + Integer.parseInt(timeMinutesSpinner.getEditor().getText() + "") * 60000))
                        == 1) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("Time can not be earlier than or equal to current time!");
                    alert.showAndWait();
                    return;
                }
            }

            if (repeat.getSelectedToggle() == repeatebleRadioButton
                    && Integer.parseInt(daysSpinner.getEditor().getText() + "") == 0
                    && Integer.parseInt(hoursSpinner.getEditor().getText() + "") == 0
                    && Integer.parseInt(minutesSpinner.getEditor().getText() + "") == 0
                    && Integer.parseInt(secondsSpinner.getEditor().getText() + "") == 0
                    ) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Interval can't be 0 if task is repeatable!");
                alert.showAndWait();
                return;
            }
            if (title.getText().compareTo("") == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("You must enter a title!");
                alert.showAndWait();
                return;
            }

                Task task;
                final int day = 86400;
                final int hour = 3600;
                final int minute = 60;


                if (taskId != -1) {
                    MainApp.getTaskData().remove(taskId);
                }

                if (repeat.getSelectedToggle() == repeatebleRadioButton) {
                    int interval;
                    interval = Integer.parseInt(daysSpinner.getEditor().getText() + "") * day
                            + Integer.parseInt(hoursSpinner.getEditor().getText() + "") * hour
                            + Integer.parseInt(minutesSpinner.getEditor().getText() + "") * minute
                            + Integer.parseInt(secondsSpinner.getEditor().getText() + "");

                    Date startDate = Date.from(startDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    Date endDate = Date.from(endDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

                    startDate = new Date(startDate.getTime()
                            + Integer.parseInt(startHoursSpinner.getEditor().getText() + "") * hour
                            + Integer.parseInt(startMinutesSpinner.getEditor().getText() + "") * minute
                    );
                    endDate = new Date(startDate.getTime()
                            + Integer.parseInt(endHoursSpinner.getEditor().getText() + "") * hour
                            + Integer.parseInt(endMinutesSpinner.getEditor().getText() + "") * minute
                    );

                    task = new Task(
                            title.getText(), startDate, endDate, interval);
                } else {
                    Date timeDate = Date.from(startDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    timeDate = new Date(timeDate.getTime()
                            + Integer.parseInt(endHoursSpinner.getEditor().getText() + "") * hour
                            + Integer.parseInt(endMinutesSpinner.getEditor().getText() + "") * minute
                    );

                    task = new Task(title.getText(), timeDate);
                }

                if (activity.getSelectedToggle() == activeRadioButton) {
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

    private void substituteData(int taskId) {
        Task task = MainApp.getTaskData().get(taskId);

        title.setText(task.getTitle());
        if (task.isActive()) {
            activity.selectToggle(activeRadioButton);
        } else {
            activity.selectToggle(inActiveRadioButton);
        }

        if (task.isRepeated()) {
            repeat.selectToggle(repeatebleRadioButton);
            startDatePicker.setValue(
                    task.getStartTime().toInstant().atZone(
                            ZoneId.systemDefault()
                    ).toLocalDate()
            );

            setTimeSpinners(task.getStartTime(), startDatePicker, startHoursSpinner, startMinutesSpinner);

            endDatePicker.setValue(
                    task.getEndTime().toInstant().atZone(
                            ZoneId.systemDefault()
                    ).toLocalDate()
            );

            setTimeSpinners(task.getEndTime(), endDatePicker, endHoursSpinner, endMinutesSpinner);

            int interval = task.getRepeatInterval();

            int day = 86400;
            daysSpinner.getEditor().setText(interval / day + "");
            interval = interval % day;


            int hour = 3600;
            hoursSpinner.getEditor().setText(interval / hour + "");
            interval = interval % hour;

            int minute = 60;
            minutesSpinner.getEditor().setText(interval / minute + "");
            interval = interval % minute;

            secondsSpinner.getEditor().setText(interval + "");

        } else {
            repeat.selectToggle(unRepeatebleRadioButton);
            timeDatePicker.setValue(
                    task.getTime().toInstant().atZone(
                            ZoneId.systemDefault()
                    ).toLocalDate()
            );

            setTimeSpinners(task.getTime(), timeDatePicker, timeHoursSpinner, timeMinutesSpinner);
        }
    }

    private void setTimeSpinners(Date date, DatePicker datePicker, Spinner hoursSpinner, Spinner minutesSpinner) {
        long timeOfStartTime = date.getTime()
                - Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime();

        int hour = 3600000;
        hoursSpinner.getEditor().setText(timeOfStartTime / hour + "");
        timeOfStartTime = timeOfStartTime % hour;


        int minute = 60000;
        minutesSpinner.getEditor().setText(timeOfStartTime / minute + "");
    }

}
