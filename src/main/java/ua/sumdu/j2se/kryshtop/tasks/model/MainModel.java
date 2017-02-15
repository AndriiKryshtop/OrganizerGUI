package ua.sumdu.j2se.kryshtop.tasks.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileNotFoundException;
import java.util.Date;

public class MainModel {
    private ObservableList<Task> taskList = FXCollections.observableArrayList();

    public MainModel(){
        /*
        ArrayTaskList arrayTaskList;
        try{
            //TODO: read from file
            //arrayTaskList = read()
        } catch(FileNotFoundException e){
            arrayTaskList = new ArrayTaskList();
            //TODO: create this file
            //TODO: print to log
        }

        for (int i = 0; i < arrayTaskList.size(); i++) {
            taskList.add(arrayTaskList.getTask(i));
        }
        */
    }

    public ObservableList<Task> getTaskList(){
        return taskList;
    }
}
