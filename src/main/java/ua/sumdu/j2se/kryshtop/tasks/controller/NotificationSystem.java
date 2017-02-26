package ua.sumdu.j2se.kryshtop.tasks.controller;

import javafx.application.Platform;
import ua.sumdu.j2se.kryshtop.tasks.MainApp;
import ua.sumdu.j2se.kryshtop.tasks.model.Task;
import ua.sumdu.j2se.kryshtop.tasks.model.util.Tasks;
import ua.sumdu.j2se.kryshtop.tasks.view.Alerts;

import java.util.*;

public class NotificationSystem implements Observer {

    private static final int MIL_SECONDS_IN_MINUTE = 60000;

    private static final int MIL_SECONDS_IN_SECOND = 1000;

    private static boolean updateIndicator;

    private Map<Date, Set<Task>> scheduledTasks;

    private Date dateMaxValue = new Date(Long.MAX_VALUE);

    private Date alreadyNotifiedDate;

    public void startNotificationSystem() {

        AddEditController.addObserverStatic(this);
        MainController.addObserverStatic(this);

        Runnable task = this::runNotificationThread;

        Thread notificationThread = new Thread(task);
        notificationThread.setDaemon(true);
        notificationThread.start();
    }

    private void runNotificationThread() {
        try {
            //noinspection InfiniteLoopStatement
            while(true) {

                //update scheduled tasks
                scheduledTasks = Tasks.calendar(MainApp.getTaskData(), new Date(), dateMaxValue);

                if (scheduledTasks.isEmpty()){
                    Thread.sleep(10 * MIL_SECONDS_IN_SECOND);
                    continue;
                }

                if (alreadyNotifiedDate != null){
                    scheduledTasks.remove(alreadyNotifiedDate);
                    alreadyNotifiedDate = null;
                }

                updateIndicator = false;

                notifyingUser();
            }
        } catch (InterruptedException exception) {
            exception.printStackTrace(); //TODO: print to log
            Alerts.showErrorAlert("Notification system error. Please restart program to be notified.");
        }

    }

    private void notifyingUser() throws InterruptedException {

        //sort out dates in a map
        for (Date date : scheduledTasks.keySet()) {
            Date notificationPeriodStart = new Date(date.getTime() - MIL_SECONDS_IN_MINUTE);

            String notificationMassage = "";

            //sort tasks of this Date by title
            SortedSet<Task> sortedSet = new TreeSet<>(Comparator.comparing(Task::getTitle));
            sortedSet.addAll(scheduledTasks.get(date));

            //sort out tasks for this Date in a map
            for (Task task : sortedSet) {

                //waite while time(Date) of notification came
                do {
                    Thread.sleep(10 * MIL_SECONDS_IN_SECOND);

                    //check whether the observable objects updated
                    if (updateIndicator) {
                        //restart notification
                        return;
                    }

                } while (new Date().compareTo(notificationPeriodStart) == -1);

                //adding task information to notification massage
                notificationMassage += task.getTitle() + " at " + date + "\n";
            }
            //making effectively final variable for lambda expression below
            String finalNotificationMassage = notificationMassage;

            //show notification in main(javafx) thread
            Platform.runLater(() -> Alerts.showInformationAlert(finalNotificationMassage, "Don't forget!"));

            alreadyNotifiedDate = date;
        }
    }


    @Override
    public void update(Observable observable, Object arg) {
        updateIndicator = true;
    }
}
