package ua.sumdu.j2se.kryshtop.tasks.controller;

import javafx.application.Platform;
import ua.sumdu.j2se.kryshtop.tasks.MainApp;
import ua.sumdu.j2se.kryshtop.tasks.model.Task;
import ua.sumdu.j2se.kryshtop.tasks.model.util.Tasks;
import ua.sumdu.j2se.kryshtop.tasks.view.Alerts;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public class NotificationSystem {
    private static final int MIL_SECONDS_IN_MINUTE = 60000;

    private static final int MIL_SECONDS_IN_SECOND = 1000;

    public void startNotificationSystem() {
        Runnable task = this::runNotificationThread;

        Thread notificationThread = new Thread(task);
        notificationThread.setDaemon(true);
        notificationThread.start();
    }

    private void runNotificationThread() {
        try {
            Map<Date, Set<Task>> sortedMap = Tasks.calendar(MainApp.getTaskData(), new Date(), new Date(Long.MAX_VALUE));
            for (Date date : sortedMap.keySet()) {
                String string = "";
                for (Task task : sortedMap.get(date)) {
                    do {

                        Thread.sleep(10 * MIL_SECONDS_IN_SECOND);

                    } while (new Date().compareTo(new Date(date.getTime() - MIL_SECONDS_IN_MINUTE)) == -1);
                    string += task.getTitle() + " at " + date + "\n";  //TODO: add sort
                }
                String finalString = string;
                Platform.runLater(() -> Alerts.showInformationAlert(finalString)); //TODO: add header
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
