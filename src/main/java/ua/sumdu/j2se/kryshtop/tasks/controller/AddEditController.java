package ua.sumdu.j2se.kryshtop.tasks.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import ua.sumdu.j2se.kryshtop.tasks.MainApp;
import ua.sumdu.j2se.kryshtop.tasks.model.Task;
import ua.sumdu.j2se.kryshtop.tasks.view.Alerts;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@SuppressWarnings({"CanBeFinal", "unused"})
public class AddEditController extends Observable {
    private static List<Observer> observers = new ArrayList<>();

    static int taskId;

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
    private RadioButton repeatableRadioButton;

    @FXML
    private RadioButton unRepeatableRadioButton;

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

    private static final int hoursMaxValue = 23;
    private static final int minutesMaxValue = 59;

    private static final int dayInSeconds = 86400;
    private static final int hourInSeconds = 3600;
    private static final int minuteInSeconds = 60;

    private static final int hourInMilSeconds = 3600000;
    private static final int minuteInMilSeconds = 60000;

    @SuppressWarnings({"unchecked", "unused"})
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

        startHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, hoursMaxValue, 12));
        startMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, minutesMaxValue, 30));
        endHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, hoursMaxValue, 12));
        endMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, minutesMaxValue, 30));
        daysSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30, 0));
        hoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, hoursMaxValue, 1));
        minutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, minutesMaxValue, 0));
        secondsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        timeHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, hoursMaxValue, 12));
        timeMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, minutesMaxValue, 30));

        repeat.selectedToggleProperty().addListener((observableValue, old_toggle, new_toggle) -> {
            boolean switchVariable;
            switchVariable = repeat.getSelectedToggle() != repeatableRadioButton;

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

        repeat.selectToggle(unRepeatableRadioButton);

        if (taskId != -1) {
            substituteData(taskId);
        }

        okButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            Task task;

            //delete spaces in beginning and in the end of the entered title
            String taskTitle = title.getText().trim();

            //check: is title empty
            if (taskTitle.compareTo("") == 0) {
                Alerts.showInformationAlert("You must enter a title!");
                return;
            }

            //check: are values in time spinners correct
            if (repeat.getSelectedToggle() == unRepeatableRadioButton) {
                if (unrepeatableTimeSpinnersCheck() == -1) return;
            } else {
                if (repeatableTimeSpinnersCheck() == -1) return;
            }

            // check: is end time equal or less then start time
            if ((repeat.getSelectedToggle() == repeatableRadioButton) &&
                    (startDatePicker.getValue().compareTo(endDatePicker.getValue()) == 0) &&
                    ((Integer.parseInt(startHoursSpinner.getEditor().getText() + "") >
                            Integer.parseInt(endHoursSpinner.getEditor().getText() + "")) ||
                            (Integer.parseInt(startHoursSpinner.getEditor().getText() + "") ==
                                    Integer.parseInt(endHoursSpinner.getEditor().getText() + "") &&
                                    (Integer.parseInt(startMinutesSpinner.getEditor().getText() + "") >=
                                            Integer.parseInt(endMinutesSpinner.getEditor().getText() + ""))))) {
                Alerts.showInformationAlert("End time can not be earlier than or equal to the start time!");
                return;
            }

            //check: is interval equals 0
            if (repeat.getSelectedToggle() == repeatableRadioButton
                    && Integer.parseInt(daysSpinner.getEditor().getText() + "") == 0
                    && Integer.parseInt(hoursSpinner.getEditor().getText() + "") == 0
                    && Integer.parseInt(minutesSpinner.getEditor().getText() + "") == 0
                    && Integer.parseInt(secondsSpinner.getEditor().getText() + "") == 0
                    ) {
                Alerts.showInformationAlert("Interval can't be 0 if task is repeatable!");
                return;
            }

            // check: is "start time" or "time" equal or less then current (real) time
            if (repeat.getSelectedToggle() == repeatableRadioButton) {
                if (new Date().compareTo(new Date(
                        Date.from(endDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()
                                + Integer.parseInt(endHoursSpinner.getEditor().getText() + "") * hourInMilSeconds
                                + Integer.parseInt(endMinutesSpinner.getEditor().getText() + "") * minuteInMilSeconds))
                        == 1) {
                    if (!Alerts.showConfirmationDialog(null, "End time is earlier than or equal to current (real) time.\n" +
                            "Are you really want to add(edit) this task?")) {
                        return;
                    }
                }
            } else {
                if (new Date().compareTo(new Date(
                        Date.from(timeDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()
                                + Integer.parseInt(timeHoursSpinner.getEditor().getText() + "") * hourInMilSeconds
                                + Integer.parseInt(timeMinutesSpinner.getEditor().getText() + "") * minuteInMilSeconds))
                        == 1) {
                    if (!Alerts.showConfirmationDialog(null, "Time is earlier than or equal to to current time.\n" +
                            "Are you really want to add(edit) this task?")) {
                        return;
                    }
                }
            }

            if (taskId != -1) {
                MainApp.getTaskData().remove(taskId);
            }

            if (repeat.getSelectedToggle() == repeatableRadioButton) {
                task = getRepeatableTaskFromInputFields(taskTitle);
            } else {
                Date timeDate = Date.from(
                        timeDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

                timeDate = new Date(timeDate.getTime()
                        + Integer.parseInt(timeHoursSpinner.getEditor().getText() + "") * hourInMilSeconds
                        + Integer.parseInt(timeMinutesSpinner.getEditor().getText() + "") * minuteInMilSeconds
                );

                task = new Task(taskTitle, timeDate);
            }

            if (activity.getSelectedToggle() == activeRadioButton) {
                task.setActive(true);
            }

            MainApp.getTaskData().add(task);

            notifyObservers();

            Stage stage = (Stage) okButton.getScene().getWindow();
            stage.close();
        });

        cancelButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
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

    private void substituteData(int taskId) {
        Task task = MainApp.getTaskData().get(taskId);

        title.setText(task.getTitle());
        if (task.isActive()) {
            activity.selectToggle(activeRadioButton);
        } else {
            activity.selectToggle(inActiveRadioButton);
        }

        if (task.isRepeated()) {
            repeat.selectToggle(repeatableRadioButton);
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

            daysSpinner.getEditor().setText(interval / dayInSeconds + "");
            interval = interval % dayInSeconds;

            hoursSpinner.getEditor().setText(interval / hourInSeconds + "");
            interval = interval % hourInSeconds;

            minutesSpinner.getEditor().setText(interval / minuteInSeconds + "");
            interval = interval % minuteInSeconds;

            secondsSpinner.getEditor().setText(interval + "");

        } else {
            repeat.selectToggle(unRepeatableRadioButton);
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

        hoursSpinner.getEditor().setText(timeOfStartTime / hourInMilSeconds + "");
        timeOfStartTime = timeOfStartTime % hourInMilSeconds;

        minutesSpinner.getEditor().setText(timeOfStartTime / minuteInMilSeconds + "");
    }

    private int unrepeatableTimeSpinnersCheck() {
        try {
            if (Integer.parseInt(timeHoursSpinner.getEditor().getText() + "") < 0 ||
                    Integer.parseInt(timeHoursSpinner.getEditor().getText() + "") > hoursMaxValue) {
                throw new NumberFormatException("time hours spinner");
            }
            if (Integer.parseInt(timeMinutesSpinner.getEditor().getText() + "") < 0 ||
                    Integer.parseInt(timeMinutesSpinner.getEditor().getText() + "") > minutesMaxValue) {
                throw new NumberFormatException("time minutes spinner");
            }
        } catch (NumberFormatException exception) {
            Alerts.showInformationAlert("You have entered a wrong value in " + exception.getMessage());
            return -1;
        }
        return 0;
    }

    private int repeatableTimeSpinnersCheck() {
        try {
            if (Integer.parseInt(startHoursSpinner.getEditor().getText() + "") < 0 ||
                    Integer.parseInt(startHoursSpinner.getEditor().getText() + "") > hoursMaxValue) {
                throw new NumberFormatException("start time hours spinner");
            }
            if (Integer.parseInt(startMinutesSpinner.getEditor().getText() + "") < 0 ||
                    Integer.parseInt(startMinutesSpinner.getEditor().getText() + "") > minutesMaxValue) {
                throw new NumberFormatException("start time minutes spinner");
            }
            if (Integer.parseInt(endHoursSpinner.getEditor().getText() + "") < 0 ||
                    Integer.parseInt(endHoursSpinner.getEditor().getText() + "") > hoursMaxValue) {
                throw new NumberFormatException("end time hours spinner");
            }
            if (Integer.parseInt(endMinutesSpinner.getEditor().getText() + "") < 0 ||
                    Integer.parseInt(endMinutesSpinner.getEditor().getText() + "") > minutesMaxValue) {
                throw new NumberFormatException("end time minutes spinner");
            }
            if (Integer.parseInt(daysSpinner.getEditor().getText() + "") < 0 ||
                    Integer.parseInt(daysSpinner.getEditor().getText() + "") > 30) {
                throw new NumberFormatException("interval days spinner");
            }
            if (Integer.parseInt(hoursSpinner.getEditor().getText() + "") < 0 ||
                    Integer.parseInt(hoursSpinner.getEditor().getText() + "") > hoursMaxValue) {
                throw new NumberFormatException("interval hours spinner");
            }
            if (Integer.parseInt(minutesSpinner.getEditor().getText() + "") < 0 ||
                    Integer.parseInt(minutesSpinner.getEditor().getText() + "") > minutesMaxValue) {
                throw new NumberFormatException("interval minutes spinner");
            }
            if (Integer.parseInt(secondsSpinner.getEditor().getText() + "") < 0 ||
                    Integer.parseInt(secondsSpinner.getEditor().getText() + "") > 59) {
                throw new NumberFormatException("interval seconds spinner");
            }
        } catch (NumberFormatException exception) {
            Alerts.showInformationAlert("You have entered a wrong value in one of the time spinners");
            return -1;
        }
        return 0;
    }

    private Task getRepeatableTaskFromInputFields(String title) {
        int interval;
        interval = Integer.parseInt(daysSpinner.getEditor().getText() + "") * dayInSeconds
                + Integer.parseInt(hoursSpinner.getEditor().getText() + "") * hourInSeconds
                + Integer.parseInt(minutesSpinner.getEditor().getText() + "") * minuteInSeconds
                + Integer.parseInt(secondsSpinner.getEditor().getText() + "");

        Date startDate = Date.from(startDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        startDate = new Date(startDate.getTime()
                + Integer.parseInt(startHoursSpinner.getEditor().getText() + "") * hourInMilSeconds
                + Integer.parseInt(startMinutesSpinner.getEditor().getText() + "") * minuteInMilSeconds
        );
        endDate = new Date(endDate.getTime()
                + Integer.parseInt(endHoursSpinner.getEditor().getText() + "") * hourInMilSeconds
                + Integer.parseInt(endMinutesSpinner.getEditor().getText() + "") * minuteInMilSeconds
        );

        return new Task(title, startDate, endDate, interval);
    }
}
