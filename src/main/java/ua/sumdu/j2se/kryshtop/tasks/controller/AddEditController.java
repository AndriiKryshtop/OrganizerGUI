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

/**
 * Controller class for Add and edit task dialog
 */
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

    private static final int HOURS_MAX_VALUE = 23;
    private static final int MINUTES_MAX_VALUE = 59;
    private static final int SECONDS_MAX_VALUE = 59;
    private static final int DAYS_MAX_VALUE = 30;

    private static final int DAY_IN_SECONDS = 86400;
    private static final int HOUR_IN_SECONDS = 3600;
    private static final int MINUTE_IN_SECONDS = 60;

    private static final int HOUR_IN_MIL_SECONDS = 3600000;
    private static final int MINUTE_IN_MIL_SECONDS = 60000;

    private static final int HOURS_DEFAULT_VALUE = 12;
    private static final int MINUTES_DEFAULT_VALUE = 30;
    private static final int SECONDS_DEFAULT_VALUE = 30;
    private static final int DAYS_DEFAULT_VALUE = 30;

    @FXML
    public void initialize() {
        initializeElements();

        okButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (!addTask()) {
                return;
            }

            Stage stage = (Stage) okButton.getScene().getWindow();
            stage.close();
        });

        cancelButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });
    }

    private boolean addTask() {
        Task task;

        Date startDate;
        startDate = getDateFromPickerAndSpinners(startDatePicker, startHoursSpinner, startMinutesSpinner);
        Date endDate;
        endDate = getDateFromPickerAndSpinners(endDatePicker, endHoursSpinner, endMinutesSpinner);

        Boolean repeatable;
        repeatable = isRadioButtonChosen(repeat, repeatableRadioButton);
        Boolean active;
        active = isRadioButtonChosen(activity, activeRadioButton);

        //delete spaces in beginning and in the end of the entered title
        String taskTitle = title.getText().trim();

        if (!checkIsEnteredDataCorrect(repeatable, taskTitle, startDate, endDate)) {
            return false;
        }

        if (taskId != -1) {
            MainApp.getTaskData().remove(taskId);
        }

        if (repeatable) {
            task = getRepeatableTaskFromInputFields(taskTitle);
        } else {
            task = getUnrepeatableTaskFromInputFields(taskTitle);
        }

        if (active) {
            task.setActive(true);
        }

        MainApp.getTaskData().add(task);

        notifyObservers();

        return true;
    }

    private boolean checkIsEnteredDataCorrect(boolean repeatable, String taskTitle, Date startDate, Date endDate) {

        //check: is title empty
        if (taskTitle.compareTo("") == 0) {
            Alerts.showInformationAlert("You must enter a title!");
            return false;
        }

        //check: are values in time spinners correct
        if (repeatable) {
            if (!repeatableTimeSpinnersCheck()) return false;
        } else {
            if (!unrepeatableTimeSpinnersCheck()) return false;
        }

        /*
            check: is "start time" or "time" is less then current (real) time
            check: is interval equal zero
        */
        return checkIsTimeCorrect(repeatable, startDate, endDate);
    }

    private boolean checkIsTimeCorrect(boolean repeatable, Date startDate, Date endDate) {
        if (repeatable && checkIsIntervalZero()) {
            return false;
        }

        if (repeatable && startDate.compareTo(endDate) >= 0) {
            Alerts.showInformationAlert("End time can not be earlier than or equal to the start time!");
            return false;
        }

        if (repeatable) {
            if (compareCurrentTimeTo(endDatePicker, endHoursSpinner, endMinutesSpinner, "End time") == -1) {
                return false;
            }
        } else {
            if (compareCurrentTimeTo(timeDatePicker, timeHoursSpinner, timeMinutesSpinner, "Time") == -1) {
                return false;
            }
        }
        return true;
    }

    private boolean checkIsIntervalZero() {
        int intervalDays = getIntFromSpinner(daysSpinner);
        int intervalHours = getIntFromSpinner(hoursSpinner);
        int intervalMinutes = getIntFromSpinner(minutesSpinner);
        int intervalSeconds = getIntFromSpinner(secondsSpinner);

        //check: is interval equals 0
        if (intervalDays == 0
                && intervalHours == 0
                && intervalMinutes == 0
                && intervalSeconds == 0
                ) {
            Alerts.showInformationAlert("Interval can't be 0 if task is repeatable!");
            return true;
        }

        return false;
    }

    @SuppressWarnings({"unchecked", "unused"})
    private void initializeElements() {
        LocalDate now = LocalDate.now();

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

        final Callback<DatePicker, DateCell> endDateCellFactory =
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

        endDatePicker.setDayCellFactory(endDateCellFactory);

        startDatePicker.valueProperty().addListener((observableValue, oldValue, newValue) ->
                endDatePicker.setValue(newValue.plusDays(1)));

        startDatePicker.setValue(now);
        endDatePicker.setValue(now);
        timeDatePicker.setValue(now);

        startHoursSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, HOURS_MAX_VALUE, HOURS_DEFAULT_VALUE));
        startMinutesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, MINUTES_MAX_VALUE, MINUTES_DEFAULT_VALUE));
        endHoursSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, HOURS_MAX_VALUE, HOURS_DEFAULT_VALUE));
        endMinutesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, MINUTES_MAX_VALUE, MINUTES_DEFAULT_VALUE));
        daysSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, DAYS_MAX_VALUE, DAYS_DEFAULT_VALUE));
        hoursSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, HOURS_MAX_VALUE, HOURS_DEFAULT_VALUE));
        minutesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, MINUTES_MAX_VALUE, MINUTES_DEFAULT_VALUE));
        secondsSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, SECONDS_MAX_VALUE, SECONDS_DEFAULT_VALUE));
        timeHoursSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, HOURS_MAX_VALUE, HOURS_DEFAULT_VALUE));
        timeMinutesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, MINUTES_MAX_VALUE, MINUTES_DEFAULT_VALUE));

        repeat.selectedToggleProperty().addListener((observableValue, old_toggle, new_toggle)
                -> repeatToggleGroupHandler());

        repeat.selectToggle(unRepeatableRadioButton);

        if (taskId != -1) {
            substituteData(taskId);
        }

    }

    private void repeatToggleGroupHandler() {
        boolean switchVariable;
        switchVariable = isRadioButtonChosen(repeat, unRepeatableRadioButton);

        /*
        If repeatRadioButton chosen it will make unRepeatable group of elements disable.
        If unRepeatableButton chosen it will make repeatable group of elements disable.
        SwitchVariable according to what radioButton is chosen, will invert argument
        of setDisable method for other group of elements so they gone be disable.
         */
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
    }

    private Task getUnrepeatableTaskFromInputFields(String taskTitle) {
        Date timeDate = Date.from(
                timeDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        timeDate = new Date(timeDate.getTime()
                + getIntFromSpinner(timeHoursSpinner) * HOUR_IN_MIL_SECONDS
                + getIntFromSpinner(timeMinutesSpinner) * MINUTE_IN_MIL_SECONDS
        );

        return new Task(taskTitle, timeDate);
    }

    private boolean isRadioButtonChosen(ToggleGroup toggleGroup, RadioButton radioButton) {
        return toggleGroup.getSelectedToggle() == radioButton;
    }

    private int compareCurrentTimeTo(DatePicker datePicker, Spinner hoursSpinner, Spinner minutesSpinner, String timeName) {
        Date currentTime = new Date();
        Date time = getDateFromPickerAndSpinners(endDatePicker, endHoursSpinner, endMinutesSpinner);

        if (currentTime.compareTo(time) == 1
                && !Alerts.showConfirmationDialog(null,
                timeName +
                        " is earlier than or equal to current (real) time.\n" +
                        "Are you really want to add(edit) this task?")
                ) {

            return -1;
        }

        return 1;
    }

    private Date getDateFromPickerAndSpinners(DatePicker datePicker, Spinner hoursSpinner, Spinner minutesSpinner) {
        return new Date(
                Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()
                        + getIntFromSpinner(hoursSpinner) * HOUR_IN_MIL_SECONDS
                        + getIntFromSpinner(minutesSpinner) * MINUTE_IN_MIL_SECONDS);

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

    private int getIntFromSpinner(Spinner spinner) {
        return Integer.parseInt(spinner.getEditor().getText());
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

            daysSpinner.getEditor().setText(String.valueOf(interval / DAY_IN_SECONDS));
            interval = interval % DAY_IN_SECONDS;

            hoursSpinner.getEditor().setText(String.valueOf(interval / HOUR_IN_SECONDS));
            interval = interval % HOUR_IN_SECONDS;

            minutesSpinner.getEditor().setText(String.valueOf(interval / MINUTE_IN_SECONDS));
            interval = interval % MINUTE_IN_SECONDS;

            secondsSpinner.getEditor().setText(String.valueOf(interval));

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

        hoursSpinner.getEditor().setText(String.valueOf(timeOfStartTime / HOUR_IN_MIL_SECONDS));
        timeOfStartTime = timeOfStartTime % HOUR_IN_MIL_SECONDS;

        minutesSpinner.getEditor().setText(String.valueOf(timeOfStartTime / MINUTE_IN_MIL_SECONDS));
    }

    private boolean unrepeatableTimeSpinnersCheck() {
        try {
            if (getIntFromSpinner(timeHoursSpinner) < 0 ||
                    getIntFromSpinner(timeHoursSpinner) > HOURS_MAX_VALUE) {
                throw new NumberFormatException("time hours spinner");
            }
            if (getIntFromSpinner(timeMinutesSpinner) < 0 ||
                    getIntFromSpinner(timeMinutesSpinner) > MINUTES_MAX_VALUE) {
                throw new NumberFormatException("time minutes spinner");
            }
        } catch (NumberFormatException exception) {
            Alerts.showInformationAlert("You have entered a wrong value in " + exception.getMessage());
            return false;
        }
        return true;
    }

    private boolean repeatableTimeSpinnersCheck() {
        try {
            checkSpinner(startHoursSpinner, HOURS_MAX_VALUE, "start time hours spinner");
            checkSpinner(startMinutesSpinner, MINUTES_MAX_VALUE, "start time minutes spinner");
            checkSpinner(endHoursSpinner, HOURS_MAX_VALUE, "end time hours spinner");
            checkSpinner(endMinutesSpinner, MINUTES_MAX_VALUE, "end time minutes spinner");
            checkSpinner(daysSpinner, 30, "interval days spinner");
            checkSpinner(hoursSpinner, HOURS_MAX_VALUE, "interval hours spinner");
            checkSpinner(minutesSpinner, MINUTES_MAX_VALUE, "interval minutes spinner");
            checkSpinner(secondsSpinner, 59, "interval seconds spinner");
        } catch (NumberFormatException exception) {
            Alerts.showInformationAlert("You have entered a wrong value in one of the time spinners");
            return false;
        }
        return true;
    }

    private void checkSpinner(Spinner spinner, int maxValue, String exceptionMassage) {
        if (getIntFromSpinner(spinner) < 0 ||
                getIntFromSpinner(spinner) > maxValue) {
            throw new NumberFormatException(exceptionMassage);
        }
    }

    private Task getRepeatableTaskFromInputFields(String title) {
        int interval;
        interval = getIntFromSpinner(daysSpinner) * DAY_IN_SECONDS
                + getIntFromSpinner(hoursSpinner) * HOUR_IN_SECONDS
                + getIntFromSpinner(minutesSpinner) * MINUTE_IN_SECONDS
                + getIntFromSpinner(secondsSpinner);

        Date startDate = Date.from(startDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        startDate = new Date(startDate.getTime()
                + getIntFromSpinner(startHoursSpinner) * HOUR_IN_MIL_SECONDS
                + getIntFromSpinner(startMinutesSpinner) * MINUTE_IN_MIL_SECONDS
        );
        endDate = new Date(endDate.getTime()
                + getIntFromSpinner(endHoursSpinner) * HOUR_IN_MIL_SECONDS
                + getIntFromSpinner(endMinutesSpinner) * MINUTE_IN_MIL_SECONDS
        );

        return new Task(title, startDate, endDate, interval);
    }
}
